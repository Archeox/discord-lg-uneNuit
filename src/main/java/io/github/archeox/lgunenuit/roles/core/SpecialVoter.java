package io.github.archeox.lgunenuit.roles.core;

import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.game.Vote;
import reactor.core.publisher.Mono;

public interface SpecialVoter {

    public float getPriority();

    public Mono<Void> voteAction(LGGame game, PlayerCard self, Vote vote);
}
