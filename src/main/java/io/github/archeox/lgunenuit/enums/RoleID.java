package io.github.archeox.lgunenuit.enums;

import io.github.archeox.lgunenuit.roles.*;
import io.github.archeox.lgunenuit.roles.core.LGRole;

import java.util.function.Function;
import java.util.function.Supplier;

public enum RoleID {
    VILLAGEOIS("Villageois", () -> new Villageois()),
    CHASSEUR("Chasseur", () -> new Chasseur()),
    LOUPGAROU("Loup-Garou", () -> new Chasseur()),
    NOISEUSE("Noiseuse", () -> new Noiseuse()),
    SBIRE("Sbire", () -> new Sbire()),
    VOYANTE("Voyante", () -> new Voyante());

    private String name;
    private Supplier<? extends LGRole> factory;

    RoleID(String name, Supplier<? extends LGRole> factory) {
        this.name = name;
        this.factory = factory;
    }

    public String getName() {
        return name;
    }

    public LGRole getFactory() {
        return factory.get();
    }
}
