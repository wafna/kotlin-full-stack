rootProject.name = "kotlin-full-stack"

pluginManagement {
    val kotlinVersion: String by settings
    val dokkaVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion apply false
        id("org.jetbrains.dokka") version dokkaVersion apply false
    }
}

include(
    "common:logger",
    "common:kdbc",
    "common:test",
    "domain",
    "db",
    "api",
    "server",
    "demo",
    "browser:util",
    "browser:domain",
    "browser:api",
    "browser:gridley",
    "browser:plots",
    "browser:main"
)
