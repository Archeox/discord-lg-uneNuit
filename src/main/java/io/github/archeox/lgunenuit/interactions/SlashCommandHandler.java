package io.github.archeox.lgunenuit.interactions;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.function.Function;

public class SlashCommandHandler {

    private GatewayDiscordClient client;
    private final HashMap<Snowflake, Function<ChatInputInteractionEvent, Mono<Void>>> interactions;

    public SlashCommandHandler() {
        this.interactions = new HashMap<>();
    }

    public void intialize(GatewayDiscordClient client) {
        this.client = client;

        this.client.on(ChatInputInteractionEvent.class,
                        selectMenuInteractEvent -> Flux.just(selectMenuInteractEvent)
                                .map(ChatInputInteractionEvent::getCommandId)
                                .filter(interactions::containsKey)
                                .map(snowflake -> interactions.get(snowflake))
                                .flatMap(func -> func.apply(selectMenuInteractEvent))
                )
                .subscribe();
    }

    public void initializeGlobalCommand(ApplicationCommandRequest request, Function<ChatInputInteractionEvent, Mono<Void>> event) {
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
    public void initializeGuildCommand(long guildId, ApplicationCommandRequest request, Function<ChatInputInteractionEvent, Mono<Void>> event) {
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
