package ml.darkdustry.core.commands.events

import arc.util.Log.debug
import kotlinx.coroutines.runBlocking
import ml.darkdustry.core.commands.CommandRegistry
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.KClass
import kotlin.reflect.full.callSuspend
import kotlin.system.measureTimeMillis

class SlashCommandInteractionListener(private val commandRegistry: CommandRegistry) : EventListener {
    override fun onEvent(event: GenericEvent) = runBlocking {
        if (event !is SlashCommandInteractionEvent) return@runBlocking

        val id = when {
            event.subcommandGroup != null -> "${event.name}.${event.subcommandGroup}.${event.subcommandName}"
            event.subcommandName != null -> "${event.name}.${event.subcommandName}"
            else -> event.name
        }

        val command = commandRegistry.command(event.name)
            ?: throw IllegalArgumentException("Unknown command: id='$id'")
        val (function, options) = commandRegistry.commandFunctions[id]

            ?: throw IllegalArgumentException("Unknown command: id='$id'")

        val duration = measureTimeMillis {
            val parameterOffset = function.parameters.size - options.size
            val eventArgs = Array(parameterOffset - 1) { i ->
                val type = function.parameters[i + 1].type.classifier as KClass<*>
                if (type == SlashCommandInteractionEvent::class) {
                    event
                } else {
                    commandRegistry.transform(event, type)
                }
            }
            val optionArgs = Array(options.size) { index ->
                val type = function.parameters[parameterOffset + index].type.classifier as KClass<*>
                val name = options[index]
                when (type) {
                    String::class -> event.getOption(name)?.asString
                    Long::class -> event.getOption(name)?.asLong
                    Boolean::class -> event.getOption(name)?.asBoolean
                    User::class -> event.getOption(name)?.asUser
                    GuildChannel::class -> event.getOption(name)?.asChannel
                    Role::class -> event.getOption(name)?.asRole
                    IMentionable::class -> event.getOption(name)?.asMentionable
                    Double::class -> event.getOption(name)?.asDouble
                    else -> event.getOption(name)?.let { option ->
                        if (type.java.isEnum) type.java.enumConstants.first { (it as Enum<*>).name == option.asString } else try {
                            when (option.type) {
                                OptionType.STRING -> commandRegistry.transform(option.asString, type)
                                OptionType.INTEGER -> commandRegistry.transform(option.asLong, type)
                                OptionType.BOOLEAN -> commandRegistry.transform(option.asBoolean, type)
                                OptionType.USER -> commandRegistry.transform(option.asUser, type)
                                OptionType.CHANNEL -> commandRegistry.transform(option.asChannel, type)
                                OptionType.ROLE -> commandRegistry.transform(option.asRole, type)
                                OptionType.MENTIONABLE -> commandRegistry.transform(option.asMentionable, type)
                                OptionType.NUMBER -> commandRegistry.transform(option.asDouble, type)
                                else -> throw IllegalStateException("Invalid option type: ${option.type}")
                            }
                        } catch (e: IllegalArgumentException) {
                            return@let event.replyEmbeds(
                                EmbedBuilder().setDescription(e.message ?: "Something went wrong \uD83D\uDE15").build()
                            ).setEphemeral(true).queue()
                        }
                    }
                }
            }
            function.callSuspend(command, *eventArgs, *optionArgs)
        }

        debug("Command $id handled. Duration = $duration userId=${event.user.id}ms. Guild id = ${event.guild?.id ?: -1}.")
    }

}