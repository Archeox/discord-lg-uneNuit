package io.github.archeox.lgunenuit.config;

import discord4j.common.util.Snowflake;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.presence.Status;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.ApplicationCommandOptionType;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.enums.RoleID;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.spi.AbstractResourceBundleProvider;
import java.util.stream.Collectors;

/**
 * A class that manages an array of LGGames and the configuration of new ones.
 */
public class LGGameManager {

    private final HashMap<Snowflake, LGGame> games;
    private List<Class<? extends LGRole>> roleList;

    public LGGameManager() {
        this.games = new HashMap<>();
    }

    public void initalize(){

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
                                    return slashCommandEvent.replyEphemeral(String.format("Configuration démarrée dans %s", channel.getMention()))
                                            .then(sendConfig((TextChannel) channel, slashCommandEvent.getInteraction().getMember().get()));
                                }else{
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
                ,slashCommandEvent -> {
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

    private Mono<Void> sendConfig(TextChannel channel, Member author){

        //on crée la partie
        games.put(channel.getId(), new LGGame(channel));

        //on crée une option par rôle
        List<SelectMenu.Option> options = new ArrayList<>();
        for (RoleID id : RoleID.values()) {
            options.add(SelectMenu.Option.of(id.getName(), id.name()));
        }

        return channel.createMessage(messageCreateSpec -> {
            messageCreateSpec.setContent("Veuillez choisir les rôles qui seront disponibles durant la partie :\n" +
                    "Pour ajouter des joueurs à la partie, utilisez la commande /addjoueur.");
            messageCreateSpec.setComponents(ActionRow.of(
                    SelectMenu.of("joueurs", options)
                    .withMinValues(4).withMaxValues(Integer.MAX_VALUE)
            ), ActionRow.of(
                    Button.primary("Start","Démarrer la partie !")
            ));
        }).then();
    }

    public void finishGame(TextChannel channel){
        games.remove(channel);
    }
}
