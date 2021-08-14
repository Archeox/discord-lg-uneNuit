package io.github.archeox.lgunenuit.game;

import io.github.archeox.lgunenuit.game.card.PlayerCard;

public interface Noctambule {

    public float getTurn();
    public void nightAction(LGGame game, PlayerCard self);

}
