package io.github.archeox.lgunenuit.roles;

import discord4j.core.object.reaction.ReactionEmoji;
import io.github.archeox.lgunenuit.enums.Team;
import io.github.archeox.lgunenuit.roles.core.LGRole;

public class Villageois extends LGRole {

    public Villageois(String name, String description, ReactionEmoji emoji, Team team) {
        super(name, description, emoji, team);
    }
}

