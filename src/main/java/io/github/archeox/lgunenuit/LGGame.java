package io.github.archeox.lgunenuit;

import discord4j.core.object.entity.Member;
import io.github.archeox.lgunenuit.roles.LGRole;

import java.util.List;

public class LGGame {

    private List<Member> players;
    private List<LGRole> roles;

    public LGGame(List<Member> players, List<LGRole> roles) {
        this.players = players;
        this.roles = roles;
    }

    public void startGame(){

    }
}
