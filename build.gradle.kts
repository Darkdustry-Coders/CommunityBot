import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "tk.darkdustry"
version = "1.0.0"

plugins {
    kotlin("jvm") version "1.7.20"
}

repositories {
    mavenCentral()
    maven(url = "https://www.jitpack.io")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.19")

    implementation("com.github.Anuken.Arc:arc-core:v138")
    implementation("com.github.Anuken.Mindustry:core:v138")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation(kotlin("reflect"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "tk.darkdustry.bot.MainKt"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}