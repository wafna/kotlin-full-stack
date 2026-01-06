plugins {
    id("kotlin-web-common")
    kotlin("plugin.serialization") version "2.3.0"
}

kotlin {
    sourceSets {
        @Suppress("unused")
        val jsMain by getting {
            dependencies {
                implementation(project(":browser:api"))
                implementation(project(":browser:util"))
                implementation(project(":browser:gridley"))
                implementation(project(":browser:plots"))
            }
        }
    }
}

rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0"
}
