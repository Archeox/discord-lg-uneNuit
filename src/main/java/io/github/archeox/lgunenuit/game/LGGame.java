package io.github.archeox.lgunenuit.game;

import discord4j.core.object.Embed;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import io.github.archeox.lgunenuit.roles.LGRole;
import io.github.archeox.lgunenuit.roles.Noctambule;
import io.github.archeox.lgunenuit.roles.Noiseuse;
import io.github.archeox.lgunenuit.utility.InteractionManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LGGame {

    private final List<Member> members;
    private final List<LGRole> roles;
    private final MessageChannel channel;
    private final List<LGPlayer> players;
    private final InteractionManager interactionManager;


    public LGGame(List<Member> members, List<LGRole> roles, MessageChannel channel) {
        this.members = members;
        this.roles = roles;
        this.channel = channel;
        this.players = new ArrayList<>();
        this.interactionManager = new InteractionManager();
    }

    public Mono<Void> startGame() {
        postAnnounce("La partie va commencer !").subscribe();

        //on attribue les rôles aux joueurs
        Collections.shuffle(roles);
        Collections.shuffle(members);
        for (int i = 0; i < roles.size(); i++) {
            if (members.size() - 1 > i) {
                players.add(new LGPlayer(members.get(i), roles.get(i)));
            } else {
                players.add(new LGPlayer(null, roles.get(i)));
            }
        }

        //on envoie leur rôle aux joueurs
        Flux.fromIterable(players)
                .filter(LGPlayer::isMember)
                .flatMap(lgPlayer -> lgPlayer.whisper("Tu es " + lgPlayer.getAttributedRole() + "\n" + lgPlayer.getAttributedRole().getDescription()))
                .subscribe();

        postAnnounce("La nuit tombe sur le village !").subscribe();

        //on fait jouer les joueurs
        Flux.fromIterable(players)
                .filter(LGPlayer::isMember)
                .filter(player -> player.getAttributedRole() instanceof Noctambule)
                .sort((o1, o2) -> {
                    Noctambule n1 = ((Noctambule) o1.getAttributedRole());
                    Noctambule n2 = ((Noctambule) o2.getAttributedRole());
                    if (n1.getTurn() < n2.getTurn()) {
                        return -1;
                    } else if (n1.getTurn() > n2.getTurn()) {
                        return 1;
                    } else {
                        return 0;
                    }
                })
                .map(player ->
                        Mono.just(player.getAttributedRole())
                                .cast(Noctambule.class)
                                .flatMapMany(noctambule -> noctambule.nightAction(this, player))
                                .subscribe()
                )
                .subscribe();

        postAnnounce("Le jour se lève !").subscribe();

        //Phase de vote


        return Mono.empty();
    }

    public Mono<Embed> postSummary() {
        throw new RuntimeException();
    }

    public Mono<Message> postAnnounce(String text) {
        return channel.createMessage(text);
    }

    public InteractionManager getInteractionManager() {
        return interactionManager;
    }

    //================================================================================================================
    //Utility Methods for Actions by Roles
    public void swapRoles(LGPlayer player1, LGPlayer player2) {
        LGRole role = player2.getRole();
        player2.setRole(player1.getRole());
        player1.setRole(role);
    }

    public List<LGPlayer> getAllPlayers() {
        return players;
    }

    public List<LGPlayer> getMembersPlayers() {
        List<LGPlayer> result = new ArrayList<>();
        players.forEach(player -> {
            if (player.isMember()) {
                result.add(player);
            }
        });
        return result;
    }

    public List<LGPlayer> getMysteryPlayers() {
        List<LGPlayer> result = new ArrayList<>();
        players.forEach(player -> {
            if (!player.isMember()) {
                result.add(player);
            }
        });
        return result;
    }

    public List<LGPlayer> getPlayersByAttributedRole(LGRole role) {
        List<LGPlayer> result = new ArrayList<>();
        players.forEach(player -> {
            if (player.getAttributedRole().getName().equals(role.getName())) {
                result.add(player);
            }
        });
        return result;
    }

    public List<LGPlayer> getPlayersByRole(LGRole role) {
        List<LGPlayer> result = new ArrayList<>();
        players.forEach(player -> {
            if (player.getRole().getName().equals(role.getName())) {
                result.add(player);
            }
        });
        return result;
    }
}
