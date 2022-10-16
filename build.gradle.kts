group = "tk.darkdustry"
version = "1.0.0"

plugins {
    java
}

repositories {
    mavenCentral()
    maven(url = "https://www.jitpack.io")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.21")

    implementation("com.github.Anuken.Arc:arc-core:v139")
    implementation("com.github.Anuken.Mindustry:core:v139")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "tk.darkdustry.bot.Main"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}