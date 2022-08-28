package ml.darkdustry.core.commands.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Button(
    val id: String = "",
)