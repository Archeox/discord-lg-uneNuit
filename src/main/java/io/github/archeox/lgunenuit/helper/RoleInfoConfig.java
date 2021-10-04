package io.github.archeox.lgunenuit.helper;

import discord4j.core.object.reaction.ReactionEmoji;
import io.github.archeox.lgunenuit.enums.Team;
import io.github.archeox.lgunenuit.exception.RoleConfigException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class RoleInfoConfig {

    private static final Charset TEXT_FILE_CHARSET = StandardCharsets.UTF_8;

    private String name;
    private String description;
    private ReactionEmoji emoji;
    private Team team;

    public RoleInfoConfig(String filepath) throws RoleConfigException {
        Properties properties = new Properties();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filepath), TEXT_FILE_CHARSET))) {
            properties.load(br);
            this.name = (String) properties.get("name");
            this.description = (String) properties.get("description");
            String[] codepoints = ((String) properties.get("codepoints")).split(",");
            this.emoji = ReactionEmoji.codepoints(codepoints);
            this.team = Team.valueOf((String) properties.get("team"));
        } catch (IOException ex) {
            throw new RoleConfigException("Problem while loading config file : " + filepath, ex.getMessage());
        }
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
}
