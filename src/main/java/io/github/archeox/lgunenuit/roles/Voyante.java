package io.github.archeox.lgunenuit.roles;

import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageCreateSpec;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.enums.Team;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.LGCard;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import io.github.archeox.lgunenuit.roles.core.Noctambule;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class Voyante extends LGRole implements Noctambule {

    private float turn = 5.0f;

    public Voyante(String name, String description, ReactionEmoji emoji, Team team) {
        super(name, description, emoji, team);
    }

    @Override
    public float getTurn() {
        return turn;
    }

    //TODO : séléctionner deux cartes mystères
    @Override
    public Mono<Void> nightAction(LGGame game, PlayerCard self) {
        List<LGCard> playerOptions = game.getAllCards();
        playerOptions.remove(self);

        List<SelectMenu.Option> options = playerOptions.stream().map(LGCard::toOption).collect(Collectors.toList());

        System.out.println(String.format("\u001B[36m%s\u001B[0m", super.getName()));
        return self.whisper(MessageCreateSpec.create()
                        .withContent("Veuillez choisir un joueur :")
                        .withComponents(ActionRow.of(SelectMenu.of(self.getId().toString(), options)
                                .withMaxValues(1)
                                .withMinValues(1)
                        ))
                )
                .map(Message::getId)
                .map(snowflake -> LGUneNuit.MENU_INTERACT_HANDLER.registerMenuInteraction(snowflake, selectMenuInteractEvent ->
                        selectMenuInteractEvent.reply("Choix enregistrés !\nVoyante")
                                .then(game.nextTurn())
                )).then();
    }
}
