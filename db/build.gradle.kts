plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val kotlinVersion: String by project

dependencies {
    api("com.zaxxer:HikariCP:5.0.1")
    api("org.postgresql:postgresql:42.7.2")
    implementation(project(":util"))
    api(project(":domain"))
}
