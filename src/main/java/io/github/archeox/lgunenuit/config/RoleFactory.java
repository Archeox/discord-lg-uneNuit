package io.github.archeox.lgunenuit.config;

import discord4j.core.object.reaction.ReactionEmoji;
import io.github.archeox.lgunenuit.enums.Team;
import io.github.archeox.lgunenuit.exception.RoleConfigException;
import io.github.archeox.lgunenuit.helper.RoleInfoConfig;
import io.github.archeox.lgunenuit.roles.*;
import io.github.archeox.lgunenuit.roles.core.LGRole;

public class RoleFactory {
    public enum RoleID{
        VILLAGEOIS("config/villageois.prop"),
        CHASSEUR("config/chass.prop"),
        LOUPGAROU("config/loupgarou.prop"),
        NOISEUSE("config/noiseuse.prop"),
        SBIRE("config/sbire.prop"),
        VOYANTE("config/voyante.prop");

        private RoleInfoConfig config;

        RoleID(String fileName) {
            this.config = new RoleInfoConfig(fileName);
        }

        public String getName() {
            return config.getName();
        }

        public String getDescription() {
            return config.getDescription();
        }

        public ReactionEmoji getEmoji() {
            return config.getEmoji();
        }

        public Team getTeam() {
            return config.getTeam();
        }

        public int[] getNumbers() {
            return config.getNumber();
        }

    }


    public static LGRole getRole(RoleID id) {
        LGRole result;
        String name = id.getName();
        String desc = id.getDescription();
        ReactionEmoji emoji = id.getEmoji();
        Team team = id.getTeam();
        switch (id) {
            case SBIRE -> result = new Sbire(name, desc, emoji, team);
            case VOYANTE -> result = new Voyante(name, desc, emoji, team);
            case CHASSEUR -> result = new Chasseur(name, desc, emoji, team);
            case NOISEUSE -> result = new Noiseuse(name, desc, emoji, team);
            case LOUPGAROU -> result = new LoupGarou(name, desc, emoji, team);
            case VILLAGEOIS -> result = new Villageois(name, desc, emoji, team);
            default -> result = null;
        }
        return result;
    }

}
