package ml.darkdustry.core.commands

import arc.util.Log.debug
import ml.darkdustry.core.commands.annotations.*
import ml.darkdustry.core.commands.events.*
import net.dv8tion.jda.api.Permission.*
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

class CommandRegistry(val commands: List<SlashCommand>, val mappers: List<Mapper<Any, Any?>>) {
    private var firstUpdate = true

    private val optionTypes = mapOf(
        String::class to OptionType.STRING,
        Long::class to OptionType.INTEGER,
        Boolean::class to OptionType.BOOLEAN,
        User::class to OptionType.USER,
        GuildChannel::class to OptionType.CHANNEL,
        Role::class to OptionType.ROLE,
        IMentionable::class to OptionType.MENTIONABLE,
        Double::class to OptionType.NUMBER,
    )

    internal val commandFunctions = mutableMapOf<String, Pair<KFunction<*>, List<String>>>()
    internal val buttonFunctions = mutableMapOf<String, KFunction<*>>()
    internal val modalFunctions = mutableMapOf<String, KFunction<*>>()

    init {
        for (command in commands) {
            val commandAnnotation = command::class.findAnnotation<Command>() ?: continue

            val name = commandAnnotation.name.ifEmpty {
                command::class.simpleName!!.removeSuffix("Command").lowercase()
            }

            val description = commandAnnotation.description
            val permissions = commandAnnotation.permissions

            val commandData = Commands.slash(name, description.ifEmpty { name })
            // val subcommandGroups = mutableListOf<SubcommandGroupData>()

            when (permissions) {
                PermissionType.PUBLIC -> commandData.defaultPermissions =
                    DefaultMemberPermissions.enabledFor(MESSAGE_SEND)
                PermissionType.MODERATOR -> commandData.defaultPermissions =
                    DefaultMemberPermissions.enabledFor(MODERATE_MEMBERS)
                PermissionType.ADMINISTRATOR -> commandData.defaultPermissions =
                    DefaultMemberPermissions.enabledFor(ADMINISTRATOR)
            }

            for (function in command::class.memberFunctions) {
                function.findAnnotation<Command>()?.let {
                    val options = parseOptions(function)
                    commandData.addOptions(options)
                    commandFunctions[name] = function to options.map { it.name }
                }

                function.findAnnotation<Button>()?.let { button ->
                    for (i in 1 until function.parameters.size) {
                        val parameter = function.parameters[i]
                        val type = parameter.type.classifier as KClass<*>
                        require(type == ButtonInteractionEvent::class || mappers.any { it.input == ButtonInteractionEvent::class && it.output == type }) {
                            "Mapper<ButtonInteractionEvent, ${type.simpleName}> not found"
                        }
                    }

                    buttonFunctions["$name.${button.id.ifEmpty { function.name.lowercase() }}"] = function
                }

                function.findAnnotation<Modal>()?.let { modal ->
                    modalFunctions["$name.${modal.name.ifEmpty { function.name.lowercase() }}"] = function
                }
            }

            command.commandRegistry = this
            command.commandData = commandData

            command.name = name
            command.decription = description
            command.permissions = permissions

            debug("Command: $name has been registered")
        }
    }

    fun command(name: String) = commands.firstOrNull { it.commandData.name == name }

    suspend inline fun <reified S : Any> transform(value: S, type: KClass<*>) =
        mappers.first { it.input == S::class && it.output == type }.transform(value)

    private fun parseOptions(function: KFunction<*>): List<OptionData> {
        val options = mutableListOf<OptionData>()
        var allowNonOptions = true
        for (i in 1 until function.parameters.size) {
            val parameter = function.parameters[i]
            val type = parameter.type.classifier as KClass<*>
            val option = parameter.findAnnotation<Option>()
            if (option == null) {
                require(allowNonOptions) {
                    "Parameter ${parameter.name} in function " + "${function.name} must be annotated as @Option!"
                }
                require(type == SlashCommandInteractionEvent::class || mappers.any { it.input == SlashCommandInteractionEvent::class && it.output == type }) {
                    "Mapper<SlashCommandInteractionEvent, ${type.simpleName}> not found"
                }
            } else {
                allowNonOptions = false
                val name = option.name.ifEmpty { parameter.name!!.lowercase() }
                if (type.java.isEnum) {
                    options.add(
                        OptionData(
                            OptionType.STRING,
                            name,
                            option.description.ifEmpty { name },
                            !parameter.type.isMarkedNullable
                        ).addChoices(type.java.enumConstants.map {
                            net.dv8tion.jda.api.interactions.commands.Command.Choice(
                                it.toString(),
                                (it as Enum<*>).name
                            )
                        })
                    )
                } else {
                    val optionType = optionTypes[type] ?: mappers.firstOrNull { it.output == type }?.type
                    ?: throw IllegalArgumentException("Mapper<?, ${type.simpleName}> not found")
                    options.add(
                        OptionData(
                            optionType, name, option.description.ifEmpty { name }, !parameter.type.isMarkedNullable
                        )
                    )
                }
            }
        }
        return options
    }

    fun updateCommands(guild: Guild) {
        if (firstUpdate) {
            firstUpdate = false
            guild.jda.addEventListener(
                SlashCommandInteractionListener(this), ButtonInteractionListener(this), ModalInteractionListener(this)
            )
        }

        guild.updateCommands().addCommands(commands.map { it.commandData }).queue { commands ->
            commands.forEach { command ->
                this.commands.first { it.commandData.name == command.name }
            }
        }
    }
}