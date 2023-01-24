import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation

group = "ru.mindustry"
version = "1.0.0"

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")

    implementation("com.github.Anuken.Arc:arc-core:v140.4")
    implementation("com.github.Anuken.Mindustry:core:v140.4")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    sourceCompatibility = "16"
    targetCompatibility = "16"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ru.mindustry.bot.Main"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

val relocate = tasks.register<ConfigureShadowRelocation>("relocateShadowJar") {
    target = tasks.shadowJar.get()
    prefix = project.property("props.root-package").toString() + ".shadow"
}

tasks.shadowJar {
    archiveFileName.set("MindustryBot.jar")
    archiveClassifier.set("plugin")
    dependsOn(relocate)
    minimize()
}