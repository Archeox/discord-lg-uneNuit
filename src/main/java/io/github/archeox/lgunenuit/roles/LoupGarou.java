package io.github.archeox.lgunenuit.roles;

import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import io.github.archeox.lgunenuit.roles.core.Noctambule;
import io.github.archeox.lgunenuit.enums.Team;
import reactor.core.publisher.Mono;

public class LoupGarou extends LGRole implements Noctambule {

    private float turn;

    public LoupGarou(float turn) {
        super("Loup-Garou", "Le but du Loup-Garou est d'éliminer un membre du village.\n" +
                "Il peut de savoir qui sont les autres Loups-Garous. S'il n'y en a pas, il a le droit de regarder une carte au milieu."
                , Team.LG);
        this.turn = turn;
    }

    @Override
    public float getTurn() {
        return this.turn;
    }

    @Override
    public Mono<Void> nightAction(LGGame game, PlayerCard self) {
        return game.nextTurn();
    }
}
