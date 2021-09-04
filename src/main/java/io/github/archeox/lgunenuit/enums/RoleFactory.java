package io.github.archeox.lgunenuit.enums;

import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.reaction.ReactionEmoji;
import io.github.archeox.lgunenuit.roles.*;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import reactor.function.Function4;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class RoleFactory {
    public enum RoleID {
        VILLAGEOIS("Villageois",
                "Le Villageois ne fait rien de spécial.",
                "\uD83E\uDDD1\u200D\uD83C\uDF3E",
                Team.VILLAGE),
        CHASSEUR("Chasseur",
                "Si le chasseur est exécuté par le village, il peut tuer le joueur de son choix à sa place.",
                "\uD83C\uDFAF",
                Team.VILLAGE),
        LOUPGAROU("Loup-Garou",
                "Le but du Loup-Garou est d'éliminer un membre du village.\n"
                        + "Il peut de savoir qui sont les autres Loups-Garous. S'il n'y en a pas, il a le droit de regarder une carte au milieu.",
                "\uD83D\uDC3A",
                Team.LG),
        NOISEUSE("Noiseuse",
                "La noiseuse peut échanger les rôles de deux autres joueurs pendant la nuit.",
                "\uD83E\uDD39\u200D??",
                Team.VILLAGE),
        SBIRE("Sbire",
                "Le Sbire fait équipe avec les loups-garous et sait qui ils sont." +
                        "Si le Sbire est éliminé les Loups-Garous gagnent !",
                "\uD83D\uDDE1?",
                Team.SBIRE),
        VOYANTE("Voyante",
                "La Voyante peut observer la carte d'un joueur.",
                "\uD83E\uDDD9\u200D??",
                Team.VILLAGE);

        private String name;
        private String description;
        private String emoji;
        private Team team;

        RoleID(String name, String description, String emoji, Team team) {
            this.name = name;
            this.description = description;
            this.emoji = emoji;
            this.team = team;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getEmoji() {
            return emoji;
        }

        public Team getTeam() {
            return team;
        }
    }


    public static LGRole getRole(RoleID id) {
        LGRole result;
        String name = id.getName();
        String desc = id.getDescription();
        String emoji = id.getEmoji();
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

    public static SelectMenu.Option toOption(RoleID id){

        //Default option
        return SelectMenu.Option.of(id.getName(), id.name())
                .withDescription(id.getDescription()).withEmoji(ReactionEmoji.unicode(id.getEmoji()));
    }
}
