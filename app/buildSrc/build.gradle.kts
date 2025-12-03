plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0-RC2")
    implementation("com.github.jmongard:git-semver-plugin:0.12.11")
    implementation("com.palantir.git-version:com.palantir.git-version.gradle.plugin:3.1.0")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.1.0")
}
