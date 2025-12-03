plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val hikariVersion: String by project

dependencies {
    implementation("org.postgresql:postgresql:42.7.8")
    api("com.zaxxer:HikariCP:$hikariVersion")
    implementation(project(":common:logger"))
    api(project(":common:kdbc"))
    api(project(":domain"))
}
