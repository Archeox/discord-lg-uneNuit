package io.github.archeox.lgunenuit.game.card;

import io.github.archeox.lgunenuit.game.LGRole;

public class MysteryCard extends LGCard{

    private final String mystery;

    public MysteryCard(LGRole role, String mystery) {
        super(role);
        this.mystery = mystery;
    }

    public String getMystery() {
        return mystery;
    }

    @Override
    public String toString() {
        return mystery;
    }
}
