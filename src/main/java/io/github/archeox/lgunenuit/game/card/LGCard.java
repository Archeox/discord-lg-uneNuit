package io.github.archeox.lgunenuit.game.card;

import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import io.github.archeox.lgunenuit.game.LGRole;

import java.util.Objects;
import java.util.UUID;

public abstract class LGCard {

    private final LGRole attributedRole;
    private LGRole role;
    private final UUID id;

    public LGCard(LGRole role) {
        this.role = role;
        this.attributedRole = role;
        this.id = UUID.randomUUID();
    }

    public LGRole getAttributedRole() {
        return attributedRole;
    }

    public LGRole getRole() {
        return role;
    }

    public void setRole(LGRole role) {
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public abstract String toString();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LGCard lgCard = (LGCard) o;
        return Objects.equals(id, lgCard.id);
    }

    public SelectMenu.Option toOption() {
        return SelectMenu.Option.of(this.toString(), this.id.toString());
    }

    public Button toButton() {
        return Button.primary(this.id.toString(), this.toString());
    }
}
