plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

dependencies {
    implementation(project(":common:logger"))
    implementation(project(":db"))
    api(project(":domain"))
}
