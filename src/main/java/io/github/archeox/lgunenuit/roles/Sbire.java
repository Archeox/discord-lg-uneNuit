package io.github.archeox.lgunenuit.roles;

import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.roles.interfaces.Noctambule;
import io.github.archeox.lgunenuit.utility.Team;

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
    public void nightAction(LGGame game, PlayerCard self) {
        game.nextTurn().subscribe();
    }
}
