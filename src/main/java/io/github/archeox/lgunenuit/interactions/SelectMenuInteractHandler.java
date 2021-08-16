package io.github.archeox.lgunenuit.interactions;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Message;
import io.github.archeox.lgunenuit.utility.MutablePair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class SelectMenuInteractHandler {

    private final HashMap<Snowflake, MutablePair<Function<SelectMenuInteractEvent, Mono<Void>>, Boolean>> interactions;

    public SelectMenuInteractHandler() {
        this.interactions = new HashMap<>();
    }

    public void initalize(GatewayDiscordClient client) {
        client.on(SelectMenuInteractEvent.class,
                        selectMenuInteractEvent -> Flux.just(selectMenuInteractEvent)
                                .map(SelectMenuInteractEvent::getInteraction)
                                .map(Interaction::getMessage)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .map(Message::getId)
                                .filter(interactions::containsKey)
                                .map(snowflake -> {
                                    MutablePair<Function<SelectMenuInteractEvent, Mono<Void>>, Boolean> pair = interactions.get(snowflake);
                                    if (pair.getValue()) {
                                        return interactions.remove(snowflake).getKey();
                                    } else {
                                        return interactions.get(snowflake).getKey();
                                    }
                                })
                                .flatMap(func -> func.apply(selectMenuInteractEvent)))
                .subscribe();
    }

    public boolean registerMenuInteraction(Snowflake id, Function<SelectMenuInteractEvent, Mono<Void>> event) {
        boolean result = false;
        if (id != null && event != null) {
            interactions.put(id, new MutablePair<>(event, true));
            result = true;
        }
        return result;
    }

    public boolean registerMenuInteraction(Snowflake id, Function<SelectMenuInteractEvent, Mono<Void>> event, boolean autoRemove) {
        boolean result = false;
        if (id != null && event != null) {
            interactions.put(id, new MutablePair<>(event, autoRemove));
            result = true;
        }
        return result;
    }

    public void unRegisterMenuInteraction(Snowflake id){
        interactions.remove(id);
    }
}
