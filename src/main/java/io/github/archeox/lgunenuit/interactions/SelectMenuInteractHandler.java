package io.github.archeox.lgunenuit.interactions;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.SelectMenuInteractEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import io.github.archeox.lgunenuit.utility.MutablePair;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class SelectMenuInteractHandler {

    private final HashMap<Snowflake, MutablePair<Function<SelectMenuInteractEvent, Mono<Void>>, Boolean>> interactions;
    private final HashMap<Snowflake, Publisher<Void>> callBack;

    public SelectMenuInteractHandler() {
        this.interactions = new HashMap<>();
        this.callBack = new HashMap<>();
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
                                .flatMap(func -> func.apply(selectMenuInteractEvent))
                )
                .subscribe();
    }

    public Snowflake registerMenuInteraction(Snowflake id, Function<SelectMenuInteractEvent, Mono<Void>> event) {
        if (id != null && event != null) {
            interactions.put(id, new MutablePair<>(event, true));
            return id;
        } else {
            return null;
        }
    }

    public Snowflake registerMenuInteraction(Snowflake id, Function<SelectMenuInteractEvent, Mono<Void>> event, boolean autoRemove) {
        if (id != null && event != null) {
            interactions.put(id, new MutablePair<>(event, autoRemove));
            return id;
        } else {
            return null;
        }
    }

    public void unRegisterMenuInteraction(Snowflake id) {
        interactions.remove(id);
    }
}
