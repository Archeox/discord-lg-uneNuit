package io.github.archeox.lgunenuit.roles;

import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.Utility.Team;
import reactor.core.publisher.Mono;

public class Noiseuse extends LGRole implements Noctambule{

    private final float turn;

    public Noiseuse(float turn) {
        super("Noiseuse",
                "La noiseuse peut échanger les rôles de deux autres joueurs pendant la nuit",
                Team.VILLAGE
        );

        this.turn = turn;
    }

    @Override
    public float getTurn() {
        return this.turn;
    }

    @Override
    public Mono<Void> nightAction(LGGame game) {
        return lgGame -> {
            return null;
        };
    }
}
