plugins {
    id("kotlin-web-common")
}

repositories {
    mavenCentral()
}

val letsPlotKotlinVersion: String by project

kotlin {
    sourceSets {
        @Suppress("unused")
        val jsMain by getting {
            dependencies {
                implementation(project(":browser:util"))
                implementation("org.jetbrains.lets-plot:lets-plot-kotlin-js:$letsPlotKotlinVersion")
            }
        }
    }
}
