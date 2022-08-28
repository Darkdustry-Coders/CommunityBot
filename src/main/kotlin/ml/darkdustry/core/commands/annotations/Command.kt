package ml.darkdustry.core.commands.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Command(
    val permissions: PermissionType = PermissionType.PUBLIC,
    val name: String = "",
    val description: String = ""
)