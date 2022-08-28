package ml.darkdustry.bot.commands.common

import ml.darkdustry.core.commands.SlashCommand
import ml.darkdustry.core.commands.annotations.Command
import ml.darkdustry.core.commands.annotations.PermissionType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@Suppress("unused")
@Command(PermissionType.PUBLIC, "ping", "Replies with Pong!")
class PingCommand : SlashCommand() {
    @Command
    fun ping(event: SlashCommandInteractionEvent) {
        event.reply("Pong!").setEphemeral(true).queue()
    }
}