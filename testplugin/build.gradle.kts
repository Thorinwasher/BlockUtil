plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("de.eldoria.plugin-yml.bukkit") version "0.7.1"
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "dev.thorinwasher"
version = "2.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":"))
    implementation("com.zaxxer:HikariCP:6.2.1")
    compileOnly("org.xerial:sqlite-jdbc:3.47.2.0")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    test {
        useJUnitPlatform()
    }

    runServer {
        minecraftVersion("1.21.5")
    }
}

bukkit {
    main = "dev.thorinwasher.blockutil.testplugin.TestPlugin"
    foliaSupported = false
    apiVersion = "1.21"
    authors = listOf("Thorinwasher")
    name = rootProject.name
}