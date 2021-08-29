package io.github.archeox.lgunenuit.roles;

import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.game.LGGame;
import io.github.archeox.lgunenuit.game.card.PlayerCard;
import io.github.archeox.lgunenuit.roles.core.LGRole;
import io.github.archeox.lgunenuit.roles.core.Noctambule;
import io.github.archeox.lgunenuit.enums.Team;
import io.github.archeox.lgunenuit.game.card.MysteryCard;
import java.util.ArrayList;
import java.util.List;
import reactor.core.publisher.Mono;

public class LoupGarou extends LGRole implements Noctambule {

    private float turn = 2.0f;

    public LoupGarou(float turn) {
        super("Loup-Garou", "Le but du Loup-Garou est d'Ã©liminer un membre du village.\n"
                + "Il peut de savoir qui sont les autres Loups-Garous. S'il n'y en a pas, il a le droit de regarder une carte au milieu.",
                Team.LG);
        this.turn = turn;
    }

    public LoupGarou() {
        super("Loup-Garou", "Le but du Loup-Garou est d'Ã©liminer un membre du village.\n" +
                        "Il peut de savoir qui sont les autres Loups-Garous. S'il n'y en a pas, il a le droit de regarder une carte au milieu."
                , Team.LG);
        this.turn = turn;
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

            return self.whisper((t) -> {
                t.setContent("Vous êtes le seul Loup-Garou !\nVous pouvez observer une des cartes mystères !");
                t.setComponents(ActionRow.of(SelectMenu.of("lg", options)));
            })
                    .map(Message::getId)
                    .flatMap(id
                            -> LGUneNuit.MENU_INTERACT_HANDLER.registerMenuInteraction(id, (t) -> {
                        return t.reply("Cette carte est " + game.getCardById(t.getValues().get(0))).
                                thenEmpty(game.nextTurn());
                    })
                    );
        }
    }
}
