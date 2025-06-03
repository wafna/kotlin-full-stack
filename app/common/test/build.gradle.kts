plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val kotestVersion: String by project

dependencies {
    api("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    api("org.testcontainers:postgresql:1.21.0")
    api("org.flywaydb:flyway-core:11.8.0")
    api("org.flywaydb:flyway-database-postgresql:11.8.0")
    api("com.h2database:h2:2.3.232")
    api("com.zaxxer:HikariCP:6.3.0")
}
