package tk.darkdustry.bot

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent.*
import tk.darkdustry.bot.commands.SendMap
import tk.darkdustry.bot.commands.core.CommandRegistryBuilder
import tk.darkdustry.bot.components.*


fun main() {
    ConfigUtils.init()
    Resources.init()

    jda = JDABuilder.createLight(config.token)
        .enableIntents(GUILD_MEMBERS, MESSAGE_CONTENT)
        .build()
        .awaitReady()

    guild = jda.getGuildById(config.guildId)!!

    mapsChannel = guild.getTextChannelById(config.mapsChannelId)!!
    schematicsChannel = guild.getTextChannelById(config.schematicsChannelId)!!

    CommandRegistryBuilder().addCommands(SendMap()).build().updateCommands(guild)
}