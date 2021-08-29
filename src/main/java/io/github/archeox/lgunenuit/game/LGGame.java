package io.github.archeox.lgunenuit.game;

import discord4j.core.object.Embed;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.enums.Team;
import io.github.archeox.lgunenuit.game.card.LGCard;
import io.github.archeox.lgunenuit.game.card.MysteryCard;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import io.github.archeox.lgunenuit.roles.LoupGarou;
import io.github.archeox.lgunenuit.roles.Villageois;
import io.github.archeox.lgunenuit.roles.core.Noctambule;
import io.github.archeox.lgunenuit.roles.core.SpecialVoter;
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


    public LGGame(MessageChannel channel) {
        this.members = new ArrayList<>();
        this.roles = new ArrayList<>();
        this.channel = channel;
        this.cards = new ArrayList<>();
        this.currentTurn = 0;
        this.currentVote = 0;
        this.nightPlayers = new ArrayList<>();
        this.votePlayers = new ArrayList<>();
        this.vote = new Vote();
    }

    //================================================================================================================
    //Méthode appelée durant la configuration de la partie

    public boolean addMember(Member member) {
        return members.add(member);
    }

    public boolean setRoles(List<LGRole> roles) {
        this.roles.clear();
        return this.roles.addAll(roles);
    }

    public boolean okToStart() {
        return (roles.size() == members.size() + 3);
    }

    //================================================================================================================
    //Main Game Logic

    public Mono<Void> startGame() {

        //on attribue les rôles aux joueurs
        int mysteryCount = 1;
        Collections.shuffle(roles);
        Collections.shuffle(members);
        for (int i = 0; i < roles.size(); i++) {
            if (members.size() > i) {
                cards.add(new PlayerCard(members.get(i), roles.get(i)));
            } else {
                cards.add(new MysteryCard("Mystère #" + mysteryCount, roles.get(i)));
                mysteryCount++;
            }
        }

        //on remplit nightplayer
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

        //on envoie leur rôle aux joueurs
        Flux<Message> roleMessage = Flux.fromIterable(cards)
                .filter(lgCard -> lgCard instanceof PlayerCard)
                .cast(PlayerCard.class)
                .flatMap(lgPlayer -> lgPlayer.whisper("Tu es " + lgPlayer.getAttributedRole() + "\n" + lgPlayer.getAttributedRole().getDescription()));

        return Mono.when(roleMessage)
                .then(postAnnounce("La nuit tombe sur le village !"))
                .then(nextTurn());
    }

    public Mono<Void> nextTurn() {
        if (currentTurn < nightPlayers.size()) {
            Mono<Void> result = ((Noctambule) nightPlayers.get(currentTurn).getAttributedRole()).nightAction(this, nightPlayers.get(currentTurn));
            currentTurn++;
            return result;
        } else {
            return votePhase();
        }
    }

    //On crée un Vote complet par le village
    private Mono<Void> votePhase() {

        List<Button> buttons = getMembersCards().stream().map(LGCard::toButton).collect(Collectors.toList());
        MysteryCard nobody = new MysteryCard("Nobody", new Villageois());
        buttons.add(Button.secondary("Nobody", "Tout le monde est innocent !"));

        //post voting message
        return this.channel.createMessage(messageCreateSpec -> {
                    messageCreateSpec.setContent("Veuillez cliquer sur la personne que vous voulez éliminer :");
                    messageCreateSpec.setComponents(ActionRow.of(buttons));
                })
                .map(Message::getId)
                .flatMap(snowflake -> LGUneNuit.BUTTON_INTERACT_HANDLER.registerButtonInteraction(snowflake, buttonInteractEvent -> {

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
                }, false));
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
            Mono<Void> result = ((SpecialVoter) votePlayers.get(currentVote).getAttributedRole()).voteAction(this, votePlayers.get(currentVote), this.vote);
            currentVote++;
            return result;
        } else {
            return endGame();
        }
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

        String endMessage = "C'est la fin de la partie, voici le résumé des roles :";
        for (LGCard card : cards) {
            if (card instanceof PlayerCard) {
                endMessage = endMessage.concat(String.format("\n%s **->** %s", ((PlayerCard) card).getMember().getNicknameMention(), card.getRole().toString()));
            } else {
                endMessage = endMessage.concat(String.format("\n%s **->** %s", card.toString(), card.getRole().toString()));
            }
        }
        return Mono.when(result).then(postAnnounce(endMessage)).then();
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
}
