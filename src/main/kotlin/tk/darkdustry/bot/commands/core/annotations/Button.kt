package tk.darkdustry.bot.commands.core.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Button(
    val id: String = "",
)