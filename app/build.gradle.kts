group = "wafna"
version = "0.0.1-SNAPSHOT"

plugins {
    id("com.github.ben-manes.versions") version "0.53.0" apply false
    id("org.owasp.dependencycheck") version "12.1.9" apply false
    id("com.savvasdalkitsis.module-dependency-graph") version "0.12"
    id("org.jetbrains.dokka")
}

subprojects {
    apply(plugin = "org.owasp.dependencycheck")
    apply(plugin = "com.github.ben-manes.versions")
}

repositories {
    mavenCentral()
}
