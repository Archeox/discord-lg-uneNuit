package io.github.archeox.lgunenuit.roles;

import discord4j.core.event.domain.interaction.SelectMenuInteractEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.utility.Team;
import io.github.archeox.lgunenuit.game.LGPlayer;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
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

//        List<LGPlayer> playerList = game.getMembersPlayers();
//        playerList.remove(self);

        System.out.println("NOISEUSE");

//        List<SelectMenu.Option> options = new ArrayList<>();
//        for (LGPlayer player : playerList) {
//            options.add(SelectMenu.Option.of(player.getMember().getDisplayName(), player.getId().toString()));
//        }

        List<SelectMenu.Option> options = Arrays.asList(SelectMenu.Option.of("option 1", "foo"),
                SelectMenu.Option.of("option 2", "bar"),
                SelectMenu.Option.of("option 3", "baz"));

        return self.menuInteraction(messageCreateSpec -> {
                    messageCreateSpec.setContent("Veuillez choisir deux joueurs :");
                    messageCreateSpec.setComponents(ActionRow.of(SelectMenu.of(self.getId().toString(), options)
                            .withMaxValues(2)
                            .withMinValues(2)
                    ));
                }, selectMenuInteractEvent ->
                selectMenuInteractEvent.acknowledge()
                        .then(selectMenuInteractEvent.getMessage().delete())
                        .then(self.whisper(selectMenuInteractEvent.getValues().toString()))
                        .then(self.whisper("Choix enregistrés !").then())
        );
    }
}
