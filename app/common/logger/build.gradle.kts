plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val kotlinVersion: String by project

dependencies {
    implementation("org.slf4j:slf4j-api:2.1.0-alpha1")
    @Suppress("VulnerableLibrariesLocal", "RedundantSuppression")
    implementation("ch.qos.logback:logback-classic:1.5.21")
    implementation("org.apache.logging.log4j:log4j-core:3.0.0-beta3")
}
