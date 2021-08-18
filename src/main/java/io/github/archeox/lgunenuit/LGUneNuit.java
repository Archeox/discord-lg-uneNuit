package io.github.archeox.lgunenuit;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.presence.Status;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.interactions.ButtonInteractHandler;
import io.github.archeox.lgunenuit.interactions.SelectMenuInteractHandler;
import io.github.archeox.lgunenuit.roles.LoupGarou;
import io.github.archeox.lgunenuit.roles.Noiseuse;
import io.github.archeox.lgunenuit.roles.Villageois;
import io.github.archeox.lgunenuit.roles.Voyante;

import java.util.Arrays;
import java.util.List;

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

        Guild guild = CLIENT.getGuildById(Snowflake.of("868161907771711498")).block();


        List<Member> memberList = guild.getMembers()
                .filter(member -> !member.isBot())
                .filter(member -> member.getPresence().block().getStatus().equals(Status.ONLINE))
                .collectList()
                .block();
        for (Member member : memberList) {
            System.out.println(member.getDisplayName());
        }

        LGGame game = new LGGame(
                memberList,
                Arrays.asList(new Villageois(), new Voyante(7), new Noiseuse(8), new LoupGarou(10), new Villageois()),
                CLIENT.getChannelById(Snowflake.of(868161911005540444L)).cast(TextChannel.class).block()
        );

        game.startGame().subscribe();

//        PlayerCard player = new PlayerCard(
//                client.getMemberById(Snowflake.of("868161907771711498"), Snowflake.of(443421769383280650l)).block(),
//                new Noiseuse(9)
//        );

//        ((Noctambule) player.getAttributedRole()).nightAction(null, player).subscribe();

        //on déconnecte le bot
        CLIENT.onDisconnect().block();
    }

}
