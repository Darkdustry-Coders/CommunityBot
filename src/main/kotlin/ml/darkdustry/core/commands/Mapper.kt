package ml.darkdustry.core.commands

import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
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
            else -> throw IllegalArgumentException("Invalid input type: ${input.qualifiedName}")
        }
}