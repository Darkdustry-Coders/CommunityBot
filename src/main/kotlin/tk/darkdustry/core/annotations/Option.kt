package tk.darkdustry.core.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Option(
    val name: String = "",
    val description: String = ""
)
