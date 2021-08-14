package io.github.archeox.lgunenuit.game.card;

import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import io.github.archeox.lgunenuit.game.LGRole;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

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

    public SelectMenu.Option toOption(){
        return SelectMenu.Option.of(this.toString(), this.id.toString());
    }


}
