package io.github.archeox.lgunenuit.game;

import discord4j.core.object.Embed;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import io.github.archeox.lgunenuit.roles.*;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class LGGame {

    private final List<Member> members;
    private final List<LGRole> roles;
    private final MessageChannel channel;
    private final List<LGPlayer> players;
    private final List<LGPlayer> nightPlayers;
    private int currentTurn;


    public LGGame(List<Member> members, List<LGRole> roles, MessageChannel channel) {
        this.members = members;
        this.roles = roles;
        this.channel = channel;
        this.players = new ArrayList<>();
        this.currentTurn = 0;
        this.nightPlayers = new ArrayList<>();
    }

    public Mono<Void> startGame() {
//        postAnnounce("La partie va commencer !").subscribe();
//
//        //on attribue les rôles aux joueurs
//        Collections.shuffle(roles);
//        Collections.shuffle(members);
//        for (int i = 0; i < roles.size(); i++) {
//            if (members.size() - 1 > i) {
//                players.add(new LGPlayer(members.get(i), roles.get(i)));
//            } else {
//                players.add(new LGPlayer(null, roles.get(i)));
//            }
//        }
//
//        //on envoie leur rôle aux joueurs
//        Flux.fromIterable(players)
//                .filter(LGPlayer::isMember)
//                .flatMap(lgPlayer -> lgPlayer.whisper("Tu es " + lgPlayer.getAttributedRole() + "\n" + lgPlayer.getAttributedRole().getDescription()))
//                .subscribe();
//
//        postAnnounce("La nuit tombe sur le village !").subscribe();
//
//        //on fait jouer les joueurs
//        Flux.fromIterable(players)
//                .filter(LGPlayer::isMember)
//                .filter(player -> player.getAttributedRole() instanceof Noctambule)
//                .sort((o1, o2) -> {
//                    Noctambule n1 = ((Noctambule) o1.getAttributedRole());
//                    Noctambule n2 = ((Noctambule) o2.getAttributedRole());
//                    if (n1.getTurn() < n2.getTurn()) {
//                        return -1;
//                    } else if (n1.getTurn() > n2.getTurn()) {
//                        return 1;
//                    } else {
//                        return 0;
//                    }
//                })
//                .map(player -> {
//                            System.out.println(player.getMember().getDisplayName());
//                            return Mono.just(player.getAttributedRole())
//                                    .cast(Noctambule.class)
//                                    .flatMapMany(noctambule -> noctambule.nightAction(this, player))
//                                    .subscribe();
//                        }
//                )
//                .subscribe();
//
//        postAnnounce("Le jour se lève !").subscribe();

        //Phase de vote


        return Mono.empty();
    }

    public Mono<Embed> postSummary() {
        throw new RuntimeException();
    }

    public Mono<Message> postAnnounce(String text) {
        return channel.createMessage(text);
    }

    //================================================================================================================
    //Utility Methods for Actions by Roles
    public void swapRoles(@org.jetbrains.annotations.NotNull LGPlayer player1, @NotNull LGPlayer player2) {
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


    //================================================================================================================

    public LGGame(MessageChannel channel) {
        players = new ArrayList<>();
        members = new ArrayList<>();
        roles = new ArrayList<>();
        this.nightPlayers = new ArrayList<>();
        this.channel = channel;
    }

    public Mono<Void> testGame(Member member) {


        players.clear();
        players.add(new LGPlayer(member, new Villageois()));
        players.add(new LGPlayer(member, new Noiseuse(9)));
        players.add(new LGPlayer(member, new Voyante(7)));

//        //on envoie leur rôle aux joueurs
//        Flux.fromIterable(players)
//                .filter(LGPlayer::isMember)
//                .flatMap(lgPlayer -> lgPlayer.whisper("Tu es " + lgPlayer.getAttributedRole() + "\n" + lgPlayer.getAttributedRole().getDescription()))
//                .then()
//                .delayElement(Duration.ofSeconds(10))
//                .then(postAnnounce("La nuit tombe sur le village !"))
//                .subscribe();

        //on lance le premier tour
        for (LGPlayer player : players){
            if (player.getAttributedRole() instanceof Noctambule && player.isMember()){
                nightPlayers.add(player);
            }
        }

        nightPlayers.sort((o1, o2) -> {
            Noctambule n1 = ((Noctambule) o1.getAttributedRole());
            Noctambule n2 = ((Noctambule) o2.getAttributedRole());
            if (n1.getTurn() < n2.getTurn()) {
                return -1;
            } else if (n1.getTurn() > n2.getTurn()) {
                return 1;
            } else {
                return 0;
            }
        });

        nextTurn().subscribe();

        return Mono.empty();
    }

    public Mono<Void> nextTurn() {
        if (currentTurn < nightPlayers.size()) {
            ((Noctambule) nightPlayers.get(currentTurn).getAttributedRole()).nightAction(this, nightPlayers.get(currentTurn));
            currentTurn++;
        }else{
            postAnnounce("Le jour se lève").subscribe();
        }
        return Mono.empty();
    }
}
