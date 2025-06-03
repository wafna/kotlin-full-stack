plugins {
    id("kotlin-jvm-application")
    id("org.jetbrains.dokka")
}

val kotlinVersion: String by project
val hopliteVersion: String by project
val cliktVersion: String by project

dependencies {
    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    runtimeOnly("com.sksamuel.hoplite:hoplite-yaml:$hopliteVersion")
    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")
    implementation(project(":common:logger"))
    implementation(project(":db"))
    implementation(project(":api"))
}

tasks.run.get().workingDir = rootProject.projectDir

application {
    mainClass.set("wafna.fullstack.demo.TestData")
}
