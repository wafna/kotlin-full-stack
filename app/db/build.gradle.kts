plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.5")
    api("com.zaxxer:HikariCP:6.3.0")
    implementation(project(":common:logger"))
    api(project(":common:kdbc"))
    api(project(":domain"))
}
