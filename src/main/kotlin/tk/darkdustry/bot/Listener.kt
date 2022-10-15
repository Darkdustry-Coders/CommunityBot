package tk.darkdustry.bot

import arc.files.Fi
import arc.util.Log.info
import mindustry.io.MapIO
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.utils.FileUpload
import tk.darkdustry.bot.components.ImageUtils

class Listener : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.isFromGuild || event.author.isBot) return

        when (event.channel) {
            mapsChannel -> parseMap(event)
            schematicsChannel -> {}
        }
    }

    private fun parseMap(event: MessageReceivedEvent) {
        info("Received a message.")

        if (event.message.attachments.size != 1) return

        val attachment = event.message.attachments[0]

        info("Got a map.")

        attachment.proxy.downloadToFile(cache.child(attachment.fileName).file()).thenAccept { file ->
            info("File downloaded.")

            val map = MapIO.createMap(Fi(file), true)

            info("Map created.")

            val pixmap = MapIO.generatePreview(map)

            info("Sending a map.")

            event.channel.sendMessageEmbeds(
                EmbedBuilder()
                    .setTitle(map.name())
                    .setDescription(map.description())
                    .setAuthor(map.author())
                    .setImage("attachment://image.png").build()
            ).addFiles(FileUpload.fromData(ImageUtils.parseImage(pixmap), "image.png"))
                .queue { message -> info("Map sent.") }
        }
    }
}