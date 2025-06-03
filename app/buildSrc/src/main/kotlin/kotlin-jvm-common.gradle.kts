import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import  org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("org.jetbrains.kotlin.jvm")
//    id("org.jlleitschuh.gradle.ktlint")
}

val coroutinesCoreVersion: String by project
val kotestVersion: String by project

kotlin {
    jvmToolchain(21)
    compilerOptions.freeCompilerArgs = listOf("-Xcontext-parameters")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesCoreVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")

    val arrowVersion = "2.1.0"
    api("io.arrow-kt:arrow-core:$arrowVersion")
    api("io.arrow-kt:arrow-core-jvm:$arrowVersion")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
}

tasks {
    withType<Test> {
        testLogging {
            events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }
}

testing {
    suites {
        @Suppress("UnstableApiUsage", "Unused")
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter("5.10.0")
        }
    }
}
