package ml.darkdustry.core.commands.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Modal(
    val name: String = "",
)