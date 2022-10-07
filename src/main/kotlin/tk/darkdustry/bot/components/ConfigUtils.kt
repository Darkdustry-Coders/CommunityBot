package tk.darkdustry.bot.components

import arc.util.Log
import arc.util.serialization.JsonWriter
import tk.darkdustry.bot.*
import kotlin.system.exitProcess

object ConfigUtils {
    fun init() {
        json.setOutputType(JsonWriter.OutputType.json)
        json.setUsePrototypes(false)

        val file = dataDirectory.child("config.json")
        if (file.exists()) {
            config = json.fromJson(Config::class.java, file.reader())
            Log.info("Config loaded. (@)", file.absolutePath())
        } else {
            file.writeString(json.toJson(Config().also { config = it }))
            Log.info("Config file generated. (@)", file.absolutePath())
            exitProcess(0)
        }
    }

    @Suppress("unused")
    class Config {
        var token = "token"

        var guildId = 0L
        var mapsChannelId = 0L
        var schematicsChannelId = 0L
    }
}
