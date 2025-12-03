import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

val coroutinesCoreVersion: String by project

plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

// DEV mode is intended primarily to suppress name mangling.
val buildMode = if (ext.has("BUILD_MODE"))
    when (ext.get("BUILD_MODE")) {
        "DEV" -> KotlinWebpackConfig.Mode.DEVELOPMENT
        else -> KotlinWebpackConfig.Mode.PRODUCTION
    } else KotlinWebpackConfig.Mode.PRODUCTION

kotlin {
    js(IR) {
        if (buildMode == KotlinWebpackConfig.Mode.DEVELOPMENT) {
            compilations.all {
                compileTaskProvider.configure {
                    compilerOptions.freeCompilerArgs.add("-Xir-minimized-member-names=false")
                }
            }
        }
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled = true
                }
                mode = buildMode
            }
            webpackTask {
                mode = buildMode
                debug = buildMode == KotlinWebpackConfig.Mode.DEVELOPMENT
                sourceMaps = buildMode == KotlinWebpackConfig.Mode.DEVELOPMENT
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    val currentOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
                    // Nice to have, doesn't work on Linux.
                    if (currentOperatingSystem.isMacOsX)
                        useFirefox()
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
                // This sets the version for the rest of the wrappers.
                implementation(project.dependencies.enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:2025.12.1"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}


rootProject.extensions.configure<NodeJsRootExtension> {
    versions.webpackCli.version = "5.1.4"
}
