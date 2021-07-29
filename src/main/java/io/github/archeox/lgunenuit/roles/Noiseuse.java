package io.github.archeox.lgunenuit.roles;

import discord4j.core.event.domain.interaction.SelectMenuInteractEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.utility.Team;
import io.github.archeox.lgunenuit.game.LGPlayer;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class Noiseuse extends LGRole implements Noctambule {

    private final float turn;

    public Noiseuse(float turn) {
        super("Noiseuse",
                "La noiseuse peut échanger les rôles de deux autres joueurs pendant la nuit.",
                Team.VILLAGE
        );

        this.turn = turn;
    }

    @Override
    public float getTurn() {
        return this.turn;
    }

    @Override
    public Mono<Void> nightAction(LGGame game, LGPlayer self) {

        List<LGPlayer> playerList = game.getMembersPlayers();
        playerList.remove(self);

        System.out.println("NOISEUSE");

        List<SelectMenu.Option> options = new ArrayList<>();
        for (LGPlayer player : playerList) {
            options.add(SelectMenu.Option.of(player.getMember().getDisplayName(), player.getId().toString()));
        }

        return self.getMember().getPrivateChannel()
                .flatMap(privateChannel -> privateChannel.createMessage(
                        msg -> {
                            msg.setContent("C'est à vous de jouer !\nChoisissez deux personnes dont vous voulez échanger le rôle :");
                            msg.setComponents(
                                    ActionRow.of(
                                            SelectMenu.of("Joueurs :", options)
                                                    .withMaxValues(2)
                                    )
                            );
                        }
                ))
                .map(Message::getId)
                .flatMap(snowflake -> {
                     return game.getInteractionManager().menuInteractionRegister(snowflake, selectMenuInteractEvent ->
                            //noiseuse
                            selectMenuInteractEvent.reply("Rôles échangés !"));
                }));
    }
}
