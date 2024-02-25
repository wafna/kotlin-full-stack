plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.2")
    api("com.zaxxer:HikariCP:5.0.1")
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation(project(":util"))
    implementation(project(":kdbc"))
    api(project(":domain"))
}
