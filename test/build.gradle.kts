plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val kotestVersion: String by project

dependencies {
    implementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    implementation("org.testcontainers:postgresql:1.19.6")
    implementation("org.flywaydb:flyway-core:10.8.1")
    implementation("org.flywaydb:flyway-database-postgresql:10.8.1")
    implementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
}
