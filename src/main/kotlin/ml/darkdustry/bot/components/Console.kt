package ml.darkdustry.bot.components

import arc.util.ColorCodes
import arc.util.CommandHandler
import arc.util.Log.*
import kotlinx.coroutines.runBlocking
import ml.darkdustry.bot.Vars.exit
import ml.darkdustry.bot.Vars.jda
import net.dv8tion.jda.api.entities.Category
import net.dv8tion.jda.api.entities.TextChannel
import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Console {
    init {
        try {
            commandHandler = CommandHandler("")
            terminal = TerminalBuilder.builder().jna(true).system(true).build()
            lineReader = LineReaderBuilder.builder().terminal(terminal).build()

            logger = LogHandler { level, log ->
                val resultLog: String = if (level == LogLevel.err) {
                    log.replace(ColorCodes.reset, ColorCodes.lightRed + ColorCodes.bold)
                } else log

                val result =
                    "${ColorCodes.bold}${ColorCodes.lightBlack}[${dateTime.format(LocalDateTime.now())}] ${ColorCodes.reset} ${
                        format(
                            tags[level.ordinal]
                        )
                    } $resultLog"

                if (lineReader.isReading) {
                    lineReader.callWidget(LineReader.CLEAR)
                    lineReader.terminal.writer().println(result)

                    lineReader.callWidget(LineReader.REDRAW_LINE)
                    lineReader.callWidget(LineReader.REDISPLAY)
                } else lineReader.terminal.writer().println(result)
            }

            commandHandler.register("help", "Shows all available console commands.") {
                for (command in commandHandler.commandList) {
                    info("${command.text} : ${command.description} ${command.paramText}")
                }
            }

            commandHandler.register("say", "<id> <message...>", "Sends message") { args ->
                jda.getChannelById(TextChannel::class.java, args[0] as String)?.sendMessage(args[1])?.queue()
            }

            commandHandler.register("servers", "Shows all servers where the bot is located.") {
                for (guild in jda.guilds) {
                    info("Guild: ${guild.name} channels: ${guild.channels.size}")
                }
            }

            commandHandler.register("channels", "Shows all server channels.") {
                for (guild in jda.guilds) {
                    info("Guild: ${guild.name}")
                    for (channel in guild.channels) {
                        if (channel is Category) {
                            info("Category: ${channel.name} id: ${channel.id}")
                        } else info("Channel: ${channel.name} id: ${channel.id}")
                    }
                }
            }

            commandHandler.register("exit", "Disables the bot.") {
                exit(0)
            }
        } catch (e: Exception) {
            err(e)
            exit(0)
        }
    }

    fun consoleInput() = runBlocking {
        info("Console unblocked")
        while (true) {
            try {
                val line = lineReader.readLine("")

                if (line.isNotEmpty()) {
                    handleCommandString(line)
                }
            } catch (e: UserInterruptException) {
                exit(0)
            } catch (e: EndOfFileException) {
                exit(0)
            } catch (e: java.lang.Exception) {
                err(e.message)
            }
        }
    }

    companion object {
        private lateinit var terminal: Terminal
        private lateinit var lineReader: LineReader
        private lateinit var commandHandler: CommandHandler

        private var dateTime: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")
        private var tags = arrayOf("&lc&fb[D]&fr", "&lb&fb[I]&fr", "&ly&fb[W]&fr", "&lr&fb[E]", "")

        private fun handleCommandString(line: String) {
            val response = commandHandler.handleMessage(line)

            when (response.type) {
                CommandHandler.ResponseType.unknownCommand -> err("Invalid command. Type 'help' for help.")
                CommandHandler.ResponseType.fewArguments -> err("Too few command arguments. Usage: ${response.command.text} ${response.command.paramText}")
                CommandHandler.ResponseType.manyArguments -> err("Too many command arguments. Usage: ${response.command.text} ${response.command.paramText}")
                CommandHandler.ResponseType.valid -> return
                else -> return
            }
        }
    }
}