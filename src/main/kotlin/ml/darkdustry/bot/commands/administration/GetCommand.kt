package ml.darkdustry.bot.commands.administration

import ml.darkdustry.bot.Vars.mainDir
import ml.darkdustry.bot.components.data.Databases
import ml.darkdustry.core.commands.SlashCommand
import ml.darkdustry.core.commands.annotations.Command
import ml.darkdustry.core.commands.annotations.Option
import ml.darkdustry.core.commands.annotations.PermissionType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.iq80.leveldb.DB
import org.iq80.leveldb.Options
import org.iq80.leveldb.impl.Iq80DBFactory.*
import java.io.File

@Suppress("unused")
@Command(PermissionType.ADMINISTRATOR, "get", "test")
class GetCommand : SlashCommand() {
    @Command
    fun get(
        event: SlashCommandInteractionEvent,
        @Option place: Databases,
        @Option value: String
    ) {
        val options = Options().createIfMissing(false)
        val database: DB = factory.open(File("${mainDir.absolutePath()}/${place.name.lowercase()}"), options)

        database.use { db ->
            val result = asString(db.get(bytes(value))) ?: "Cannot find $value"
            event.reply("Value: $result").queue()
        }
    }
}