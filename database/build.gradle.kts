plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.2")
    api("com.zaxxer:HikariCP:5.1.0")
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation(project(":util"))
    implementation(project(":kdbc"))
    api(project(":domain"))
}

val integrationTest = java.sourceSets.create("integrationTest").apply {
    compileClasspath += sourceSets.test.get().compileClasspath
    runtimeClasspath += sourceSets.test.get().runtimeClasspath
    java.srcDirs("src/integration-test/kotlin")
}

tasks.create("integrationTest", Test::class) {
    group = "verification"
    description = "Runs integration tests."
    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath
    shouldRunAfter("test")
}
