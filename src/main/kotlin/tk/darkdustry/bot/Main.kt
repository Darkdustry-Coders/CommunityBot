package tk.darkdustry.bot

import arc.util.Log.err
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent.*
import tk.darkdustry.bot.commands.SendMap
import tk.darkdustry.bot.commands.core.CommandRegistryBuilder
import tk.darkdustry.bot.components.*
import kotlin.system.exitProcess


fun main() {
    ConfigUtils.init()
    Resources.init()

    jda = JDABuilder.createLight(config.token)
        .enableIntents(GUILD_MEMBERS, MESSAGE_CONTENT)
        .build()
        .awaitReady()

    try {
        guild = jda.getGuildById(config.guildId)!!

        mapsChannel = guild.getTextChannelById(config.mapsChannelId)!!
        schematicsChannel = guild.getTextChannelById(config.schematicsChannelId)!!
    } catch (e: NullPointerException) {
        err("Configure config.json in the .community directory before using the bot!")
        exitProcess(1)
    }

    CommandRegistryBuilder().addCommands(SendMap()).build().updateCommands(guild)
}