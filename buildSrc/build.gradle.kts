plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
//    implementation("org.jlleitschuh.gradle:ktlint-gradle:11.3.1")
}
