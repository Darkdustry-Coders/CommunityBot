package tk.darkdustry.bot.commands.core

import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.KClass

interface Mapper<S, T> {

    suspend fun transform(value: S): T

    val input: KClass<*>
        get() = this::class.members.first { it.name == "transform" }.parameters[1].type.classifier as KClass<*>

    val output: KClass<*>
        get() = this::class.members.first { it.name == "transform" }.returnType.classifier as KClass<*>

    val type: OptionType
        get() = when (input) {
            String::class -> OptionType.STRING
            Long::class -> OptionType.INTEGER
            Boolean::class -> OptionType.BOOLEAN
            User::class -> OptionType.USER
            GuildChannel::class -> OptionType.CHANNEL
            Role::class -> OptionType.ROLE
            IMentionable::class -> OptionType.MENTIONABLE
            Double::class -> OptionType.NUMBER
            Attachment::class -> OptionType.ATTACHMENT
            else -> throw IllegalArgumentException("Invalid input type: ${input.qualifiedName}")
        }
}