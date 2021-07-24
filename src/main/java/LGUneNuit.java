import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class LGUneNuit {

    private final static String TOKEN_FILE = "token.secret";
    private final static Charset TOKEN_CHARSET = StandardCharsets.UTF_8;
    private String test;


    public static void main(String[] args) {

        try {
            GatewayDiscordClient client = DiscordClientBuilder.create(readToken())
                    .build()
                    .login()
                    .block();

            client.getEventDispatcher().on(ReadyEvent.class)
                    .subscribe(event -> {
                        final User self = event.getSelf();
                        System.out.println(String.format(
                                "\u001B[32mLogged in as %s#%s\u001B[0m", self.getUsername(), self.getDiscriminator()
                        ));
                    });

            client.getEventDispatcher().on(MessageCreateEvent.class)
                    .map(MessageCreateEvent::getMessage)
                    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                    .filter(message -> message.getContent().equalsIgnoreCase("!ping"))
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("Pong!"))
                    .subscribe();

            client.onDisconnect().block();
        } catch (IOException e) {
            System.err.println("Error while reading the token file");
        }
    }

    private static String readToken() throws IOException {

        BufferedReader br = null;
        String result = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(TOKEN_FILE), TOKEN_CHARSET));

            result = br.readLine();
        } catch (Exception e) {
            throw new IOException("Error while reading the token file");
        }
        return result;

    }

}
