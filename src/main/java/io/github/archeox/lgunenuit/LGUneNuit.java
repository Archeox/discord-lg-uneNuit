package io.github.archeox.lgunenuit;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.interactions.ButtonInteractHandler;
import io.github.archeox.lgunenuit.interactions.SelectMenuInteractHandler;

public class LGUneNuit {

    public final static SelectMenuInteractHandler MENU_INTERACT_HANDLER = new SelectMenuInteractHandler();
    public final static ButtonInteractHandler BUTTON_INTERACT_HANDLER = new ButtonInteractHandler();
    private static GatewayDiscordClient CLIENT;

    public static void main(String[] args) {

        //on connecte le bot
        CLIENT = DiscordClientBuilder.create(args[0])
                .build()
                .login()
                .block();
        //login message
        CLIENT.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                    final User self = event.getSelf();
                    System.out.println(String.format(
                            "\u001B[32mLogged in as %s#%s\u001B[0m", self.getUsername(), self.getDiscriminator()
                    ));
                });

        //on initialise les listeners
        MENU_INTERACT_HANDLER.initalize(CLIENT);
        BUTTON_INTERACT_HANDLER.initalize(CLIENT);

        String guildId = "868161907771711498";


        LGGame game = new LGGame(
                CLIENT.getChannelById(Snowflake.of("868161911005540444"))
                        .cast(TextChannel.class)
                        .block()
        );

        game.testGame(CLIENT.getMemberById(Snowflake.of(868161907771711498L) ,Snowflake.of(443421769383280650L)).block()).subscribe();

//        PlayerCard player = new PlayerCard(
//                client.getMemberById(Snowflake.of("868161907771711498"), Snowflake.of(443421769383280650l)).block(),
//                new Noiseuse(9)
//        );

//        ((Noctambule) player.getAttributedRole()).nightAction(null, player).subscribe();

        //on déconnecte le bot
        CLIENT.onDisconnect().block();
    }

}
