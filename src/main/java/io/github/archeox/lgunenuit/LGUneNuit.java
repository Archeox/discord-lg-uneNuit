package io.github.archeox.lgunenuit;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommand;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.rest.RestClient;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.roles.Noiseuse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class LGUneNuit {

    public static void main(String[] args) {

        //on connecte le bot
        GatewayDiscordClient client = DiscordClientBuilder.create(args[0])
                .build()
                .login()
                .block();
        //login message
        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                    final User self = event.getSelf();
                    System.out.println(String.format(
                            "\u001B[32mLogged in as %s#%s\u001B[0m", self.getUsername(), self.getDiscriminator()
                    ));
                });
        String guildId = "868161907771711498";

        LGGame game = new LGGame(
                client.getGuildById(Snowflake.of(guildId))
                .flatMapMany(Guild::getMembers)
                .collectList()
                .block(),
                Arrays.asList(new Noiseuse(9), new Noiseuse(9)),
                client.getGuildById(Snowflake.of(guildId))
                .flatMapMany(Guild::getChannels)
                .ofType(TextChannel.class)
                .blockFirst()
                );

        game.startGame();

        //on déconnecte le bot
        client.onDisconnect().block();
    }

    private static void deleteSlashCommands(RestClient restClient, String guildIdS) {

        long guildId = Snowflake.asLong(guildIdS);

        restClient.getApplicationService().getGuildApplicationCommands(restClient.getApplicationId().block(), guildId)
                .map(ApplicationCommandData::id).flatMap(s -> {

            return restClient.getApplicationService().deleteGuildApplicationCommand(
                    restClient.getApplicationId().block(),
                    guildId,
                    Snowflake.asLong(s)
            );
        }).subscribe();
    }

    private static void registerSlashCommands(RestClient restClient, Flux<ApplicationCommand> commands) {

    }

}
