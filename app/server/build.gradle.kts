plugins {
    id("kotlin-jvm-application")
    id("org.jetbrains.dokka")
    kotlin("plugin.serialization") version "2.3.0"
}

val kotlinVersion: String by project
val ktorVersion: String by project
val moshiVersion: String by project
val hopliteVersion: String by project
val cliktVersion: String by project

dependencies {
    implementation("io.arrow-kt:arrow-fx:0.12.1")
    implementation("io.arrow-kt:arrow-core-extensions:0.9.0")
    // CLI
    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")
    // Config
    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    runtimeOnly("com.sksamuel.hoplite:hoplite-yaml:$hopliteVersion")
    // Web
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:${ktorVersion}")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    implementation("com.squareup.moshi:moshi-adapters:$moshiVersion")
    // Import
    implementation("com.opencsv:opencsv:5.12.0")

    implementation(project(":common:logger"))
    implementation(project(":db"))
    implementation(project(":api"))

    @Suppress("VulnerableLibrariesLocal", "RedundantSuppression")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")

    testImplementation(project(":common:test"))
}

tasks.run.get().workingDir = rootProject.projectDir

application {
    mainClass.set("wafna.fullstack.server.AppKt")
}

// The production browser is bundled into the server distribution.
listOf(":server:distZip", ":server:distTar").forEach { dep ->
    tasks.getByPath(dep).dependsOn(":browser:main:build")
}

distributions {
    main {
        distributionBaseName.set("kotlin-full-stack-demo")
        contents {
            from(project(":browser").file("build/distributions")) {
                into("browser")
            }
        }
    }
}
