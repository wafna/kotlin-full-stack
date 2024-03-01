plugins {
    id("kotlin-jvm-application")
    id("org.jetbrains.dokka")
}

val ktorVersion: String by project
val hopliteVersion = "2.7.5"

dependencies {
    implementation("io.arrow-kt:arrow-fx:0.12.1")
    implementation("io.arrow-kt:arrow-core-extensions:0.9.0")
    // CLI
    implementation("com.github.ajalt.clikt:clikt:4.2.1")
    // Config
    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    runtimeOnly("com.sksamuel.hoplite:hoplite-yaml:$hopliteVersion")
    // Web
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")

    api(project(":util"))
    api(project(":database"))
}

tasks.run.get().workingDir = rootProject.projectDir

application {
    mainClass.set("wafna.fullstack.server.AppKt")
}

// The production browser is bundled into the server distribution.
listOf(":server:distZip", ":server:distTar").forEach { dep ->
    tasks.getByPath(dep).dependsOn(":browser:build")
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
