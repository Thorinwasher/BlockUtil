plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.0.0-rc1"
}

group = "dev.thorinwasher"
version = "2.0.0"
description = "A library that assists in block management."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")
    compileOnly("org.xerial:sqlite-jdbc:3.45.1.0")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
}
