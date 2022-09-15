package tk.darkdustry.bot

import arc.files.Fi
import arc.util.Log.info
import arc.util.serialization.Json
import arc.util.serialization.JsonWriter.OutputType
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS
import net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT

lateinit var config: Config

lateinit var jda: JDA
lateinit var guild: Guild

lateinit var mapsChannel: TextChannel
lateinit var schematicsChannel: TextChannel

val json = Json()
val dataDirectory = Fi(".community")

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
    }

    jda = JDABuilder.createLight(config.token)
        .enableIntents(GUILD_MEMBERS, MESSAGE_CONTENT)
        .build()
        .awaitReady()

    guild = jda.getGuildById(config.guildId)!!

    mapsChannel = guild.getTextChannelById(config.mapsChannelId)!!
    schematicsChannel = guild.getTextChannelById(config.schematicsChannelId)!!
}