package tk.darkdustry.bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import tk.darkdustry.bot.components.ContentHandler;

import java.util.Objects;

import static arc.graphics.Color.scarlet;
import static arc.util.Strings.getSimpleMessage;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mindustry.graphics.Pal.accent;
import static tk.darkdustry.bot.Vars.*;

public class Listener extends ListenerAdapter {

    private static void parseMap(MessageReceivedEvent event) {
        var attachments = event.getMessage().getAttachments();
        if (attachments.size() != 1 || !Objects.equals(attachments.get(0).getFileExtension(), "msav")) {
            replyError(event, ":link: Необходимо прикрепить 1 файл с расширением **.msav**");
            return;
        }

        var attachment = attachments.get(0);

        attachment.getProxy().downloadToFile(cache.child(attachment.getFileName()).file()).thenAccept(file -> {
            try {
                var map = ContentHandler.parseMap(file);
                var image = ContentHandler.parseMapImage(map);

                var embed = new EmbedBuilder()
                        .setTitle(map.name())
                        .setDescription(map.description())
                        .setAuthor(event.getMember().getEffectiveName(), event.getMember().getEffectiveAvatarUrl(), event.getMember().getEffectiveAvatarUrl())
                        .setFooter(map.width + "x" + map.height)
                        .setColor(accent.argb8888())
                        .setImage("attachment://image.png");

                event.getChannel().sendMessageEmbeds(embed.build()).addFiles(FileUpload.fromData(image, "image.png")).queue(message -> event.getMessage().delete().queue());
            } catch (Exception e) {
                file.delete();
                replyError(event, getSimpleMessage(e));
            }
        });
    }

    private static void parseSchematic(MessageReceivedEvent event) {
        var attachments = event.getMessage().getAttachments();
        if (attachments.size() != 1 || !Objects.equals(attachments.get(0).getFileExtension(), "msch")) {
            replyError(event, ":link: Необходимо прикрепить 1 файл с расширением **.msch**");
            return;
        }

        var attachment = attachments.get(0);

        attachment.getProxy().downloadToFile(cache.child(attachment.getFileName()).file()).thenAccept(file -> {
            try {
                var schematic = ContentHandler.parseSchematic(file);
                var image = ContentHandler.parseSchematicImage(schematic);

                var embed = new EmbedBuilder()
                        .setTitle(schematic.name())
                        .setDescription(schematic.description())
                        .setAuthor(event.getMember().getEffectiveName(), event.getMember().getEffectiveAvatarUrl(), event.getMember().getEffectiveAvatarUrl())
                        .setFooter(schematic.width + "x" + schematic.height + ", " + schematic.tiles.size + " блоков")
                        .setColor(accent.argb8888())
                        .setImage("attachment://image.png");

                event.getChannel().sendMessageEmbeds(embed.build()).addFiles(FileUpload.fromData(image, "image.png")).queue(message -> event.getMessage().delete().queue());
            } catch (Exception e) {
                file.delete();
                replyError(event, getSimpleMessage(e));
            }
        });
    }

    private static void replyError(MessageReceivedEvent event, String text) {
        event.getChannel().sendMessageEmbeds(new EmbedBuilder().setTitle(":warning: Ошибка!").setDescription(text).setColor(scarlet.argb8888()).build()).queue(message -> {
            event.getMessage().delete().queueAfter(10, SECONDS);
            message.delete().queueAfter(10, SECONDS);
        });
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild() || event.getAuthor().isBot()) return;

        if (event.getChannel() == mapsChannel) {
            parseMap(event);
        }

        if (event.getChannel() == schematicsChannel) {
            parseSchematic(event);
        }
    }
}