package io.github.archeox.lgunenuit.roles;

import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.LGPlayer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Noctambule {

    public float getTurn();
    public Mono<Void> nightAction(LGGame game, LGPlayer self);

}
