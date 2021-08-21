package io.github.archeox.lgunenuit.config;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.presence.Status;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.ApplicationCommandOptionType;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * A class that manages an array of LGGames and the configuration of new ones.
 */
public class LGGameManager {

    private final HashMap<TextChannel, LGGame> games;
    private List<Class<? extends LGRole>> roleList;

    public LGGameManager() {
        this.games = new HashMap<>();
    }

    public void initalize(){

        //on enregistre les différentes commandes
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
    }

    private Mono<Void> sendConfig(TextChannel channel, Member author){


        return channel.createMessage(
                messageCreateSpec -> {

                }
        ).then();
    }

    public void finishGame(TextChannel channel){
        games.remove(channel);
    }
}
