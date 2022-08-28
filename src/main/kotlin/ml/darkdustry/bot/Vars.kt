package ml.darkdustry.bot

import arc.files.Fi
import ml.darkdustry.bot.components.Console
import ml.darkdustry.core.commands.SlashCommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import org.iq80.leveldb.Options
import kotlin.system.exitProcess

object Vars {
    // Discord
    lateinit var jda: JDA
    lateinit var guild: Guild

    // Channels
    lateinit var suggestionChannel: TextChannel

    // Bot
    lateinit var console: Console
    var commands: MutableList<SlashCommand> = mutableListOf()

    fun exit(code: Int) {
        jda.shutdown()
        exitProcess(code)
    }

    // Files
    var mainDir = Fi(".communitybot")

    val options = Options().createIfMissing(true)!!

    //val appeals: DB = Iq80DBFactory.factory.open(File("${mainDir.absolutePath()}/appeals"), options)
    //val warns: DB = Iq80DBFactory.factory.open(File("${mainDir.absolutePath()}/warns"), options)
    //val mutes: DB = Iq80DBFactory.factory.open(File("${mainDir.absolutePath()}/mutes"), options)
}