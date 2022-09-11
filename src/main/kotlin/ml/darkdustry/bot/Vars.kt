package ml.darkdustry.bot

import arc.files.Fi
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import org.iq80.leveldb.Options
import kotlin.system.exitProcess

object Vars {
    // Discord
    lateinit var jda: JDA
    lateinit var guild: Guild

    // Channels
    lateinit var suggestionChannel: TextChannel
    lateinit var appealChannel: TextChannel

    // Roles
    lateinit var appealRole: Role

    // Bot
    fun exit(code: Int) {
        jda.shutdown()
        exitProcess(code)
    }

    // Files
    var mainDir = Fi(".communitybot")
    val options = Options().createIfMissing(true)!!
}