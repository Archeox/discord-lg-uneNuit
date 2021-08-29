package io.github.archeox.lgunenuit.enums;

import io.github.archeox.lgunenuit.roles.*;
import io.github.archeox.lgunenuit.roles.core.LGRole;

public enum RoleID {
    VILLAGEOIS("Villageois", new Villageois()),
    CHASSEUR("Chasseur", new Chasseur()),
    LOUPGAROU("Loup-Garou", new Chasseur()),
    NOISEUSE("Noiseuse", new Noiseuse()),
    SBIRE("Sbire", new Sbire()),
    VOYANTE("Voyante", new Voyante());

    private String name;
    private LGRole role;

    RoleID(String name, LGRole role) {
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public LGRole getRole() {
        return role;
    }
}
