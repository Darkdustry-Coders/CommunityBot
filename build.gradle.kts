import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "ml.darkdustry"
version = "1.0.0"

plugins {
    kotlin("jvm") version "1.7.10"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    mavenCentral()
    maven(url = "https://www.jitpack.io")
}

dependencies {
    val mindustryVersion = "v137"
    val jline = "3.21.0"

    implementation("com.github.halibobor.leveldb-java:leveldb:1.23.1")
    implementation("net.dv8tion:JDA:5.0.0-alpha.17")

    implementation("org.jline:jline-reader:$jline")
    implementation("org.jline:jline-terminal-jna:$jline")

    implementation("com.github.Anuken.Arc:arc-core:$mindustryVersion")
    implementation("com.github.Anuken.Mindustry:core:$mindustryVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation(kotlin("reflect"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ml.darkdustry.bot.MainKt"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}