package io.github.archeox.lgunenuit.utility;


import io.github.archeox.lgunenuit.game.card.LGCard;
import io.github.archeox.lgunenuit.game.card.PlayerCard;

import java.util.*;

public class Vote {

    //key : voter, value : voted for
    private final HashMap<PlayerCard, LGCard> vote;
    //Ignored during winner calculation
    private final List<LGCard> ignoredCard;
    //Field to override winner calculation,
    private LGCard winner;

    public Vote() {
        this.vote = new HashMap<>();
        this.ignoredCard = new ArrayList<>();
        this.winner = null;
    }

    public void registerVote(PlayerCard voter, LGCard voted) {
        this.vote.putIfAbsent(voter, voted);
    }

    public boolean asVoted(PlayerCard player) {
        return this.vote.containsKey(player);
    }

    public boolean hasEveryBodyVoted(List<PlayerCard> cards) {
        boolean result = true;
        for (LGCard card : cards) {
            if (!this.vote.containsKey(card)) {
                System.out.println("FAUX");
                result = false;
                break;
            }
        }
        return result;
    }

    public LGCard getWinner() {
        LGCard winner = null;
        if (this.winner == null) {
            HashMap<LGCard, Integer> count = new HashMap<>();
            Collection<LGCard> players = vote.values();

            for (LGCard player : players) {
                if (!this.ignoredCard.stream().anyMatch(lgCard -> lgCard.equals(player))) {
                    if (count.containsKey(player)) {
                        count.replace(player, 1 + count.get(player));
                    } else {
                        count.put(player, 1);
                    }
                }
            }

            int max = count.values().stream().max(Integer::compareTo).get();

            for (LGCard card : count.keySet()) {
                if (count.get(card).equals(max)) {
                    winner = card;
                    break;
                }
            }
        } else {
            winner = this.winner;
        }

        return winner;
    }

    public boolean addIgnored(LGCard card) {
        return this.ignoredCard.add(card);
    }

    public void setWinner(LGCard winner){
        this.winner = winner;
    }
}
