package io.github.archeox.lgunenuit.interactions;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.SelectMenuInteractEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class SelectMenuInteractHandler {

    private final HashMap<Snowflake, Function<SelectMenuInteractEvent, Mono<Void>>> interactions;

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
                                .filter(snowflake -> interactions.containsKey(snowflake))
                                .map(snowflake -> interactions.get(snowflake))
                                .flatMap(func -> func.apply(selectMenuInteractEvent)))
                .subscribe();
    }

    public boolean registerMenuInteraction(Snowflake id, Function<SelectMenuInteractEvent, Mono<Void>> event) {
        boolean result = false;
        if (id != null && event != null) {
            interactions.put(id, event);
            result = true;
        }
        return result;
    }
}
