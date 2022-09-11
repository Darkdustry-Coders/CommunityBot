package ml.darkdustry.core.commands.events

import arc.util.Log.debug
import kotlinx.coroutines.runBlocking
import ml.darkdustry.core.commands.CommandRegistry
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.hooks.EventListener
import kotlin.reflect.KClass
import kotlin.reflect.full.callSuspend
import kotlin.system.measureTimeMillis

class ModalInteractionListener(private val commandRegistry: CommandRegistry) : EventListener {
    override fun onEvent(event: GenericEvent) = runBlocking {
        if (event !is ModalInteractionEvent) return@runBlocking

        val commandName = event.modalId.split('.')[0]
        val command = commandRegistry.command(commandName)
            ?: throw IllegalArgumentException("Unknown modal ${event.modalId}")
        val function = commandRegistry.modalFunctions[event.modalId]
            ?: throw IllegalArgumentException("Unknown modal ${event.modalId}")

        val duration = measureTimeMillis {
            val args = Array(function.parameters.size - 1) { i ->
                val type = function.parameters[i + 1].type.classifier as KClass<*>
                if (type == ModalInteractionEvent::class) {
                    event
                } else {
                    try {
                        commandRegistry.transform(event, type)
                    } catch (e: IllegalArgumentException) {
                        return@Array event.replyEmbeds(
                            EmbedBuilder().setDescription(e.message ?: "Something went wrong \uD83D\uDE15").build()
                        ).setEphemeral(true).queue()
                    }
                }
            }
            function.callSuspend(command, *args)
        }

        debug("Modal ${event.modalId} handled. Duration = $duration userId=${event.user.id}ms. Guild id = ${event.guild?.id ?: -1}.")
    }
}