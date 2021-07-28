package io.github.archeox.lgunenuit.roles;

import io.github.archeox.lgunenuit.game.LGGame;
import reactor.core.publisher.Mono;

public interface Noctambule {

    public float getTurn();
    public Mono<Void> nightAction(LGGame game);

}
