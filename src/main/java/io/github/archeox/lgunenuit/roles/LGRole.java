package io.github.archeox.lgunenuit.roles;

import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.Utility.Team;
import reactor.core.publisher.Mono;

public abstract class LGRole {

    private String name;
    private String description;
    private int turn;
    private Team team;

    public LGRole(String name, String description, int turn, Team team) {
        this.name = name;
        this.description = description;
        this.turn = turn;
        this.team = team;
    }

    public abstract Mono<Void> action(LGGame game);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }
}
