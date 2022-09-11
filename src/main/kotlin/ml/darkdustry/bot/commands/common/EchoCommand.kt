package ml.darkdustry.bot.commands.common

import ml.darkdustry.core.commands.SlashCommand
import ml.darkdustry.core.commands.annotations.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@Suppress("unused")
@Command(PermissionType.PUBLIC, "echo", "Replies with your message.")
class EchoCommand : SlashCommand() {
    @Command
    fun echo(
        event: SlashCommandInteractionEvent,
        @Option("message", "The message that the bot will reply to you.") message: String,
    ) {
        event.reply(message).setEphemeral(true).queue()
    }
}