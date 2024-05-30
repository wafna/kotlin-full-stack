plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

kotlin {
    jvmToolchain(18)
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
//    implementation("org.jlleitschuh.gradle:ktlint-gradle:11.3.1")
}
