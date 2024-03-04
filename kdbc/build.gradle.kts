plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val kotestVersion: String by project

dependencies {
    implementation(project(":util"))
    testImplementation("com.google.guava:guava:33.0.0-jre")
    testImplementation("com.h2database:h2:2.2.224")
    testImplementation("com.zaxxer:HikariCP:5.1.0")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}

/*
val integrationTest = java.sourceSets.create("integrationTest").apply {
    compileClasspath += sourceSets.test.get().compileClasspath
    runtimeClasspath += sourceSets.test.get().runtimeClasspath
    java.srcDirs("src/integration-test/kotlin")
    resources.srcDirs("src/integration-test/resources")
    dependencies {
        implementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
        implementation("io.kotest:kotest-assertions-core:$kotestVersion")
        implementation("com.google.guava:guava:33.0.0-jre")
        implementation("org.postgresql:postgresql:42.7.2")
        implementation(project(":test"))
    }
}

tasks.create("integrationTest", Test::class) {
    group = "verification"
    description = "Runs integration tests."
    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath
    shouldRunAfter("test")
}
*/
