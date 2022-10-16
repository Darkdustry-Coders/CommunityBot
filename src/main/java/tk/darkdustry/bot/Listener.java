package tk.darkdustry.bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import tk.darkdustry.bot.components.ContentHandler;

import static arc.util.Log.err;
import static tk.darkdustry.bot.Vars.*;

public class Listener extends ListenerAdapter {

    private static void parseMap(MessageReceivedEvent event) {
        if (event.getMessage().getAttachments().size() != 1) return;

        var attachment = event.getMessage().getAttachments().get(0);

        attachment.getProxy().downloadToFile(cache.child(attachment.getFileName()).file()).thenAccept(file -> {
            try {
                var map = ContentHandler.parseMap(file);
                var image = ContentHandler.parseMapImage(map);

                event.getChannel().sendMessageEmbeds(
                        new EmbedBuilder().setTitle(map.name()).setDescription(map.description()).setAuthor(map.author()).setImage("attachment://image.png").build()
                ).addFiles(FileUpload.fromData(image, "image.png")).queue();

            } catch (Exception e) { // TODO (люцин) отвечать ошибкой в дискорде
                file.delete();
                err(e);
            }
        });
    }

    private static void parseSchematic(MessageReceivedEvent event) {
        if (event.getMessage().getAttachments().size() != 1) return;

        var attachment = event.getMessage().getAttachments().get(0);

        attachment.getProxy().downloadToFile(cache.child(attachment.getFileName()).file()).thenAccept(file -> {
            try {
                var schematic = ContentHandler.parseSchematic(file);
                var image = ContentHandler.parseSchematicImage(schematic);

                event.getChannel().sendMessageEmbeds(
                        new EmbedBuilder().setTitle(schematic.name()).setDescription(schematic.description()).setImage("attachment://image.png").build()
                ).addFiles(FileUpload.fromData(image, "image.png")).queue();

            } catch (Exception e) { // TODO (люцин) отвечать ошибкой в дискорде
                file.delete();
                err(e);
            }
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