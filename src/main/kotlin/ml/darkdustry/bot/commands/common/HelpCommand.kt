package ml.darkdustry.bot.commands.common

import ml.darkdustry.bot.Vars.commands
import ml.darkdustry.core.commands.SlashCommand
import ml.darkdustry.core.commands.annotations.Command
import ml.darkdustry.core.commands.annotations.PermissionType
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@Suppress("unused")
@Command(PermissionType.PUBLIC, "help", "Shows all available slash commands")
class HelpCommand : SlashCommand() {
    @Command
    fun help(
        event: SlashCommandInteractionEvent
    ) {
        val embed = EmbedBuilder()
        embed.setTitle("**__All slash commands:__**")

        val builder = StringBuilder()

        // TODO: remove this shit

        if (event.member?.hasPermission(Permission.ADMINISTRATOR)!!) {
            builder.append("**__Administration Commands:__**\n")
            for (command in commands) {
                if (command.permissions == PermissionType.ADMINISTRATOR) {
                    builder.append("/`${command.name}` ${command.decription}\n")
                }
            }
            builder.append("\n\n")
        }

        if (event.member?.hasPermission(Permission.BAN_MEMBERS)!!) {
            builder.append("**__Moderation Commands:__**\n")
            for (command in commands) {
                if (command.permissions == PermissionType.MODERATOR) {
                    builder.append("/`${command.name}` ${command.decription}\n")
                }
            }
            builder.append("\n\n")
        }

        if (event.member?.hasPermission(Permission.MESSAGE_SEND)!!) {
            builder.append("**__Public Commands:__**\n")
            for (command in commands) {
                if (command.permissions == PermissionType.PUBLIC) {
                    builder.append("/`${command.name}` ${command.decription}\n")
                }
            }
        }

        embed.setDescription(builder.toString())
        event.replyEmbeds(embed.build()).queue()
    }
}