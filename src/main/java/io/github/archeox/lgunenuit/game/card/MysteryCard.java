package io.github.archeox.lgunenuit.game.card;

import io.github.archeox.lgunenuit.roles.core.LGRole;

public class MysteryCard extends LGCard {

    private final String mystery;

    public MysteryCard(String mystery, LGRole role) {
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
