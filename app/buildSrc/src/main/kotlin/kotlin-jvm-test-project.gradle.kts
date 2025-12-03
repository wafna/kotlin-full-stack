plugins {
    id("kotlin-jvm-common")
    `java-library`
}

val kotestVersion: String by project

/**
 * The same as the test dependencies in `common` but in the `main` source tree.
 */
dependencies {
    api("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    api("io.kotest:kotest-assertions-core:$kotestVersion")
}
