package io.github.archeox.lgunenuit.roles;

import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.LGCard;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.game.LGRole;
import io.github.archeox.lgunenuit.game.Noctambule;
import io.github.archeox.lgunenuit.utility.Team;

import java.util.List;
import java.util.stream.Collectors;

public class Voyante extends LGRole implements Noctambule {

    private final float turn;

    public Voyante(float turn) {
        super("Voyante", "La Voyante peut observer la carte d'un joueur", Team.VILLAGE);
        this.turn = turn;
    }

    @Override
    public float getTurn() {
        return turn;
    }

    //TODO : séléctionner deux cartes mystères
    @Override
    public void nightAction(LGGame game, PlayerCard self) {
        List<LGCard> playerOptions = game.getAllCards();
        playerOptions.remove(self);

        List<SelectMenu.Option> options = playerOptions.stream().map(LGCard::toOption).collect(Collectors.toList());

        System.out.println(String.format("\u001B[36m%s\u001B[0m", super.getName()));
        self.whisper(messageCreateSpec -> {
                    messageCreateSpec.setContent("Veuillez choisir un joueur :");
                    messageCreateSpec.setComponents(ActionRow.of(SelectMenu.of(self.getId().toString(), options)
                            .withMaxValues(1)
                            .withMinValues(1)
                    ));
                })
                .map(Message::getId)
                .map(snowflake -> LGUneNuit.MENU_INTERACT_HANDLER.registerMenuInteraction(snowflake, selectMenuInteractEvent ->
                        selectMenuInteractEvent.reply("Choix enregistrés !\nVoyante")
                                .then(game.nextTurn())
                ))
                .subscribe();
    }
}
