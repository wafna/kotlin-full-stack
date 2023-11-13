plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

val kotlinVersion: String by project

dependencies {
    api("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    api("org.slf4j:slf4j-api:2.0.7")
    api("ch.qos.logback:logback-classic:1.4.7")
    api("commons-codec:commons-codec:1.15")
}
