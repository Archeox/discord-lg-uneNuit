package io.github.archeox.lgunenuit.game;

import discord4j.core.object.Embed;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import io.github.archeox.lgunenuit.roles.LGRole;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LGGame {

    private List<Member> members;
    private List<LGRole> roles;
    private MessageChannel channel;
    private List<LGPlayer> players;


    public LGGame(List<Member> members, List<LGRole> roles, MessageChannel channel) {
        this.members = members;
        this.roles = roles;
        this.channel = channel;

        this.players = new ArrayList<>();
    }

    public void startGame() {
        postAnnounce("La partie va commencer !");

        //on attribue les rôles aux joueurs
        Collections.shuffle(roles);
        Collections.shuffle(members);
        for (int i = 0; i < roles.size(); i++) {
            if (members.get(i) != null) {
                players.add(new LGPlayer(members.get(i), roles.get(i)));
            } else {
                players.add(new LGPlayer(members.get(i), roles.get(i)));
            }
        }
        

    }

    public Mono<Embed> postSummary() {
        throw new RuntimeException();
    }

    public Mono<Message> postAnnounce(String text) {
        return channel.createMessage(text);
    }
}
