package io.github.archeox.lgunenuit.utility;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.SelectMenuInteractEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Message;
import io.github.archeox.lgunenuit.LGUneNuit;
import io.github.archeox.lgunenuit.game.LGGame;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class InteractionManager {

    private Map<Snowflake, Function<SelectMenuInteractEvent, Mono<Void>>> menuMap;

    public InteractionManager() {

        menuMap = new HashMap<>();

        LGUneNuit.client.on(SelectMenuInteractEvent.class)
                .map(SelectMenuInteractEvent::getInteraction)
                .map(Interaction::getMessage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Message::getId)
                .map(snowflake -> menuMap.get(snowflake))
                .filter()
                .flatMap(func -> func.apply(SelectMenuInteractEvent))
                .map()
                .subscribe();

//        LGUneNuit.client.on(SelectMenuInteractEvent.class, selectMenuInteractEvent ->{
//            if (selectMenuInteractEvent.getInteraction().getMessage().isPresent()) {
//                Snowflake evSnowflake = selectMenuInteractEvent.getInteraction().getMessage().get().getId();
//                 Function func = menuMap.get(evSnowflake);
//                 if (func != null) {
//                     func.apply(selectMenuInteractEvent);
//                 }
//            }
//        });


    }

    public Mono<Void> menuInteractionRegister(Snowflake id, Function<SelectMenuInteractEvent, Mono<Void>> function) {
        menuMap.put(id, function);
    }

    public void menuUnregister(Snowflake id) {
        menuMap.remove(id, menuMap.get(id));
    }

}
