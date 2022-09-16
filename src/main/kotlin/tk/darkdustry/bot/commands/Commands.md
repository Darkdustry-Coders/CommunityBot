## Commands
An example of a basic command:

```kotlin
@Command(PermissionType.Public, "echo", "Replies with your message.")
class Echo : SlashCommand() {
    @Command
    fun echo(
        event: SlashCommandInteractionEvent,
        @Option("message", "Your message.") message: String
    ) {
        event.reply("> $message").queue()
    }
}
```

Here you can add a button without having to create an event listener:

```kotlin
@Button
fun button(
    event: ButtonInteractionEvent
) {
    event.reply("Hello!").queue
}
```

If you need to use a selection in a command, you can make an enum:

```kotlin
@Command(PermissionType.Public, "choice", "Replies with your choice.")
class Echo : SlashCommand() {
    @Command
    fun echo(
        event: SlashCommandInteractionEvent,
        @Option("choice", "Your choice.") choice: Choice
    ) {
        event.reply("I like ${choice.name}'s too!").queue()
    }
}

enum class Choice {
    Cat, Dog
}
```