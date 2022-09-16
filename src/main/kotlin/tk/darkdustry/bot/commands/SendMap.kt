package tk.darkdustry.bot.commands

import arc.files.Fi
import arc.graphics.Pixmap
import arc.graphics.PixmapIO
import arc.struct.StringMap
import arc.util.Log
import arc.util.io.CounterInputStream
import arc.util.io.Streams
import mindustry.core.Version
import mindustry.io.*
import mindustry.maps.Map
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.utils.FileUpload.fromData
import tk.darkdustry.bot.mapsChannel
import tk.darkdustry.bot.mapsDirectory
import tk.darkdustry.core.SlashCommand
import tk.darkdustry.core.annotations.*
import java.io.DataInputStream
import java.io.InputStream
import java.util.concurrent.CompletableFuture
import java.util.zip.InflaterInputStream
import kotlin.random.Random.Default.nextInt

@Suppress("unused")
@Command(PermissionType.Public, "sendmap", "Sends your map in a special channel.")
class SendMap : SlashCommand() {
    @Command
    fun send(
        event: SlashCommandInteractionEvent,
        @Option("file", "Your map file with an .msav extension") file: Attachment
    ) {
        event.deferReply().queue()

        if (file.fileExtension != "msav") {
            event.hook.sendMessage("The file extension must be `.msav`!").setEphemeral(true).queue()
            return
        }

        try {
            val futureStream: CompletableFuture<InputStream> = file.proxy.download()

            val counter = CounterInputStream(InflaterInputStream(futureStream.get()))
            val stream = DataInputStream(CounterInputStream(counter))

            SaveIO.readHeader(stream)

            val version = stream.readInt()
            val ver: SaveVersion = SaveIO.getSaveWriter(version)
            val tags = StringMap()

            val mapFile =
                Fi(
                    file.proxy.downloadToFile(
                        mapsDirectory.child(
                            "${
                                file.fileName.replace(
                                    ".msav",
                                    ""
                                )
                            }-${nextInt()}.msav"
                        ).file()
                    ).get()
                )

            ver.region("meta", stream, counter) { `in` -> tags.putAll(ver.readStringMap(`in`)) }
            val map = Map(
                mapFile,
                tags.getInt("width"),
                tags.getInt("height"),
                tags,
                true,
                version,
                Version.build
            )
            val preview = MapIO.generatePreview(map)

            mapsChannel.sendMessageEmbeds(
                EmbedBuilder()
                    .setTitle("**${map.name()}**")
                    .setDescription(map.description())
                    .addField("**Размеры:**", "x: ${map.width}, y: ${map.height}", false)
                    .setImage("attachment://${mapFile.name()}")
                    .build()
            ).addFiles(fromData(parseImage(preview), mapFile.name())).queue()
        } catch (e: Exception) {
            Log.err(e)
            event.hook.sendMessage("Something went wrong...")
        } finally {
            event.hook.sendMessage("Done. Check ${mapsChannel.asMention}").setEphemeral(true).queue()
        }
    }

    private fun parseImage(pixmap: Pixmap): ByteArray {
        val writer = PixmapIO.PngWriter(pixmap.width * pixmap.height)
        val stream = Streams.OptimizedByteArrayOutputStream(pixmap.width * pixmap.height)
        return try {
            writer.setFlipY(false)
            writer.write(stream, pixmap)
            stream.toByteArray()
        } catch (e: Exception) {
            Streams.emptyBytes
        } finally {
            writer.dispose()
        }
    }
}