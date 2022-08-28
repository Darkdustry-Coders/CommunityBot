package ml.darkdustry.bot.events

import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class MemberJoin : ListenerAdapter() {
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent): Unit = runBlocking {
        event.user.openPrivateChannel().map { channel -> channel.sendMessage("cum").queue() }
    }
}