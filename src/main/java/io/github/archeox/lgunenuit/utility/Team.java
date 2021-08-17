package io.github.archeox.lgunenuit.utility;

public enum Team {
    //Enum representing Team. Each that has special winnning condition gets a Team.
    //First argument is the string representation of the Team
    //Second argument is if the team win by having one of its member killed or not (true = suicidal)
    VILLAGE("Le Village"),
    LG("Les Loups-Garous"),
    TANNEUR("Le tanneur"),
    SBIRE("Les Loups-Garous, grâce au Sbire,");

    private final String description;

    Team(String description) {
        this.description = description;

    }

    public String getDescription() {
        return description;
    }
}
