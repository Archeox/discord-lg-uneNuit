package io.github.archeox.lgunenuit.game;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import io.github.archeox.lgunenuit.roles.LGRole;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Classe représentant un joueur, avec son rôle attribué et effectif. Si le champ membre n'est pas spécifié, le joueur est une carte retournée.
 */
public class LGPlayer {

    private final LGRole attributedRole;
    private LGRole role;
    private final Member member;
    private final boolean isMember;
    private final UUID id;

    public LGPlayer(Member member, LGRole role) {
        this.member = member;
        this.attributedRole = role;
        this.role = role;
        this.isMember = (this.member != null);
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

    public Member getMember() {
        return member;
    }

    public boolean isMember() {
        return isMember;
    }

    public UUID getId() {
        return id;
    }

    public Mono<Message> whisper(MessageCreateSpec message) {
        if (isMember) {
            return member.getPrivateChannel().flatMap(privateChannel -> privateChannel.createMessage(messageCreateSpec -> messageCreateSpec = message));
        } else {
            throw new UnsupportedOperationException("This player has no user.");
        }
    }

    public Mono<Message> whisper(String message) {
        if (isMember) {
            return member.getPrivateChannel().flatMap(privateChannel -> privateChannel.createMessage(message));
        } else {
            throw new UnsupportedOperationException("This player has no user.");
        }
    }
}
