group = "wafna"
version = "0.0.1-SNAPSHOT"

plugins {
    id("com.github.ben-manes.versions") version "0.52.0"
    id("org.jetbrains.dokka") version "2.0.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

repositories {
    mavenCentral()
}

detekt {
    config.setFrom(rootProject.files("buildConfig/detekt.yml"))
}
