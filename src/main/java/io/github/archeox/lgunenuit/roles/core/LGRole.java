package io.github.archeox.lgunenuit.roles.core;

import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.enums.Team;

import java.util.Objects;

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

    public Team getTeam() {
        return team;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LGRole lgRole = (LGRole) o;
        return Objects.equals(name, lgRole.name);
    }
}
