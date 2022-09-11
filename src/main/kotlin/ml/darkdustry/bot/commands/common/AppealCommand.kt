package ml.darkdustry.bot.commands.common

import ml.darkdustry.bot.Vars.appealChannel
import ml.darkdustry.bot.Vars.appealRole
import ml.darkdustry.core.commands.SlashCommand
import ml.darkdustry.core.commands.annotations.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Modal.create
import net.dv8tion.jda.api.interactions.components.buttons.Button.danger
import net.dv8tion.jda.api.interactions.components.buttons.Button.success
import net.dv8tion.jda.api.interactions.components.text.TextInput.create
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import java.awt.Color

@Suppress("unused")
@Command(PermissionType.PUBLIC, "appeal", "Send appeal for review.")
class AppealCommand : SlashCommand() {
    @Command
    fun appeal(
        event: SlashCommandInteractionEvent
    ) {
        val nickname = create("nickname", "Nickname", TextInputStyle.SHORT)
            .setPlaceholder("Ваш игровой никнейм")
            .setRequiredRange(0, 150)
            .build()

        val reason = create("reason", "Reason", TextInputStyle.SHORT)
            .setPlaceholder("Причина вашей блокировки")
            .setRequiredRange(0, 150)
            .build()

        val date = create("date", "Unban date", TextInputStyle.SHORT)
            .setPlaceholder("Дата разблокировки")
            .setRequiredRange(0, 150)
            .build()

        val server = create("server", "Server", TextInputStyle.SHORT)
            .setPlaceholder("Сервер")
            .setRequiredRange(0, 150)
            .build()


        val appeal = create("appeal", "Апелляция")
            .addActionRows(ActionRow.of(nickname), ActionRow.of(reason), ActionRow.of(date), ActionRow.of(server))
            .build()

        event.replyModal(appeal).queue()
    }

    @Modal
    fun listener(
        event: ModalInteractionEvent,
        @Option nickname: String,
        @Option reason: String,
        @Option date: String,
        @Option server: String
    ) {
        appealChannel.sendMessageEmbeds(
            EmbedBuilder()
                .setTitle("**Апелляция на рассмотрение:**")
                .addField("**Никнейм:**", nickname, false)
                .addField("**Причина:**", reason, false)
                .addField("**Дата разблокировки:**", date, false)
                .addField("**Сервер:**", server, false)
                .setThumbnail(event.member?.avatarUrl)
                .setFooter("id: ${event.member?.id}")
                .build()
        ).setActionRows(ActionRow.of(success("appeal.accept", "Принять"), danger("appeal.reject", "Отклонить")))
            .queue { message ->
                message.createThreadChannel(event.member?.nickname).queue { channel ->
                    channel.sendMessage("${event.member?.asMention} ${appealRole.asMention}")
                }
            }

        event.reply("Администраторы рассмотрят апелляцию в ближайшее время.").setEphemeral(true).queue()
    }

    @Button
    fun accept(
        event: ButtonInteractionEvent
    ) {
        val message = event.message
        val buttons = ArrayList<net.dv8tion.jda.api.interactions.components.buttons.Button>()
        val done = EmbedBuilder()
            .setDescription("Принято администратором ${event.member?.asMention}")
            .setColor(Color.GREEN)

        message.embeds.forEach { embed ->
            done.setTitle(embed.title)
            done.setThumbnail(embed.thumbnail?.url)
            done.setFooter(embed.footer?.text)

            embed.fields.forEach { field ->
                done.addField(field)
            }
        }

        message.actionRows.forEach { actionRow ->
            actionRow.buttons.forEach { button ->
                buttons.add(button.asDisabled())
            }
        }

        message.editMessageEmbeds(done.build()).setActionRow(buttons).queue()
    }
}