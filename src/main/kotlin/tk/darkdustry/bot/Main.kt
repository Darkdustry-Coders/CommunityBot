package tk.darkdustry.bot

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS
import net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT
import tk.darkdustry.bot.components.ConfigUtils
import tk.darkdustry.bot.components.ResourceUtils

fun main() {
    dataDirectory.mkdirs()
    cache.mkdirs()
    resources.mkdirs()
    sprites.mkdirs()

    ConfigUtils.init()
    ResourceUtils.init()

    jda = JDABuilder.createLight(config.token)
        .enableIntents(GUILD_MEMBERS, MESSAGE_CONTENT)
        .addEventListeners(Listener())
        .build()
        .awaitReady()

    guild = jda.getGuildById(config.guildId)!!

    mapsChannel = guild.getTextChannelById(config.mapsChannelId)!!
    schematicsChannel = guild.getTextChannelById(config.schematicsChannelId)!!
}