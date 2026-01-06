import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.dokka")
}

val coroutinesCoreVersion: String by project
val kotestVersion: String by project

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesCoreVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1-0.6.x-compat")

    val arrowVersion = "2.2.1.1"
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-core-jvm:$arrowVersion")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }
    withType<Test> {
        testLogging {
            events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }
    withType<Jar> {
        archiveAppendix = archiveBaseName.get()
        archiveBaseName = "bucknell-red"
    }
}

testing {
    suites {
        @Suppress("UnstableApiUsage", "Unused")
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter("6.1.0-M1")
        }
    }
}

dokka {
    pluginsConfiguration.html {
        footerMessage.set("(c) 2024-2025 Bucknell University")
    }
}