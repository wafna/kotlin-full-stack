group = "wafna"
version = "0.0.1-SNAPSHOT"

plugins {
    id("com.github.ben-manes.versions") version "0.46.0"
    id("org.jetbrains.dokka") version "1.8.10"
    id("io.gitlab.arturbosch.detekt") version "1.23.0-RC3"
}

repositories {
    mavenCentral()
}

detekt {
    config.setFrom(rootProject.files("buildConfig/detekt.yml"))
}
