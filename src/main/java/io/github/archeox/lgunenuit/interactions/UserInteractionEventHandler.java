package io.github.archeox.lgunenuit.interactions;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.UserInteractionEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class UserInteractionEventHandler {

    private GatewayDiscordClient client;
    private final HashMap<Snowflake, Function<UserInteractionEvent, Mono<Void>>> interactions;

    public UserInteractionEventHandler() {
        this.interactions = new HashMap<>();
    }

    public void initalize(GatewayDiscordClient client) {
        this.client = client;

        client.on(UserInteractionEvent.class,
                        userInteractionEvent -> Flux.just(userInteractionEvent)
                                .map(UserInteractionEvent::getInteraction)
                                .map(Interaction::getMessage)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .map(Message::getId)
                                .filter(interactions::containsKey)
                                .map(snowflake -> interactions.get(snowflake))
                                .flatMap(func -> func.apply(userInteractionEvent))
                )
                .subscribe();
    }

    public void initializeGlobalCommand(ApplicationCommandRequest request, Function<UserInteractionEvent, Mono<Void>> event) {
        Mono.just(client.getRestClient())
                .flatMap(restClient ->
                        restClient.getApplicationId()
                                .flatMap(id ->
                                        restClient.getApplicationService().createGlobalApplicationCommand(id, request)
                                )
                                .map(ApplicationCommandData::id)
                ).subscribe(s -> interactions.put(Snowflake.of(s), event));
    }

    public void deleteGlobalCommands() {
        Mono.just(client.getRestClient())
                .flatMapMany(restClient ->
                        restClient.getApplicationId()
                                .flatMapMany(id -> restClient.getApplicationService().getGlobalApplicationCommands(id)
                                        .map(ApplicationCommandData::id)
                                        .flatMap(commandId ->
                                                restClient.getApplicationService().deleteGlobalApplicationCommand(id, Long.parseLong(commandId))
                                        )
                                )
                )
                .subscribe();
    }


    //FOR DEBUG
    public void initializeGuildCommand(long guildId, ApplicationCommandRequest request, Function<UserInteractionEvent, Mono<Void>> event) {
        Mono.just(client.getRestClient())
                .flatMap(restClient ->
                        restClient.getApplicationId()
                                .flatMap(id ->
                                        restClient.getApplicationService().createGuildApplicationCommand(id, guildId, request)
                                )
                                .map(ApplicationCommandData::id)
                ).subscribe(s -> interactions.put(Snowflake.of(s), event));
    }

    public void deleteGuildCommand(long guildId) {
        Mono.just(client.getRestClient())
                .map(restClient ->
                        restClient.getApplicationId()
                                .map(id -> restClient.getApplicationService().getGuildApplicationCommands(id, guildId)
                                        .map(ApplicationCommandData::id)
                                        .map(commandId ->
                                                restClient.getApplicationService().deleteGuildApplicationCommand(id, guildId, Long.parseLong(commandId))
                                        )
                                )
                ).subscribe();
    }

}
