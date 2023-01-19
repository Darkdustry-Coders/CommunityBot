group = "ru.mindustry"
version = "1.0.0"

plugins {
    java
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
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ru.mindustry.bot.Main"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}