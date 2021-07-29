package io.github.archeox.lgunenuit.roles;

import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.utility.Team;

public abstract class LGRole {

    private String name;
    private String description;
    private Team team;

    public LGRole(String name, String description, Team team) {
        this.name = name;
        this.description = description;
        this.team = team;
    }

    //méthode qui sera appelée à chaque tour
    public Runnable getEachTurn(LGGame game) {
        return null;
    }

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

    @Override
    public String toString() {
        return name;
    }
}
