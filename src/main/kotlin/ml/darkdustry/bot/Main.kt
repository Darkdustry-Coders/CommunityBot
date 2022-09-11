package ml.darkdustry.bot

import arc.util.Log
import ml.darkdustry.bot.commands.administration.GetCommand
import ml.darkdustry.bot.commands.administration.ShutdownCommand
import ml.darkdustry.bot.commands.common.*
import ml.darkdustry.bot.components.Console
import ml.darkdustry.core.commands.CommandRegistryBuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: {
        Log.err("Cannot find token")
        Vars.exit(2)
    }

    try {
        val duration = measureTimeMillis {
            Console.init()

            if (!Vars.mainDir.exists()) {
                Vars.mainDir.mkdirs()
                Log.info("Main bot directory generated: ${Vars.mainDir.absolutePath()}")
            } else Log.info("Main bot directory: ${Vars.mainDir.absolutePath()}")

            Vars.jda = JDABuilder.createDefault(token as String)
                .setActivity(Activity.listening("Spotify"))
                .setStatus(OnlineStatus.IDLE)
                .build()
                .awaitReady()

            Vars.guild = Vars.jda.getGuildById("810758118442663936")!!

            Vars.suggestionChannel = Vars.guild.getTextChannelById("878923967862300712")!!
            Vars.appealChannel = Vars.guild.getTextChannelById("878923967862300712")!!

            Vars.appealRole = Vars.guild.getRoleById("942813823260303401")!!

            Log.info("Setup commands...")
            val commandsDuration = measureTimeMillis {
                val commandRegistryBuilder =
                    CommandRegistryBuilder().addCommands(
                        // Public
                        PingCommand(),
                        EchoCommand(),
                        AppealCommand(),

                        // Moderation

                        // Administration
                        ShutdownCommand(),
                        GetCommand()
                    )

                commandRegistryBuilder.build().updateCommands(Vars.guild)
            }

            Log.info("Setup of commands finished in $commandsDuration ms")
        }

        Log.info("Setup finished in $duration ms")
        Console.input()
    } catch (e: Exception) {
        Log.err(e)
        Vars.exit(2)
    }
}