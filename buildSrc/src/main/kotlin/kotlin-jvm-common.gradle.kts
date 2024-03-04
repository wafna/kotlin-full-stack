import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import  org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("org.jetbrains.kotlin.jvm")
//    id("org.jlleitschuh.gradle.ktlint")
}

val coroutinesCoreVersion: String by project
val arrowVersion: String by project
val kotestVersion: String by project

kotlin {
    jvmToolchain(18)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesCoreVersion")
    implementation("io.arrow-kt:arrow-core-jvm:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-stm:$arrowVersion")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
    }
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
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.10.0")
        }
    }
}
