package io.github.archeox.lgunenuit.config;

import discord4j.common.util.Snowflake;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.ApplicationCommandOptionType;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * A class that manages an array of LGGames and the configuration of new ones.
 */
public class LGGameManager {

    private final HashMap<Snowflake, LGGame> games;

    public LGGameManager() {
        this.games = new HashMap<>();
    }

    public void initalize() {

        //Commande pour lancer une partie
        LGUneNuit.SLASH_COMMAND_HANDLER.initializeGuildCommand(868161907771711498L,
                ApplicationCommandRequest.builder()
                        .name("lgunenuit")
                        .description("Permet de lancer une partie de Loup-Garou une Nuit.")
                        .addOption(ApplicationCommandOptionData.builder()
                                .name("salon")
                                .description("Le salon dans lequel la partie se déroulera.")
                                .type(ApplicationCommandOptionType.CHANNEL.getValue())
                                .required(true)
                                .build())
                        .build(),
                slashCommandEvent ->
                        Mono.justOrEmpty(slashCommandEvent.getOption("salon"))
                                .map(ApplicationCommandInteractionOption::getValue)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .flatMap(ApplicationCommandInteractionOptionValue::asChannel)
                                .flatMap(channel -> {
                                    if (channel.getType() == Channel.Type.GUILD_TEXT) {
                                        return slashCommandEvent.replyEphemeral(String.format("Configuration démarrée dans %s", channel.getMention())).log()
                                                .then(sendConfig((TextChannel) channel, slashCommandEvent.getInteraction().getMember().get()));
                                    } else {
                                        return slashCommandEvent.replyEphemeral("Veuillez sélectionner un salon textuel");
                                    }
                                })
        );

        //Commande pour ajouter un joueur
        LGUneNuit.SLASH_COMMAND_HANDLER.initializeGuildCommand(868161907771711498L,
                ApplicationCommandRequest.builder()
                        .name("ajouterjoueur")
                        .description("Ajoute un joueur à la partie crée dans ce salon.")
                        .addOption(ApplicationCommandOptionData.builder()
                                .name("joueur")
                                .description("Le Joueur à ajouter.")
                                .type(ApplicationCommandOptionType.USER.getValue())
                                .required(true)
                                .build()
                        )
                        .build()
                , slashCommandEvent -> {
                    if (games.containsKey(slashCommandEvent.getInteraction().getChannelId())) {

                        Snowflake guildId = slashCommandEvent.getInteraction().getGuildId().get();
                        return Mono.justOrEmpty(slashCommandEvent.getOption("joueur"))
                                .map(ApplicationCommandInteractionOption::getValue)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .flatMap(ApplicationCommandInteractionOptionValue::asUser)
                                .flatMap(user -> user.asMember(guildId))
                                .map(member ->
                                        games.get(slashCommandEvent.getInteraction().getChannelId()).addMember(member)
                                )
                                .then();
                    } else {
                        return slashCommandEvent.replyEphemeral("Pas de partie de ce salon.");
                    }
                });
    }

    private Mono<Void> sendConfig(TextChannel channel, Member author) {

        //on crée la partie
        games.put(channel.getId(), new LGGame(channel, author));

        List<SelectMenu.Option> optionList = new ArrayList<>();
        for (RoleFactory.RoleID roleID : RoleFactory.RoleID.values()) {
            for (int i : roleID.getNumbers()) {
                if (i == 1) {
                    optionList.add(SelectMenu.Option.of(roleID.getName(), roleID.name() + "-" + i)
                            .withDescription(roleID.getDescription())
                            .withEmoji(roleID.getEmoji()));
                } else {
                    String name = roleID.getName() + " (x" + i + ")";
                    optionList.add(SelectMenu.Option.of(name, roleID.name() + "-" + i)
                            .withDescription(roleID.getDescription())
                            .withEmoji(roleID.getEmoji()));
                }
            }
        }

        return Mono.just(optionList)
                .flatMap(options -> channel.createMessage(messageCreateSpec -> {
                    messageCreateSpec.setContent("Veuillez choisir les rôles pour cette partie :");
                    messageCreateSpec.setComponents(
                            ActionRow.of(SelectMenu.of("roleMenu", options)
                                    .withMinValues(3)
                                    .withMaxValues(10)
                            )
                    );
                }))
                .map(Message::getId)
                .map(snowflake -> LGUneNuit.MENU_INTERACT_HANDLER.registerMenuInteraction(snowflake, selectMenuInteractEvent -> {
                            if (games.containsKey(selectMenuInteractEvent.getInteraction().getChannelId())) {
                                LGGame currentGame = games.get(selectMenuInteractEvent.getInteraction().getChannelId());
                                if (selectMenuInteractEvent.getInteraction().getMember().get().equals(currentGame.getOwner())) {
                                    List<LGRole> roles = new ArrayList<>();
                                    for (String value : selectMenuInteractEvent.getValues()) {
                                        String[] valueInfo = value.split("-");
                                        for (int i = 0; i < Integer.parseInt(valueInfo[1]); i++) {
                                            roles.add(RoleFactory.getRole(RoleFactory.RoleID.valueOf(valueInfo[0])));
                                        }
                                    }
                                    currentGame.setRole(roles);
                                    return selectMenuInteractEvent.acknowledge();
                                } else {
                                    return selectMenuInteractEvent.replyEphemeral("Seul l'utilisateur ayant lancé la partie peut modifier la configuration");
                                }
                            } else {
                                return selectMenuInteractEvent.replyEphemeral("Pas de jeu actif ici");
                            }
                        }, false)
                )
                .then();
    }


    public void finishGame(TextChannel channel) {
        games.remove(channel);
    }
}
