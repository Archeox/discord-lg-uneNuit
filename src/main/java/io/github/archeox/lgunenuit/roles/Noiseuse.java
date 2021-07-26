package io.github.archeox.lgunenuit.roles;

import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.Utility.Team;
import reactor.core.publisher.Mono;

public class Noiseuse extends LGRole {


    public Noiseuse() {
        super("Noiseuse",
                "La noiseuse peut échanger les rôles de deux autres joueurs pendant la nuit",
                9,
                Team.VILLAGE
        );
    }

    @Override
    public Mono<Void> action(LGGame game) {
        throw new RuntimeException();
    }
}
