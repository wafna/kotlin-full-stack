import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension

val coroutinesCoreVersion: String by project

plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
    compilerOptions.freeCompilerArgs = listOf("-Xcontext-parameters")
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    // useFirefox()
                }
            }
        }
        binaries.executable()
    }
    sourceSets {
        @Suppress("unused")
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesCoreVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
                implementation(project.dependencies.enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:2025.8.16"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test")) // This makes test annotations and functionality available in JS
        }
    }
}

rootProject.extensions.configure<NodeJsRootExtension> {
    versions.webpackCli.version = "5.1.4"
}
