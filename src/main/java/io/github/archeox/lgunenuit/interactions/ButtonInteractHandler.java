package io.github.archeox.lgunenuit.interactions;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class ButtonInteractHandler {

    private final HashMap<Snowflake, Function<ButtonInteractEvent, Mono<Void>>> interactions;

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
                                .map(snowflake -> interactions.get(snowflake))
                                .flatMap(func -> func.apply(buttonInteractEvent)))
                .subscribe();
    }

    public boolean registerButtonInteraction(Snowflake id, Function<ButtonInteractEvent, Mono<Void>> event) {
        boolean result = false;
        if (id != null && event != null) {
            interactions.put(id, event);
            result = true;
        }
        return result;
    }
}
