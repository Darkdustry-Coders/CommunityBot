package ml.darkdustry.bot

import arc.util.Log.err
import arc.util.Log.info
import kotlinx.coroutines.runBlocking
import ml.darkdustry.bot.Vars.commands
import ml.darkdustry.bot.Vars.console
import ml.darkdustry.bot.Vars.exit
import ml.darkdustry.bot.Vars.guild
import ml.darkdustry.bot.Vars.jda
import ml.darkdustry.bot.commands.administration.GetCommand
import ml.darkdustry.bot.commands.administration.ShutdownCommand
import ml.darkdustry.bot.commands.common.EchoCommand
import ml.darkdustry.bot.commands.common.HelpCommand
import ml.darkdustry.bot.commands.common.PingCommand
import ml.darkdustry.bot.commands.common.SuggestionCommand
import ml.darkdustry.bot.components.Console
import ml.darkdustry.bot.components.data.Config
import ml.darkdustry.core.commands.CommandRegistryBuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus.IDLE
import net.dv8tion.jda.api.entities.Activity
import kotlin.system.measureTimeMillis

class Main(args: Array<String>) {
    init {
        val token = args.firstOrNull() ?: {
            err("Cannot find token")
            exit(0)
        }

        try {
            val duration = measureTimeMillis {
                console = Console()
                Config()

                jda = JDABuilder.createDefault(token as String)
                    .setActivity(Activity.listening("Spotify"))
                    .setStatus(IDLE)
                    .build()
                    .awaitReady()

                // TODO: properties.json

                guild = jda.getGuildById("993884930281050192")!!

                // suggestionChannel = guild.getTextChannelById("878918624415465483")!!
            }

            info("Setup finished in $duration ms")
            setupCommands()
            console.consoleInput()
        } catch (e: Exception) {
            err(e)
            exit(0)
        }
    }

    companion object {
        private fun setupCommands() = runBlocking {
            info("Setup commands...")
            val commandsDuration = measureTimeMillis {
                val commandRegistryBuilder =
                    CommandRegistryBuilder().addCommands(
                        // Public
                        HelpCommand(),
                        PingCommand(),
                        EchoCommand(),
                        SuggestionCommand(),

                        // Moderation

                        // Administration
                        ShutdownCommand(),
                        GetCommand()
                    )
                commands = commandRegistryBuilder.getCommands()

                commandRegistryBuilder.build().updateCommands(guild)
            }

            info("Setup of commands finished in $commandsDuration ms")
        }
    }
}