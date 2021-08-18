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
import io.github.archeox.lgunenuit.roles.LGRole;
import io.github.archeox.lgunenuit.roles.LoupGarou;
import io.github.archeox.lgunenuit.roles.Villageois;
import io.github.archeox.lgunenuit.roles.interfaces.Noctambule;
import io.github.archeox.lgunenuit.roles.interfaces.SpecialVoter;
import io.github.archeox.lgunenuit.utility.Team;
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
    private final List<PlayerCard> votePlayers;
    private final Vote vote;
    private int currentTurn;
    private int currentVote;


    public LGGame(List<Member> members, List<LGRole> roles, MessageChannel channel) {
        this.members = members;
        this.roles = roles;
        this.channel = channel;
        this.cards = new ArrayList<>();
        this.currentTurn = 0;
        this.currentVote = 0;
        this.nightPlayers = new ArrayList<>();
        this.votePlayers = new ArrayList<>();
        this.vote = new Vote();
    }

    public Mono<Void> startGame() {

        Mono result = postAnnounce("La partie va commencer !");

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
        result.then(Flux.fromIterable(cards)
                .filter(lgCard -> lgCard instanceof PlayerCard)
                .cast(PlayerCard.class)
                .flatMap(lgPlayer -> lgPlayer.whisper("Tu es " + lgPlayer.getAttributedRole() + "\n" + lgPlayer.getAttributedRole().getDescription()))
                .then()
                .delayElement(Duration.ofSeconds(10))
                .then(postAnnounce("La nuit tombe sur le village !")));

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
        result.then(nextTurn());
        return result;
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

    //On crée un Vote complet par le village
    private Mono<Void> votePhase() {

        List<Button> buttons = getMembersCards().stream().map(LGCard::toButton).collect(Collectors.toList());
        MysteryCard nobody = new MysteryCard("Nobody", new Villageois());
        buttons.add(Button.secondary("Nobody", "Tout le monde est innocent !"));

        //post voting message
        this.channel.createMessage(messageCreateSpec -> {
                    messageCreateSpec.setContent("Veuillez cliquer sur la personne que vous voulez éliminer :");
                    messageCreateSpec.setComponents(ActionRow.of(buttons));
                })
                .map(Message::getId)
                .map(snowflake -> LGUneNuit.BUTTON_INTERACT_HANDLER.registerButtonInteraction(snowflake, buttonInteractEvent -> {

                    PlayerCard voter = getCardFromMember(buttonInteractEvent.getInteraction().getMember().get());
                    String id = buttonInteractEvent.getCustomId();
                    LGCard voted = (!id.equals("Nobody")) ? getCardById(id) : nobody;

                    if (voter == null || voted == null) {
                        return buttonInteractEvent.acknowledge();
                    } else if (!vote.asVoted(voter)) {
                        vote.registerVote(voter, voted);
                        if (vote.hasEveryBodyVoted(getMembersCards())) {
                            LGUneNuit.BUTTON_INTERACT_HANDLER.unRegisterInteraction(snowflake);
                            return buttonInteractEvent.replyEphemeral("Votre vote à été enregistré !")
                                    .then(specialVoterInit());
                        } else {
                            return buttonInteractEvent.replyEphemeral("Votre vote à été enregistré !");
                        }
                    } else {
                        return buttonInteractEvent.replyEphemeral("Vous avez déjà voté !");
                    }
                }, false))
                .subscribe();


        return Mono.empty();
    }

    //On définit quels les rôles qui modifient le Vote, et on les fait jouer
    private Mono<Void> specialVoterInit() {

        for (LGCard card : cards) {
            if (card.getAttributedRole() instanceof SpecialVoter && card instanceof PlayerCard) {
                votePlayers.add((PlayerCard) card);
            }
        }
        votePlayers.sort((o1, o2) -> {
            SpecialVoter n1 = ((SpecialVoter) o1.getAttributedRole());
            SpecialVoter n2 = ((SpecialVoter) o2.getAttributedRole());
            if (n1.getPriority() < n2.getPriority()) {
                return -1;
            } else if (n1.getPriority() > n2.getPriority()) {
                return 1;
            } else {
                return 0;
            }
        });
        return nextSpecialVoter();
    }

    public Mono<Void> nextSpecialVoter() {
        if (currentVote < votePlayers.size()) {
            ((Noctambule) votePlayers.get(currentVote).getAttributedRole()).nightAction(this, votePlayers.get(currentVote));
            currentVote++;
        } else {
            endGame().subscribe();
        }
        return Mono.empty();
    }

    //We're in the EndGame now...
    private Mono<Void> endGame() {

        Team eliminatedTeam = this.vote.getWinner().getRole().getTeam();
        Mono<Void> result;

        switch (eliminatedTeam) {
            case VILLAGE -> result = postAnnounce("Les loups-garous ont gagné !").then();
            case LG -> result = postAnnounce("Le Village a gagné !").then();
            case SBIRE -> {
                if (!onlyMemberCard(getCardsByRole(LoupGarou.class)).isEmpty()) {
                    result = postAnnounce("Les Loups-Garou ont gagné grâce au sacrifice du Sbire !").then();
                } else {
                    result = postAnnounce("Le Village a gagné !").then();
                }
            }
            case TANNEUR -> result = postAnnounce("Le Tanneur a gagné !").then();
            default -> throw new IllegalStateException("Non-Valid Team");
        }

        String endMessage = "C'est la fin de la partie, voici le résumé des roles :\n";
        for (LGCard card : cards) {
            if (card instanceof PlayerCard) {
                endMessage = endMessage.concat(String.format("%s **->** %s", ((PlayerCard) card).getMember().getNicknameMention(), card.getRole().toString()));
            } else {
                endMessage = endMessage.concat(String.format("%s **->** %s", card.toString(), card.getRole().toString()));
            }
        }

        result.then(postAnnounce(endMessage));
        return result;
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

    public List<LGCard> getCardsByAttributedRole(Class<? extends LGRole> roleClass) {
        List<LGCard> result = new ArrayList<>();
        cards.forEach(player -> {
            if (roleClass.isInstance(player.getAttributedRole())) {
                result.add(player);
            }
        });
        return result;
    }

    public List<LGCard> getCardsByRole(Class<? extends LGRole> roleClass) {
        List<LGCard> result = new ArrayList<>();
        cards.forEach(player -> {
            if (roleClass.isInstance(player.getRole())) {
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

    public PlayerCard getCardFromMember(Member member) {
        PlayerCard result = null;
        for (PlayerCard card : this.getMembersCards()) {
            if (card.getMember().equals(member)) {
                result = card;
                break;
            }
        }
        return result;
    }

    public List<PlayerCard> onlyMemberCard(List<LGCard> lgCards) {
        List<PlayerCard> result = new ArrayList<>();
        for (LGCard lgCard : lgCards) {
            if (lgCard instanceof PlayerCard) {
                result.add((PlayerCard) lgCard);
            }
        }
        return result;
    }

    //================================================================================================================

    public LGGame(MessageChannel channel) {
        this.cards = new ArrayList<>();
        this.members = new ArrayList<>();
        this.roles = new ArrayList<>();
        this.nightPlayers = new ArrayList<>();
        this.votePlayers = new ArrayList<>();
        this.channel = channel;
        this.vote = new Vote();
        this.currentTurn = 0;
        this.currentVote = 0;
    }

    public Mono<Void> testGame(Member member) {

        cards.clear();
        cards.add(new PlayerCard(member, new Villageois()));

        //on envoie leur rôle aux joueurs
        Mono<Void> result = Flux.fromIterable(cards)
                .filter(lgCard -> lgCard instanceof PlayerCard)
                .cast(PlayerCard.class)
                .flatMap(lgPlayer -> lgPlayer.whisper("Tu es " + lgPlayer.getAttributedRole() + "\n" + lgPlayer.getAttributedRole().getDescription()))
                .then()
                .delayElement(Duration.ofSeconds(10))
                .then(postAnnounce("La nuit tombe sur le village !"))
                .then();

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

        result.then(nextTurn());

        return result;
    }
}
