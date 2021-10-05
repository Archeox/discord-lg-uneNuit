package io.github.archeox.lgunenuit.roles;

import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageCreateSpec;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.enums.Team;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.MysteryCard;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import io.github.archeox.lgunenuit.roles.core.Noctambule;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class LoupGarou extends LGRole implements Noctambule {

    private float turn = 2.0f;

    public LoupGarou(String name, String description, ReactionEmoji emoji, Team team) {
        super(name, description, emoji, team);
    }

    @Override
    public float getTurn() {
        return this.turn;
    }

    @Override
    public Mono<Void> nightAction(LGGame game, PlayerCard self) {

        List<PlayerCard> otherLGs = game.onlyMemberCard(game.getCardsByAttributedRole(LoupGarou.class));

        if (otherLGs.size() > 1) {

            String msg = "Les autres loups sont :";
            for (PlayerCard lg : otherLGs) {
                msg += ("\n" + lg.getMember().getDisplayName());
            }

            return self.whisper(msg).thenEmpty(game.nextTurn());
        } else {

            List<SelectMenu.Option> options = new ArrayList<>();
            for (MysteryCard card : game.getMysteryCards()) {
                options.add(card.toOption());
            }

            return self.whisper(MessageCreateSpec.create()
                            .withContent("Vous �tes le seul Loup-Garou !\nVous pouvez observer une des cartes mystères !")
                            .withComponents(ActionRow.of(SelectMenu.of("lg", options)))
                    )
                    .map(Message::getId)
                    .map(id -> LGUneNuit.MENU_INTERACT_HANDLER.registerMenuInteraction(id,
                            (t) -> t.reply("Cette carte est " + game.getCardById(t.getValues().get(0)))
                                    .thenEmpty(game.nextTurn()))
                    ).then();
        }
    }
}
