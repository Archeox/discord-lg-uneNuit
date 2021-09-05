package io.github.archeox.lgunenuit.roles;

import discord4j.core.object.reaction.ReactionEmoji;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import io.github.archeox.lgunenuit.roles.core.Noctambule;
import io.github.archeox.lgunenuit.enums.Team;
import reactor.core.publisher.Mono;

public class Sbire extends LGRole implements Noctambule {

    public float turn = 3.0f;

    public Sbire(String name, String description, ReactionEmoji emoji, Team team) {
        super(name, description, emoji, team);
    }

    @Override
    public float getTurn() {
        return turn;
    }

    @Override
    public Mono<Void> nightAction(LGGame game, PlayerCard self) {
        return game.nextTurn();
    }
}
