package ml.darkdustry.bot.commands.common

import ml.darkdustry.bot.Vars.suggestionChannel
import ml.darkdustry.bot.components.Utilities.fromByteArray
import ml.darkdustry.bot.components.Utilities.toByteArray
import ml.darkdustry.bot.components.data.Database
import ml.darkdustry.bot.components.data.Databases
import ml.darkdustry.core.commands.SlashCommand
import ml.darkdustry.core.commands.annotations.Button
import ml.darkdustry.core.commands.annotations.Command
import ml.darkdustry.core.commands.annotations.Option
import ml.darkdustry.core.commands.annotations.PermissionType
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button.*
import org.iq80.leveldb.impl.Iq80DBFactory.bytes
import java.awt.Color
import java.io.Serializable

@Suppress("unused")
@Command(PermissionType.PUBLIC, "suggest", "Sends a suggestion to a special channel.")
class SuggestionCommand : SlashCommand() {
    @Command
    fun suggest(
        event: SlashCommandInteractionEvent,
        @Option("server", "Server.") server: Servers,
        @Option("title", "Title of the suggestion.") title: String,
        @Option("suggestion", "Your suggestion.") suggestion: String
    ) {
        val embed = EmbedBuilder().setTitle("**Предложение для сервера ${server.name}:**")
            .addField("**$title**", suggestion, false)
            .addField("**Предложено пользователем:**", "${event.member?.asMention}", false)

        when (server) {
            Servers.Discord -> embed.setColor(Color(88, 101, 242))
            Servers.Mindustry -> embed.setColor(Color(254, 231, 92))
            Servers.Other -> embed.setColor(Color(35, 39, 42))
        }

        suggestionChannel.sendMessageEmbeds(embed.build()).setActionRow(
            success("suggest.yes", "Да!"),
            danger("suggest.no", "Нет")
        ).queue { message ->
            Database.write(Databases.Suggestions, bytes(message.id), Suggestion(title, suggestion).toByteArray())
        }

        event.reply("Предложение отправлено успешно.").setEphemeral(true).queue()
    }

    @Button
    fun yes(
        event: ButtonInteractionEvent
    ) {
        val suggestion = fromByteArray<Suggestion>(Database.get(Databases.Suggestions, bytes(event.message.id)))
        if (!check(event, suggestion)) {
            return event.reply("Вы не можете голосовать повторно.").setEphemeral(true).queue()
        }

        suggestion.votes++
        suggestion.members.add(event.member?.id)

        Database.rewrite(Databases.Suggestions, bytes(event.message.id), suggestion.toByteArray())
        update(suggestion, event)
        event.reply("Вы успешно проголосовали за.").setEphemeral(true).queue()
    }

    @Button
    fun no(
        event: ButtonInteractionEvent
    ) {
        val suggestion = fromByteArray<Suggestion>(Database.get(Databases.Suggestions, bytes(event.message.id)))
        if (!check(event, suggestion)) {
            return event.reply("Вы не можете голосовать повторно.").setEphemeral(true).queue()
        }

        suggestion.votes--
        suggestion.members.add(event.member?.id)

        Database.rewrite(Databases.Suggestions, bytes(event.message.id), suggestion.toByteArray())
        update(suggestion, event)
        event.reply("Вы успешно проголосовали против.").setEphemeral(true).queue()
    }

    private fun check(event: ButtonInteractionEvent, suggestion: Suggestion): Boolean {
        if (suggestion.members.contains(event.member?.id)) {
            return false
        }

        return true
    }

    private fun update(suggestion: Suggestion, event: ButtonInteractionEvent) {
        event.message.editMessage("**Общий счет голосов: ${suggestion.votes}**").queue()
    }
}

@Suppress("unused")
class Suggestion(title: String, description: String) : Serializable {
    internal var title: String
    internal var description: String

    internal var votes: Int
    internal var members: ArrayList<String?> = ArrayList()

    init {
        this.title = title
        this.description = description
        this.votes = 0
    }
}

@Suppress("unused")
enum class Servers {
    Discord, Mindustry, Other
}