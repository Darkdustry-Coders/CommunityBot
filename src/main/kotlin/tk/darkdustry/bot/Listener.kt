package tk.darkdustry.bot

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.utils.FileUpload
import tk.darkdustry.bot.components.ContentHandler

class Listener : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.isFromGuild || event.author.isBot) return

        when (event.channel) {
            mapsChannel -> parseMap(event)
            schematicsChannel -> parseSchematic(event)
        }
    }

    private fun parseMap(event: MessageReceivedEvent) {
        if (event.message.attachments.size != 1) return

        val attachment = event.message.attachments[0]

        attachment.proxy.downloadToFile(cache.child(attachment.fileName).file()).thenAccept { file ->
            val map = ContentHandler.parseMap(file)
            val image = ContentHandler.parseMapImage(map)

            event.channel.sendMessageEmbeds(
                EmbedBuilder()
                    .setTitle(map.name())
                    .setDescription(map.description())
                    .setAuthor(map.author())
                    .setImage("attachment://image.png").build()
            ).addFiles(FileUpload.fromData(image, "image.png")).queue()
        }
    }

    private fun parseSchematic(event: MessageReceivedEvent) {
        if (event.message.attachments.size != 1) return

        val attachment = event.message.attachments[0]

        attachment.proxy.downloadToFile(cache.child(attachment.fileName).file()).thenAccept { file ->
            val schematic = ContentHandler.parseSchematic(file)
            val image = ContentHandler.parseSchematicImage(schematic)

            event.channel.sendMessageEmbeds(
                EmbedBuilder()
                    .setTitle(schematic.name())
                    .setDescription(schematic.description())
                    .setImage("attachment://image.png")
                    .build()
            ).addFiles(FileUpload.fromData(image, "image.png")).queue()
        }
    }
}