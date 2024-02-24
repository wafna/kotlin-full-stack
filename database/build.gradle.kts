plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val kotlinVersion: String by project

dependencies {
    implementation("org.apache.commons:commons-text:1.11.0")
    implementation(project(":util"))
}
