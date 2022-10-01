package tk.darkdustry.bot.commands

import arc.util.Log.err
import arc.util.io.CounterInputStream
import mindustry.io.*
import mindustry.world.WorldContext
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import tk.darkdustry.bot.mapsChannel
import tk.darkdustry.bot.commands.core.SlashCommand
import tk.darkdustry.bot.commands.core.annotations.*
import java.io.*
import java.util.concurrent.CompletableFuture
import java.util.zip.InflaterInputStream

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


        } catch (e: Exception) {
            err(e)
            event.hook.sendMessage("Something went wrong...")
        } finally {
            event.hook.sendMessage("Done. Check ${mapsChannel.asMention}").setEphemeral(true).queue()
        }
    }
}