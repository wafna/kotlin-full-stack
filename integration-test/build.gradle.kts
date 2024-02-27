plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val kotestVersion: String by project

dependencies {
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    testImplementation("org.testcontainers:postgresql:1.19.6")
    testImplementation("org.flywaydb:flyway-core:10.8.1")
}
