package tk.darkdustry.bot.commands.core.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Command(
    val permissions: PermissionType = PermissionType.Public,
    val name: String = "",
    val description: String = ""
)