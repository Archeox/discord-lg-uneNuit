package io.github.archeox.lgunenuit.roles;

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

    public abstract void action();

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
