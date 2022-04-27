package de.kaleidox.testbot;

import org.comroid.common.io.FileHandle;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.util.logging.ExceptionLogger;

import java.util.Arrays;
import java.util.List;

public class Bot {
    public static final FileHandle BASE_DIR = new FileHandle("/srv/dcb/CobaltonTestBot");

    public static void main(String[] args) {
        final DiscordApi api = new DiscordApiBuilder()
                .setToken(BASE_DIR.createSubFile("token.txt").getContent(true))
                .login()
                .join();

        SlashCommand.with("hello2", "Hello World!")
                .addOption(SlashCommandOption.createStringOption("userId", "Greet this user", true))
                .createGlobal(api)
                .exceptionally(ExceptionLogger.get());

        api.addSlashCommandCreateListener(cmd -> {
            switch (cmd.getSlashCommandInteraction().getCommandName()) {
                case "hello":
                    List<SlashCommandInteractionOption> options = cmd.getSlashCommandInteraction().getArguments();

                    String targetUserId = options.get(0).getStringValue().orElseThrow(RuntimeException::new);
                    String mentionTag = api.getUserById(Long.parseLong(targetUserId))
                            .join()
                            .getNicknameMentionTag();

                    cmd.getSlashCommandInteraction()
                            .getChannel()
                            .orElseThrow(RuntimeException::new)
                            .sendMessage(new EmbedBuilder()
                                    .setTitle("Hello, world!")
                                    .setDescription("Specifically hello to you, " + mentionTag))
                            .exceptionally(ExceptionLogger.get());

                    break;
                default:
                    throw new RuntimeException("Invalid command used: " + cmd);
            }
        });

        System.out.println("Ready!");
    }
}
