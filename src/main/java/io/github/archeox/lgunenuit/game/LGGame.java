package io.github.archeox.lgunenuit.game;

import discord4j.core.object.Embed;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.game.card.LGCard;
import io.github.archeox.lgunenuit.game.card.MysteryCard;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.roles.Noiseuse;
import io.github.archeox.lgunenuit.roles.Villageois;
import io.github.archeox.lgunenuit.roles.Voyante;
import io.github.archeox.lgunenuit.utility.Vote;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class LGGame {

    private final List<Member> members;
    private final List<LGRole> roles;
    private final MessageChannel channel;
    private final List<LGCard> cards;
    private final List<PlayerCard> nightPlayers;
    private final Vote vote;
    private int currentTurn;


    public LGGame(List<Member> members, List<LGRole> roles, MessageChannel channel) {
        this.members = members;
        this.roles = roles;
        this.channel = channel;
        this.cards = new ArrayList<>();
        this.currentTurn = 0;
        this.nightPlayers = new ArrayList<>();
        this.vote = new Vote();
    }

    public Mono<Void> startGame() {
        postAnnounce("La partie va commencer !").subscribe();

        //on attribue les rôles aux joueurs
        int mysteryCount = 1;
        Collections.shuffle(roles);
        Collections.shuffle(members);
        for (int i = 0; i < roles.size(); i++) {
            if (members.size() - 1 > i) {
                cards.add(new PlayerCard(members.get(i), roles.get(i)));
            } else {
                cards.add(new MysteryCard("Mystère #" + mysteryCount, roles.get(i)));
                mysteryCount++;
            }
        }


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
            votePhase().subscribe();
        }
        return Mono.empty();
    }

    public Mono<Void> votePhase() {

        List<Button> buttons = getMembersCards().stream().map(LGCard::toButton).collect(Collectors.toList());
        buttons.add(Button.secondary(new MysteryCard("Nobody", new Villageois()).getId().toString(), "Tout le monde est innocent !"));

        //post voting message
        this.channel.createMessage(messageCreateSpec -> {
                    messageCreateSpec.setContent("Veuillez cliquer sur la personne que vous voulez éliminer :");
                    messageCreateSpec.setComponents(ActionRow.of(buttons));
                })
                .map(Message::getId)
                .map(snowflake -> LGUneNuit.BUTTON_INTERACT_HANDLER.registerButtonInteraction(snowflake, buttonInteractEvent -> {

                    PlayerCard voter = getCardFromMember(buttonInteractEvent.getInteraction().getMember().get());
                    LGCard voted = getCardById(buttonInteractEvent.getCustomId());

                    if (!vote.asVoted(voter)){
                        vote.registerVote(voter, voted);
                    }

                    return buttonInteractEvent.replyEphemeral("Votre vote à été enregistré !");
                }))
                .subscribe();


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

    public List<LGCard> getAllCards() {
        return cards;
    }

    public List<PlayerCard> getMembersCards() {
        List<PlayerCard> result = new ArrayList<>();
        cards.forEach(player -> {
            if (player instanceof PlayerCard) {
                result.add((PlayerCard) player);
            }
        });
        return result;
    }

    public List<MysteryCard> getMysteryCards() {
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

    public LGCard getCardById(String id) {
        LGCard result = null;
        for (LGCard player : cards) {
            if (player.getId().toString().equals(id)) {
                result = player;
                break;
            }
        }
        return result;
    }

    public PlayerCard getCardFromMember(Member member){
        PlayerCard result = null;
        for (PlayerCard card : this.getMembersCards()){
            if (card.getMember().equals(member)) {
                result  = card;
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
        this.vote = new Vote();
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
}
