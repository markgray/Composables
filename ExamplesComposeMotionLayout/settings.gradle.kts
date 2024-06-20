pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage") // TODO: Keep an eye on this
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "ExamplesComposeMotionLayout"
include(":app")
