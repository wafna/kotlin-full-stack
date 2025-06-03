plugins {
    id("kotlin-web-common")
}

kotlin {
    sourceSets {
        @Suppress("unused")
        val jsMain by getting {
            dependencies {
                api(project(":browser:util"))
            }
        }
    }
}
