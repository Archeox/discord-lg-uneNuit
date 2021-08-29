package io.github.archeox.lgunenuit.interactions;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class ButtonInteractHandler {

    private final HashMap<Snowflake, Tuple2<Function<ButtonInteractEvent, Mono<Void>>, Boolean>> interactions;

    public ButtonInteractHandler() {
        this.interactions = new HashMap<>();
    }

    public void initalize(GatewayDiscordClient client) {
        client.on(ButtonInteractEvent.class,
                        buttonInteractEvent -> Flux.just(buttonInteractEvent)
                                .map(ButtonInteractEvent::getInteraction)
                                .map(Interaction::getMessage)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .map(Message::getId)
                                .filter(snowflake -> interactions.containsKey(snowflake))
                                .map(snowflake -> {
                                    Tuple2<Function<ButtonInteractEvent, Mono<Void>>, Boolean> pair = interactions.get(snowflake);
                                    if (pair.getT2()) {
                                        return interactions.remove(snowflake).getT1();
                                    } else {
                                        return interactions.get(snowflake).getT1();
                                    }
                                })
                                .flatMap(func -> func.apply(buttonInteractEvent)))
                .subscribe();
    }

    public Mono<Void> registerButtonInteraction(Snowflake id, Function<ButtonInteractEvent, Mono<Void>> event) {
        if (id != null && event != null) {
            interactions.put(id, Tuples.of(event, true));
        }
        return Mono.empty();
    }

    public Mono<Void> registerButtonInteraction(Snowflake id, Function<ButtonInteractEvent, Mono<Void>> event, boolean autoRemove) {
        if (id != null && event != null) {
            interactions.put(id, Tuples.of(event, autoRemove));
        }
        return Mono.empty();
    }

    public void unRegisterInteraction(Snowflake id) {
        interactions.remove(id);
    }
}
