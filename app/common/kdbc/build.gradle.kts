plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val kotlinVersion: String by project

dependencies {
    implementation(project(":common:logger"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    testImplementation(project(":common:test"))
    testImplementation("com.zaxxer:HikariCP:6.3.0")
    testImplementation("org.postgresql:postgresql:42.7.5")
    testImplementation(project(":common:test"))
}
