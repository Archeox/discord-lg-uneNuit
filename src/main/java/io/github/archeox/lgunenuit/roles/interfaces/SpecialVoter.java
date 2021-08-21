package io.github.archeox.lgunenuit.roles.interfaces;

import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.utility.Vote;
import reactor.core.publisher.Mono;

public interface SpecialVoter {

    public float getPriority();

    public Mono<Void> voteAction(LGGame game, PlayerCard self, Vote vote);
}
