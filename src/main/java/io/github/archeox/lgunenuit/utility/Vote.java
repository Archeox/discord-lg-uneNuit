package io.github.archeox.lgunenuit.utility;


import io.github.archeox.lgunenuit.game.card.LGCard;
import io.github.archeox.lgunenuit.game.card.PlayerCard;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Vote {

    //key : voter, value : voted for
    private final HashMap<PlayerCard, LGCard> vote;

    public Vote() {
        this.vote = new HashMap<>();
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
        HashMap<LGCard, Integer> count = new HashMap<>();
        Collection<LGCard> players = vote.values();

        for (LGCard LGCard : players) {
            if (count.containsKey(LGCard)) {
                count.replace(LGCard, 1 + count.get(LGCard));
            } else {
                count.put(LGCard, 1);
            }
        }

        int max = count.values().stream().max(Integer::compareTo).get();

        for (LGCard card : count.keySet()) {
            if (count.get(card).equals(max)) {
                winner = card;
                break;
            }
        }

        return winner;
    }

    public LGCard getWinner(LGCard... ignoredCards) {
        LGCard winner = null;
        HashMap<LGCard, Integer> count = new HashMap<>();
        Collection<LGCard> players = vote.values();

        for (LGCard player : players) {
            if (!Arrays.stream(ignoredCards).anyMatch(LGCard -> LGCard.equals(player))) {
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

        return winner;
    }

}
