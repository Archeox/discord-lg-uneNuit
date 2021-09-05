package io.github.archeox.lgunenuit.roles;

import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import io.github.archeox.lgunenuit.roles.core.Noctambule;
import io.github.archeox.lgunenuit.enums.Team;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class Noiseuse extends LGRole implements Noctambule {

    private float turn = 7.0f;

    public Noiseuse(String name, String description, ReactionEmoji emoji, Team team) {
        super(name, description, emoji, team);
    }

    @Override
    public float getTurn() {
        return this.turn;
    }

    @Override
    public Mono<Void> nightAction(LGGame game, PlayerCard self) {

//        List<PlayerCard> playerList = game.getMembersPlayers();
//        playerList.remove(self);

//        List<SelectMenu.Option> options = new ArrayList<>();
//        for (PlayerCard player : playerList) {
//            options.add(SelectMenu.Option.of(player.getMember().getDisplayName(), player.getId().toString()));
//        }

        List<SelectMenu.Option> options = Arrays.asList(SelectMenu.Option.of("option 1", "foo"),
                SelectMenu.Option.of("option 2", "bar"),
                SelectMenu.Option.of("option 3", "baz"));


        System.out.println(String.format("\u001B[36m%s\u001B[0m", super.getName()));

        return self.whisper(messageCreateSpec -> {
                    messageCreateSpec.setContent("Veuillez choisir deux joueurs :");
                    messageCreateSpec.setComponents(ActionRow.of(SelectMenu.of(self.getId().toString(), options)
                            .withMaxValues(2)
                            .withMinValues(2)
                    ));
                })
                .map(Message::getId)
                .map(snowflake -> LGUneNuit.MENU_INTERACT_HANDLER.registerMenuInteraction(snowflake, selectMenuInteractEvent ->
                        selectMenuInteractEvent.reply("Choix enregistrés !\nVoyante")
                                .then(game.nextTurn())
                )).then();

    }
}
