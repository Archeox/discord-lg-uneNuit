package io.github.archeox.lgunenuit.roles;

import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.LGPlayer;
import io.github.archeox.lgunenuit.utility.Team;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class Voyante extends LGRole implements Noctambule {

    private final float turn;

    public Voyante(float turn) {
        super("Voyante", "La Voyante peut observer la carte d'un joueur", Team.VILLAGE);
        this.turn = turn;
    }

    @Override
    public float getTurn() {
        return turn;
    }

    @Override
    public Mono<Void> nightAction(LGGame game, LGPlayer self) {
        List<SelectMenu.Option> options = Arrays.asList(SelectMenu.Option.of("option 1", "foo"),
                SelectMenu.Option.of("option 2", "bar"),
                SelectMenu.Option.of("option 3", "baz"));

        return self.menuInteraction(messageCreateSpec -> {
                    messageCreateSpec.setContent("Veuillez choisir un joueur :");
                    messageCreateSpec.setComponents(ActionRow.of(SelectMenu.of(self.getId().toString(), options)
                            .withMaxValues(1)
                            .withMinValues(1)
                    ));
                }, selectMenuInteractEvent ->
                        selectMenuInteractEvent.reply(selectMenuInteractEvent.getValues().toString())
                                .then(self.whisper("Choix enregistrés !"))
                                .then()
        );
    }
}
