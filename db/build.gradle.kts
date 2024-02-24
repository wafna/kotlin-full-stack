plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.2")
    api("com.zaxxer:HikariCP:5.0.1")
    implementation(project(":util"))
    implementation(project(":database"))
    api(project(":domain"))
}
