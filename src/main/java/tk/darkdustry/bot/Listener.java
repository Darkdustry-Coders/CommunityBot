package tk.darkdustry.bot;

import arc.func.Cons;
import arc.graphics.Color;
import arc.util.UnsafeRunnable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tk.darkdustry.bot.components.ContentHandler;

import java.io.File;
import java.util.Objects;

import static arc.graphics.Color.scarlet;
import static arc.util.Strings.*;
import static java.util.Objects.requireNonNull;
import static mindustry.graphics.Pal.accent;
import static net.dv8tion.jda.api.utils.FileUpload.fromData;
import static tk.darkdustry.bot.Vars.*;

public class Listener extends ListenerAdapter {

    public static void loadCommands(String prefix) {
        handler.setPrefix(prefix);

        handler.<Message>register("postmap", "Отправить карту в специальный канал.", (args, message) -> {
            if (message.getAttachments().size() != 1 || !Objects.equals(message.getAttachments().get(0).getFileExtension(), "msav")) {
                reply(message, ":warning: Ошибка", ":link: Необходимо прикрепить 1 файл с расширением **.msav**", scarlet);
                return;
            }

            var attachment = message.getAttachments().get(0);

            attachment.getProxy().downloadToFile(cache.child(attachment.getFileName()).file()).thenAccept(file -> tryWorkWithFile(file, () -> {
                var map = ContentHandler.parseMap(file);
                var image = ContentHandler.parseMapImage(map);

                var embed = new EmbedBuilder()
                        .setTitle(map.name())
                        .setDescription(map.description())
                        .setAuthor(requireNonNull(message.getMember()).getEffectiveName(), message.getMember().getEffectiveAvatarUrl(), message.getMember().getEffectiveAvatarUrl())
                        .setFooter(map.width + "x" + map.height)
                        .setColor(accent.argb8888())
                        .setImage("attachment://image.png");

                mapsWebhook.sendMessageEmbeds(embed.build()).addFiles(fromData(image, "image.png")).queue(queue -> reply(message, ":white_check_mark: Успешно", ":map: Карта отправлена в " + mapsWebhook.getChannel().getAsMention(), accent));
            }, t -> reply(message, ":warning: Ошибка", getSimpleMessage(t), scarlet)));
        });

        handler.<Message>register("postschem", "Отправить схему в специальный канал.", (args, message) -> {
            if (message.getAttachments().size() != 1 || !Objects.equals(message.getAttachments().get(0).getFileExtension(), "msch")) {
                reply(message, ":warning: Ошибка", ":link: Необходимо прикрепить 1 файл с расширением **.msch**", scarlet);
                return;
            }

            var attachment = message.getAttachments().get(0);

            attachment.getProxy().downloadToFile(cache.child(attachment.getFileName()).file()).thenAccept(file -> tryWorkWithFile(file, () -> {
                var schematic = ContentHandler.parseSchematic(file);
                var image = ContentHandler.parseSchematicImage(schematic);

                var builder = new StringBuilder();
                schematic.requirements().each((item, amount) -> {
                    if (emojis.containsKey(item)) builder.append("<:").append(item.name.replaceAll("-", "")).append(":").append(emojis.get(item)).append(">");
                    else builder.append(item.name);
                    builder.append(" ").append(amount).append(" ");
                });

                var embed = new EmbedBuilder()
                        .setTitle(schematic.name())
                        .setDescription(schematic.description())
                        .setAuthor(requireNonNull(message.getMember()).getEffectiveName(), message.getMember().getEffectiveAvatarUrl(), message.getMember().getEffectiveAvatarUrl())
                        .addField("Ресурсы для постройки", builder.toString(), true)
                        .setFooter(schematic.width + "x" + schematic.height + ", " + schematic.tiles.size + " блоков")
                        .setColor(accent.argb8888())
                        .setImage("attachment://image.png");

                schematicsWebhook.sendMessageEmbeds(embed.build()).addFiles(fromData(image, "image.png")).queue(queue -> reply(message, ":white_check_mark: Успешно", ":wrench: Схема отправлена в " + schematicsWebhook.getChannel().getAsMention(), accent));
            }, t -> reply(message, ":warning: Ошибка", getSimpleMessage(t), scarlet)));
        });
    }

    private static void reply(Message message, String title, String description, Color color) {
        message.replyEmbeds(new EmbedBuilder().setTitle(title).setDescription(description).setColor(color.argb8888()).build()).queue();
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
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild() || event.getAuthor().isBot()) return;

        handler.handleMessage(event.getMessage().getContentRaw(), event.getMessage());
    }
}