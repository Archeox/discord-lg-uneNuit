package io.github.archeox.lgunenuit.roles.core;

import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import reactor.core.publisher.Mono;

public interface Noctambule {

    public float getTurn();
    public Mono<Void> nightAction(LGGame game, PlayerCard self);

}
