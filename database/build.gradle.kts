plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

dependencies {
    implementation("org.apache.commons:commons-text:1.11.0")
    implementation(project(":util"))
}
