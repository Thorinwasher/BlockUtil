pluginManagement {
  repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    gradlePluginPortal()
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

rootProject.name = "blockutil"
