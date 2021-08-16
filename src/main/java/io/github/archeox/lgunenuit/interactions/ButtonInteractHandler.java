package io.github.archeox.lgunenuit.interactions;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Message;
import io.github.archeox.lgunenuit.utility.MutablePair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class ButtonInteractHandler {

    private final HashMap<Snowflake, MutablePair<Function<ButtonInteractEvent, Mono<Void>>, Boolean>> interactions;

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
                                    MutablePair<Function<ButtonInteractEvent, Mono<Void>>, Boolean> pair = interactions.get(snowflake);
                                    if (pair.getValue()) {
                                        return interactions.remove(snowflake).getKey();
                                    } else {
                                        return interactions.get(snowflake).getKey();
                                    }
                                })
                                .flatMap(func -> func.apply(buttonInteractEvent)))
                .subscribe();
    }

    public boolean registerButtonInteraction(Snowflake id, Function<ButtonInteractEvent, Mono<Void>> event) {
        boolean result = false;
        if (id != null && event != null) {
            interactions.put(id, new MutablePair<>(event, true));
            result = true;
        }
        return result;
    }

    public boolean registerButtonInteraction(Snowflake id, Function<ButtonInteractEvent, Mono<Void>> event, boolean autoRemove) {
        boolean result = false;
        if (id != null && event != null) {
            interactions.put(id, new MutablePair<>(event, autoRemove));
            result = true;
        }
        return result;
    }

    public void unRegisterInteraction(Snowflake id) {
        interactions.remove(id);
    }
}
