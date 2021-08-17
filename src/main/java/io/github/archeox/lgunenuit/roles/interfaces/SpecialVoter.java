package io.github.archeox.lgunenuit.roles.interfaces;

import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.utility.Vote;

public interface SpecialVoter {

    public float getPriority();

    public void voteAction(LGGame game, PlayerCard self, Vote vote);
}
