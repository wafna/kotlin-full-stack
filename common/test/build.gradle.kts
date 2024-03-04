plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val kotestVersion: String by project

dependencies {
    implementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    implementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    implementation("org.testcontainers:postgresql:1.19.6")
    api("org.flywaydb:flyway-core:10.8.1")
    api("org.flywaydb:flyway-database-postgresql:10.8.1")
    api("com.h2database:h2:2.2.224")
    api("com.zaxxer:HikariCP:5.1.0")
}
