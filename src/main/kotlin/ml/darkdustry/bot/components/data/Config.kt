package ml.darkdustry.bot.components.data

import arc.util.Log
import ml.darkdustry.bot.Vars

class Config {
    init {
        if (!Vars.mainDir.exists()) {
            Vars.mainDir.mkdirs()
            Log.info("Main bot directory generated: ${Vars.mainDir.absolutePath()}")
        } else Log.info("Main bot directory: ${Vars.mainDir.absolutePath()}")
    }
}