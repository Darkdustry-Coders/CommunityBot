package tk.darkdustry.bot

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Listener : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.isFromGuild || event.author.isBot) return;

        when (event.channel) {
            mapsChannel -> {}
            schematicsChannel -> {}
        }
    }
}