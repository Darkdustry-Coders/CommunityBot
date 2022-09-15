package tk.darkdustry.bot

import arc.util.Log
import arc.util.serialization.JsonWriter.OutputType
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent.*
import tk.darkdustry.bot.Vars.*

class Main {

    fun main(args: Array<String>) {
        json.setOutputType(OutputType.json)
        json.setUsePrototypes(false)

        val file = dataDirectory.child("config.json")
        if (file.exists()) {
            config = json.fromJson(Config::class.java, file.reader())
            Log.info("Config loaded. (@)", file.absolutePath())
        } else {
            file.writeString(json.toJson(Config().also { config = it }))
            Log.info("Config file generated. (@)", file.absolutePath())
        }

        jda = JDABuilder.createLight(config.token)
            .enableIntents(GUILD_MEMBERS, MESSAGE_CONTENT)
            .build()
            .awaitReady()

        guild = jda.getGuildById(config.guildId)!!

        mapsChannel = guild.getTextChannelById(config.mapsChannelId)!!
        schematicsChannel = guild.getTextChannelById(config.schematicsChannelId)!!
    }
}