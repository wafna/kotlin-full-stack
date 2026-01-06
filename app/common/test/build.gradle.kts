plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val kotestVersion: String by project

dependencies {
    // ⚠ Ignoring CVEs: test code only.
    @Suppress("VulnerableLibrariesLocal", "RedundantSuppression")
    api("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    api("org.testcontainers:postgresql:1.21.4")
    api("org.flywaydb:flyway-core:11.20.0")
    api("org.flywaydb:flyway-database-postgresql:11.20.0")
    api("com.h2database:h2:2.4.240")
    api("com.zaxxer:HikariCP:7.0.2")
}
