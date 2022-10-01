package tk.darkdustry.bot

import arc.util.Log.info
import arc.util.serialization.JsonWriter.OutputType
import mindustry.Vars
import mindustry.core.*
import mindustry.ctype.Content
import mindustry.ctype.ContentType
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent.*
import tk.darkdustry.bot.commands.SendMap
import tk.darkdustry.bot.commands.core.CommandRegistryBuilder


fun main() {
    json.setOutputType(OutputType.json)
    json.setUsePrototypes(false)

    val file = dataDirectory.child("config.json")
    if (file.exists()) {
        config = json.fromJson(Config::class.java, file.reader())
        info("Config loaded. (@)", file.absolutePath())
    } else {
        file.writeString(json.toJson(Config().also { config = it }))
        info("Config file generated. (@)", file.absolutePath())
        return
    }

    Version.enabled = false
    Vars.content = ContentLoader()
    Vars.content.createBaseContent()
    // ContentType.all.forEach { type -> Vars.content.getBy<Content>(type).forEach { content -> content.init() } }

    jda = JDABuilder.createLight(config.token)
        .enableIntents(GUILD_MEMBERS, MESSAGE_CONTENT)
        .build()
        .awaitReady()

    guild = jda.getGuildById(config.guildId)!!

    mapsChannel = guild.getTextChannelById(config.mapsChannelId)!!
    schematicsChannel = guild.getTextChannelById(config.schematicsChannelId)!!

    CommandRegistryBuilder().addCommands(SendMap()).build().updateCommands(guild)
}