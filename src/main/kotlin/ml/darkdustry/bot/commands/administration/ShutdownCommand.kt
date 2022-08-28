package ml.darkdustry.bot.commands.administration

import arc.util.Log.info
import ml.darkdustry.bot.Vars.exit
import ml.darkdustry.core.commands.SlashCommand
import ml.darkdustry.core.commands.annotations.Command
import ml.darkdustry.core.commands.annotations.PermissionType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@Suppress("unused")
@Command(PermissionType.ADMINISTRATOR, "shutdown", "Disables the bot.")
class ShutdownCommand : SlashCommand() {
    @Command
    fun shutdown(
        event: SlashCommandInteractionEvent
    ) {
        event.reply("Disabling...").setEphemeral(true).queue {
            info("Bot disabled by user: ${event.member?.nickname} id: ${event.member?.id}")
            exit(0)
        }
    }
}