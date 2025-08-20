plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val kotestVersion: String by project

dependencies {
    api("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    api("org.testcontainers:postgresql:1.21.3")
    api("org.flywaydb:flyway-core:11.11.1")
    api("org.flywaydb:flyway-database-postgresql:11.11.1")
    api("com.h2database:h2:2.3.232")
    api("com.zaxxer:HikariCP:7.0.2")
}
