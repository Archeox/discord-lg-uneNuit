package io.github.archeox.lgunenuit.roles;

import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import io.github.archeox.lgunenuit.roles.core.Noctambule;
import io.github.archeox.lgunenuit.enums.Team;
import reactor.core.publisher.Mono;

public class Sbire extends LGRole implements Noctambule {

    public final int turn;

    public Sbire(int turn) {
        super("Sbire", "Le Sbire fait équipe avec les loups-garous et sait qui ils sont." +
                "\nSi le Sbire est éliminé les loups-garou gagnent !", Team.SBIRE);
        this.turn = turn;
    }

    @Override
    public float getTurn() {
        return 0;
    }

    @Override
    public Mono<Void> nightAction(LGGame game, PlayerCard self) {
        return game.nextTurn();
    }
}
