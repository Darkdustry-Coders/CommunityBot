package ml.darkdustry.core.commands

import ml.darkdustry.core.commands.annotations.PermissionType
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.components.buttons.Button

@Suppress("unused")
abstract class SlashCommand {
    internal lateinit var name: String
    internal lateinit var decription: String
    internal lateinit var permissions: PermissionType

    internal lateinit var commandRegistry: CommandRegistry
    internal lateinit var commandData: CommandData

    protected fun Btn(id: String, label: String) = Btn("${commandData.name}.$id", label, null)
    protected fun Btn(id: String, emoji: Emoji) = Btn("${commandData.name}.$id", null, emoji)

    protected data class Btn(val id: String, val label: String?, val emoji: Emoji?) {
        fun primary() = label?.let { Button.primary(id, it) } ?: emoji?.let { Button.primary(id, it) }
        fun secondary() = label?.let { Button.secondary(id, it) } ?: emoji?.let { Button.secondary(id, it) }
        fun success() = label?.let { Button.success(id, it) } ?: emoji?.let { Button.success(id, it) }
        fun danger() = label?.let { Button.danger(id, it) } ?: emoji?.let { Button.danger(id, it) }
    }
}