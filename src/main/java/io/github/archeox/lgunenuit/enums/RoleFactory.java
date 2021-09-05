package io.github.archeox.lgunenuit.enums;

import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.reaction.ReactionEmoji;
import io.github.archeox.lgunenuit.roles.*;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import reactor.core.publisher.Mono;
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
                ReactionEmoji.codepoints("U+1F9D1", "U+200D", "U+1F33E"),
                Team.VILLAGE,
                true),
        CHASSEUR("Chasseur",
                "Si le chasseur est exécuté par le village, il peut tuer le joueur de son choix à sa place.",
                ReactionEmoji.codepoints("U+1F3AF"),
                Team.VILLAGE,
                false),
        LOUPGAROU("Loup-Garou",
                "Les Loups-Garous doivent éliminer un membre du village.",
                ReactionEmoji.codepoints("U+1F43A"),
                Team.LG,
                true),
        NOISEUSE("Noiseuse",
                "La noiseuse peut échanger les rôles de deux autres joueurs pendant la nuit.",
                ReactionEmoji.codepoints("U+1F939", "U+200D", "U+2640", "U+FE0F"),
                Team.VILLAGE,
                false),
        SBIRE("Sbire",
                "Le Sbire fait équipe avec les loups-garous. Si le Sbire est éliminé les Loups-Garous gagnent !",
                ReactionEmoji.codepoints("U+1F5E1", "U+FE0F"),
                Team.SBIRE,
                false),
        VOYANTE("Voyante",
                "La Voyante peut observer la carte d'un joueur.",
                ReactionEmoji.codepoints("U+1F52E"),
                Team.VILLAGE,
                false);

        private String name;
        private String description;
        private ReactionEmoji emoji;
        private Team team;
        private boolean multiple;

        RoleID(String name, String description, ReactionEmoji emoji, Team team, boolean multiple) {
            this.name = name;
            this.description = description;
            this.emoji = emoji;
            this.team = team;
            this.multiple = multiple;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public ReactionEmoji getEmoji() {
            return emoji;
        }

        public Team getTeam() {
            return team;
        }

        public boolean isMultiple() {
            return multiple;
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
