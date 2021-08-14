package io.github.archeox.lgunenuit.game;

import discord4j.core.object.Embed;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import io.github.archeox.lgunenuit.game.card.LGCard;
import io.github.archeox.lgunenuit.game.card.MysteryCard;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.roles.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LGGame {

    private final List<Member> members;
    private final List<LGRole> roles;
    private final MessageChannel channel;
    private final List<LGCard> cards;
    private final List<PlayerCard> nightPlayers;
    private int currentTurn;


    public LGGame(List<Member> members, List<LGRole> roles, MessageChannel channel) {
        this.members = members;
        this.roles = roles;
        this.channel = channel;
        this.cards = new ArrayList<>();
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
//                players.add(new PlayerCard(members.get(i), roles.get(i)));
//            } else {
//                players.add(new PlayerCard(null, roles.get(i)));
//            }
//        }
//
//        //on envoie leur rôle aux joueurs
//        Flux.fromIterable(players)
//                .filter(PlayerCard::isMember)
//                .flatMap(lgPlayer -> lgPlayer.whisper("Tu es " + lgPlayer.getAttributedRole() + "\n" + lgPlayer.getAttributedRole().getDescription()))
//                .subscribe();
//
//        postAnnounce("La nuit tombe sur le village !").subscribe();
//
//        //on fait jouer les joueurs
//        Flux.fromIterable(players)
//                .filter(PlayerCard::isMember)
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
    public void swapRoles(PlayerCard player1, PlayerCard player2) {
        LGRole role = player2.getRole();
        player2.setRole(player1.getRole());
        player1.setRole(role);
    }

    public List<LGCard> getAllPlayers() {
        return cards;
    }

    public List<PlayerCard> getMembersPlayers() {
        List<PlayerCard> result = new ArrayList<>();
        cards.forEach(player -> {
            if (player instanceof PlayerCard) {
                result.add((PlayerCard) player);
            }
        });
        return result;
    }

    public List<MysteryCard> getMysteryPlayers() {
        List<MysteryCard> result = new ArrayList<>();
        cards.forEach(player -> {
            if (player instanceof MysteryCard) {
                result.add((MysteryCard) player);
            }
        });
        return result;
    }

    public List<LGCard> getCardsByAttributedRole(LGRole role) {
        List<LGCard> result = new ArrayList<>();
        cards.forEach(player -> {
            if (player.getAttributedRole().getName().equals(role.getName())) {
                result.add(player);
            }
        });
        return result;
    }

    public List<LGCard> getCardsByRole(LGRole role) {
        List<LGCard> result = new ArrayList<>();
        cards.forEach(player -> {
            if (player.getRole().getName().equals(role.getName())) {
                result.add(player);
            }
        });
        return result;
    }

    public LGCard getCardById(UUID id) {
        LGCard result = null;
        for (LGCard player : cards) {
            if (player.getId().equals(id)) {
                result = player;
                break;
            }
        }
        return result;
    }

    //================================================================================================================

    public LGGame(MessageChannel channel) {
        cards = new ArrayList<>();
        members = new ArrayList<>();
        roles = new ArrayList<>();
        this.nightPlayers = new ArrayList<>();
        this.channel = channel;
    }

    public Mono<Void> testGame(Member member) {


        cards.clear();
        cards.add(new PlayerCard(member, new Villageois()));
        cards.add(new PlayerCard(member, new Noiseuse(9)));
        cards.add(new PlayerCard(member, new Voyante(7)));

        //on envoie leur rôle aux joueurs
        Flux.fromIterable(cards)
                .filter(lgCard -> lgCard instanceof PlayerCard)
                .cast(PlayerCard.class)
                .flatMap(lgPlayer -> lgPlayer.whisper("Tu es " + lgPlayer.getAttributedRole() + "\n" + lgPlayer.getAttributedRole().getDescription()))
                .then()
                .delayElement(Duration.ofSeconds(10))
                .then(postAnnounce("La nuit tombe sur le village !"))
                .subscribe();

        //on lance le premier tour
        for (LGCard card : cards) {
            if (card.getAttributedRole() instanceof Noctambule && card instanceof PlayerCard) {
                nightPlayers.add((PlayerCard) card);
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
        } else {
            postAnnounce("Le jour se lève").subscribe();
        }
        return Mono.empty();
    }
}
