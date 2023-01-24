package ru.mindustry.bot;

import static arc.graphics.Color.scarlet;
import static arc.util.Strings.getSimpleMessage;
import static mindustry.graphics.Pal.accent;
import static net.dv8tion.jda.api.interactions.commands.build.Commands.slash;
import static net.dv8tion.jda.api.utils.FileUpload.fromData;
import static ru.mindustry.bot.Vars.*;

import arc.func.Cons;
import arc.graphics.Color;
import arc.struct.ObjectMap;
import arc.util.UnsafeRunnable;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import ru.mindustry.bot.components.ContentHandler;

public class Listener extends ListenerAdapter {

    private static final ArrayList<SlashCommandData> rawCommands = new ArrayList<>();
    private static final ObjectMap<SlashCommandData, Cons<SlashCommandInteractionEvent>> commandsData = new ObjectMap<>();

    private static void register(SlashCommandData data, Cons<SlashCommandInteractionEvent> runner) {
        commandsData.put(data, runner);
    }

    private static void loadCommands(@NotNull Guild guild) {
        commandsData.forEach(command -> rawCommands.add(command.key));
        guild.updateCommands().addCommands(rawCommands).queue();
    }

    public static void registerCommands() {
        register(
            slash("map", "Отправить карту в специальный канал")
                .addOption(OptionType.ATTACHMENT, "map", "Карта, которая будет отправлена в специальный канал", true),
            event -> {
                var member = event.getMember();
                var attachment = Objects.requireNonNull(event.getOption("map")).getAsAttachment();

                if (!Objects.equals(attachment.getFileExtension(), "msav")) {
                    reply(event, ":warning: Ошибка", ":link: Необходимо прикрепить файл с расширением **.msav**", scarlet);
                    return;
                }

                attachment
                    .getProxy()
                    .downloadToFile(cache.child(attachment.getFileName()).file())
                    .thenAccept(file ->
                        tryWorkWithFile(
                            file,
                            () -> {
                                var map = ContentHandler.parseMap(file);
                                var image = ContentHandler.parseMapImage(map);

                                var embed = new EmbedBuilder()
                                    .setTitle(map.name())
                                    .setDescription(map.description())
                                    .setAuthor(
                                        Objects.requireNonNull(member).getEffectiveName(),
                                        attachment.getUrl(),
                                        member.getEffectiveAvatarUrl()
                                    )
                                    .setFooter(map.width + "x" + map.height)
                                    .setColor(accent.argb8888())
                                    .setImage("attachment://image.png");

                                mapsChannel
                                    .sendMessageEmbeds(embed.build())
                                    .addFiles(
                                        fromData(image, "image.png"),
                                        fromData(attachment.getProxy().download().get(), attachment.getFileName())
                                    )
                                    .queue();

                                reply(event, ":map: Успешно", "Карта отправлена в " + mapsChannel.getAsMention(), accent);
                            },
                            t -> reply(event, ":warning: Ошибка", getSimpleMessage(t), scarlet)
                        )
                    );
            }
        );

        register(
            slash("schematic", "Отправить схему в специальный канал")
                .addOption(OptionType.ATTACHMENT, "schematic", "Схема, которая будет отправлена в специальный канал", true),
            event -> {
                var member = event.getMember();
                var attachment = Objects.requireNonNull(event.getOption("schematic")).getAsAttachment();

                if (!Objects.equals(attachment.getFileExtension(), "msch")) {
                    reply(event, ":warning: Ошибка", ":link: Необходимо прикрепить файл с расширением **.msch**", scarlet);
                    return;
                }

                attachment
                    .getProxy()
                    .downloadToFile(cache.child(attachment.getFileName()).file())
                    .thenAccept(file ->
                        tryWorkWithFile(
                            file,
                            () -> {
                                var schematic = ContentHandler.parseSchematic(file);
                                var image = ContentHandler.parseSchematicImage(schematic);

                                var builder = new StringBuilder();
                                schematic
                                    .requirements()
                                    .each((item, amount) -> builder.append(item.localizedName).append(": ").append(amount).append("; "));

                                var embed = new EmbedBuilder()
                                    .setTitle(schematic.name())
                                    .setDescription(schematic.description())
                                    .setAuthor(
                                        Objects.requireNonNull(member).getEffectiveName(),
                                        attachment.getUrl(),
                                        member.getEffectiveAvatarUrl()
                                    )
                                    .addField("Необходимые ресурсы", builder.toString(), true)
                                    .setFooter(schematic.width + "x" + schematic.height + ", " + schematic.tiles.size + " blocks")
                                    .setColor(accent.argb8888())
                                    .setImage("attachment://image.png");

                                schematicsChannel
                                    .sendMessageEmbeds(embed.build())
                                    .addFiles(
                                        fromData(image, "image.png"),
                                        fromData(attachment.getProxy().download().get(), attachment.getFileName())
                                    )
                                    .queue();

                                reply(event, ":wrench: Успешно", "Схема отправлена в " + schematicsChannel.getAsMention(), accent);
                            },
                            t -> reply(event, ":warning: Ошибка", getSimpleMessage(t), scarlet)
                        )
                    );
            }
        );

        loadCommands(Vars.guild);
    }

    private static void reply(SlashCommandInteractionEvent event, String title, String description, Color color) {
        event
            .replyEmbeds(new EmbedBuilder().setTitle(title).setDescription(description).setColor(color.argb8888()).build())
            .setEphemeral(true)
            .queue();
    }

    private static void tryWorkWithFile(File file, UnsafeRunnable runnable, Cons<Throwable> error) {
        try {
            runnable.run();
        } catch (Throwable t) {
            error.get(t);
        } finally {
            file.deleteOnExit();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commandsData.forEach(command -> {
            if (command.key.getName().equals(event.getName())) command.value.get(event);
        });
    }
}
