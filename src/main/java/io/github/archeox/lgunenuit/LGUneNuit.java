package io.github.archeox.lgunenuit;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.command.ApplicationCommand;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.rest.RestClient;
import reactor.core.publisher.Flux;

public class LGUneNuit {

    public static void main(String[] args) {

        //on connecte le bot
        GatewayDiscordClient client = DiscordClientBuilder.create(args[0])
                .build()
                .login()
                .block();

        String guildId = "868161907771711498";
        RestClient restClient = client.getRestClient();

        //on détruit les commandes slashs
        deleteSlashCommands(restClient, guildId);

        //on enregistre les commandes slashs


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
