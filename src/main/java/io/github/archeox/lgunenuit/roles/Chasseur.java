package io.github.archeox.lgunenuit.roles;

import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import io.github.archeox.lgunenuit.roles.core.SpecialVoter;
import io.github.archeox.lgunenuit.enums.Team;
import io.github.archeox.lgunenuit.game.Vote;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class Chasseur extends LGRole implements SpecialVoter {

    private final float priority;

    public Chasseur(String name, String description, String emoji, Team team) {
        super(name, description, emoji, team);
        this.priority = 3;
    }

    @Override
    public float getPriority() {
        return priority;
    }

    @Override
    public Mono<Void> voteAction(LGGame game, PlayerCard self, Vote vote) {
        if (vote.getWinner().equals(self)) {

            return game.postAnnounce(String.format("Le village a décidé d'éliminer %s, qui était Chasseur ! C'est maintenant " +
                                    "à lui de décider qui il va éliminer avec son fusil..."
                            , self.getMember().getDisplayName()))
                    .then(
                    self.whisper(messageCreateSpec -> {
                        messageCreateSpec.setContent("Choisissez le joueur qui va mourir de votre fusil :");
                        messageCreateSpec.setComponents(ActionRow.of(SelectMenu.of("chasseur", game.getMembersCards().stream().map(PlayerCard::toOption).collect(Collectors.toList()))
                                .withMinValues(1).withMaxValues(1)
                        ));
                    }))
                    .map(Message::getId)
                    .map(snowflake -> LGUneNuit.MENU_INTERACT_HANDLER.registerMenuInteraction(snowflake, selectMenuInteractEvent -> {
                        vote.setWinner(game.getCardById(selectMenuInteractEvent.getValues().get(0)));
                        return selectMenuInteractEvent.reply("Votre choix a été enregistré !").then(game.nextSpecialVoter());
                    })).then();
        }else {
            return game.nextSpecialVoter();
        }
    }
}

