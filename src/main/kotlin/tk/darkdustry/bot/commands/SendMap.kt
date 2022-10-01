package tk.darkdustry.bot.commands

import arc.util.Log.err
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.utils.AttachedFile.fromData
import tk.darkdustry.bot.commands.core.SlashCommand
import tk.darkdustry.bot.commands.core.annotations.*
import tk.darkdustry.bot.components.MapUtils
import tk.darkdustry.bot.components.MapUtils.renderMap
import tk.darkdustry.bot.mapsChannel

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
            val stream = file.proxy.download().get()
            val map = MapUtils.generateMap(stream)

            if (map != null) {
                val embed = EmbedBuilder()
                    .setAuthor(map.author())
                    .setTitle(map.name())
                    .setDescription(map.description())
                    .setImage("attachment://map.png")
                    .setFooter("h: ${map.height}, w: ${map.width}")

                mapsChannel.sendMessageEmbeds(embed.build()).addFiles(
                    fromData(map.file.file()),
                    fromData(renderMap(map), "map.png")
                ).queue()
            }
        } catch (e: Exception) {
            err(e)
            event.hook.sendMessage("Something went wrong...")
        } finally {
            event.hook.sendMessage("Done. Check ${mapsChannel.asMention}").setEphemeral(true).queue()
        }
    }
}