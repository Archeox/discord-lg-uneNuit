package io.github.archeox.lgunenuit;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.SelectMenuInteractEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommand;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.presence.Status;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.rest.RestClient;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.LGPlayer;
import io.github.archeox.lgunenuit.roles.Noctambule;
import io.github.archeox.lgunenuit.roles.Noiseuse;
import io.github.archeox.lgunenuit.roles.Villageois;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class LGUneNuit {

    public static GatewayDiscordClient client;

    public static void main(String[] args) {

        //on connecte le bot
        client = DiscordClientBuilder.create(args[0])
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

        //listener d'interaction

        String guildId = "868161907771711498";


        LGGame game = new LGGame(
                client.getChannelById(Snowflake.of("868161911005540444"))
                        .cast(TextChannel.class)
                        .block()
        );

        game.testGame(client.getMemberById(Snowflake.of(868161907771711498L) ,Snowflake.of(443421769383280650L)).block()).subscribe();

//        LGPlayer player = new LGPlayer(
//                client.getMemberById(Snowflake.of("868161907771711498"), Snowflake.of(443421769383280650l)).block(),
//                new Noiseuse(9)
//        );

//        ((Noctambule) player.getAttributedRole()).nightAction(null, player).subscribe();

        //on déconnecte le bot
        client.onDisconnect().block();
    }

}
