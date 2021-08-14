package io.github.archeox.lgunenuit.game.card;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import io.github.archeox.lgunenuit.game.LGRole;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Classe représentant un joueur, avec son rôle attribué et effectif. Si le champ membre n'est pas spécifié, le joueur est une carte retournée.
 */
public class PlayerCard extends LGCard {

    private final Member member;

    public PlayerCard(Member member, LGRole role) {
        super(role);
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    public Mono<Message> whisper(String message) {
        return member.getPrivateChannel().flatMap(privateChannel -> privateChannel.createMessage(message));
    }

    public Mono<Message> whisper(Consumer<MessageCreateSpec> content) {
            return this.getMember().getPrivateChannel()
                    .flatMap(privateChannel -> privateChannel.createMessage(content));
    }

    @Override
    public String toString() {
            return this.getMember().getDisplayName();
    }
}
