package io.github.archeox.lgunenuit.game;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import io.github.archeox.lgunenuit.roles.LGRole;
import reactor.core.publisher.Mono;

/**
 * Classe représentant un joueur, avec son rôle attribué et effectif. Si le champ membre n'est pas spécifié, le joueur est une carte retournée.
 */
public class LGPlayer {

    private final LGRole attributedRole;
    private LGRole role;
    private final Member member;
    private final boolean isMystery;

    public LGPlayer(Member member, LGRole attributedRole) {
        this.member = member;
        this.attributedRole = attributedRole;
        this.isMystery = (this.member == null);
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

    public Mono<Message> whisper(MessageCreateSpec message){
        if (!isMystery) {
            return member.getPrivateChannel().flatMap(privateChannel -> privateChannel.createMessage(messageCreateSpec -> messageCreateSpec = message));
        } else {
            throw new UnsupportedOperationException("This player has no user.");
        }
    }

    public Mono<Message> whisper(String message){
        if (!isMystery) {
            return member.getPrivateChannel().flatMap(privateChannel -> privateChannel.createMessage(message));
        } else {
            throw new UnsupportedOperationException("This player has no user.");
        }
    }
}
