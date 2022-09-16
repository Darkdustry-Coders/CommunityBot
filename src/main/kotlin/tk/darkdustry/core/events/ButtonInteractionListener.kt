package tk.darkdustry.core.events

import arc.util.Log.debug
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.EventListener
import tk.darkdustry.core.CommandRegistry
import kotlin.reflect.KClass
import kotlin.reflect.full.callSuspend
import kotlin.system.measureTimeMillis

class ButtonInteractionListener(private val commandRegistry: CommandRegistry) : EventListener {
    override fun onEvent(event: GenericEvent) = runBlocking {
        if (event !is ButtonInteractionEvent) return@runBlocking

        val commandName = event.componentId.split('.')[0]
        val command = commandRegistry.command(commandName)
            ?: throw IllegalArgumentException("Unknown button ${event.componentId}")
        val function = commandRegistry.buttonFunctions[event.componentId]
            ?: throw IllegalArgumentException("Unknown button ${event.componentId}")

        val duration = measureTimeMillis {
            val args = Array(function.parameters.size - 1) { i ->
                val type = function.parameters[i + 1].type.classifier as KClass<*>
                if (type == ButtonInteractionEvent::class) {
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

        debug("Button ${event.componentId} handled. Duration = $duration userId=${event.user.id}ms. Guild id = ${event.guild?.id ?: -1}.")
    }
}