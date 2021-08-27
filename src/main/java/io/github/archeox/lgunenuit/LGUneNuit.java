package io.github.archeox.lgunenuit;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import io.github.archeox.lgunenuit.config.LGGameManager;
import io.github.archeox.lgunenuit.interactions.ButtonInteractHandler;
import io.github.archeox.lgunenuit.interactions.SelectMenuInteractHandler;
import io.github.archeox.lgunenuit.interactions.SlashCommandHandler;

public class LGUneNuit {
    
    private String test;

    public final static SelectMenuInteractHandler MENU_INTERACT_HANDLER = new SelectMenuInteractHandler();
    public final static ButtonInteractHandler BUTTON_INTERACT_HANDLER = new ButtonInteractHandler();
    public final static SlashCommandHandler SLASH_COMMAND_HANDLER = new SlashCommandHandler();
    public final static LGGameManager GAME_MANAGER = new LGGameManager();
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
        SLASH_COMMAND_HANDLER.intialize(CLIENT);
        GAME_MANAGER.initalize();


//        Guild guild = CLIENT.getGuildById(Snowflake.of("868161907771711498")).block();
//        List<Member> memberList = guild.getMembers()
//                .filter(member -> !member.isBot())
//                .filter(member -> (member.getPresence().block().getStatus().equals(Status.ONLINE) || member.getPresence().block().getStatus().equals(Status.DO_NOT_DISTURB)))
//                .collectList()
//                .block();
//        for (Member member : memberList) {
//            System.out.println(member.getDisplayName());
//        }
//
//        LGGame game = new LGGame(
//                memberList,
//                Arrays.asList(new Noiseuse(8), new Noiseuse(2)),
//                CLIENT.getChannelById(Snowflake.of(868161911005540444L)).cast(TextChannel.class).block()
//        );
//
//        game.startGame().subscribe();

//        PlayerCard player = new PlayerCard(
//                client.getMemberById(Snowflake.of("868161907771711498"), Snowflake.of(443421769383280650l)).block(),
//                new Noiseuse(9)
//        );

//        ((Noctambule) player.getAttributedRole()).nightAction(null, player).subscribe();

        //on déconnecte le bot
        CLIENT.onDisconnect().block();
    }

}
