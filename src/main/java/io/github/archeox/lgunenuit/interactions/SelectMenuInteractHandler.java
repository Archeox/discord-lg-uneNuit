package io.github.archeox.lgunenuit.interactions;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class SelectMenuInteractHandler {

    private final HashMap<Snowflake, Tuple2<Function<SelectMenuInteractionEvent, Mono<Void>>, Boolean>> interactions;
    private final HashMap<Snowflake, Publisher<Void>> callBack;

    public SelectMenuInteractHandler() {
        this.interactions = new HashMap<>();
        this.callBack = new HashMap<>();
    }

    public void initalize(GatewayDiscordClient client) {
        client.on(SelectMenuInteractionEvent.class,
                        selectMenuInteractEvent -> Flux.just(selectMenuInteractEvent)
                                .map(SelectMenuInteractionEvent::getInteraction)
                                .map(Interaction::getMessage)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .map(Message::getId)
                                .filter(interactions::containsKey)
                                .map(snowflake -> {
                                    Tuple2<Function<SelectMenuInteractionEvent, Mono<Void>>, Boolean> pair = interactions.get(snowflake);
                                    if (pair.getT2()) {
                                        return interactions.remove(snowflake).getT1();
                                    } else {
                                        return interactions.get(snowflake).getT1();
                                    }
                                })
                                .flatMap(func -> func.apply(selectMenuInteractEvent))
                )
                .subscribe();
    }

    public Snowflake registerMenuInteraction(Snowflake id, Function<SelectMenuInteractionEvent, Mono<Void>> event) {
        if (id != null && event != null) {
            interactions.put(id, Tuples.of(event, true));
            return id;
        } else {
            return null;
        }
    }

    public Snowflake registerMenuInteraction(Snowflake id, Function<SelectMenuInteractionEvent, Mono<Void>> event, boolean autoRemove) {
        if (id != null && event != null) {
            interactions.put(id, Tuples.of(event, autoRemove));
            return id;
        } else {
            return null;
        }
    }

    public void unRegisterMenuInteraction(Snowflake id) {
        interactions.remove(id);
    }
}
