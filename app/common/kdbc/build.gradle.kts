plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val kotlinVersion: String by project
val hikariVersion: String by project

dependencies {
    implementation(project(":common:logger"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    testImplementation(project(":common:test"))
    testImplementation("com.zaxxer:HikariCP:$hikariVersion")
    testImplementation("org.postgresql:postgresql:42.7.7")
    testImplementation(project(":common:test"))
}
