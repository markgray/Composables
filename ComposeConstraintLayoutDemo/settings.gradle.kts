pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage") // TODO: Keep an eye on this
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage") // TODO: Keep an eye on this
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "ComposeConstraintLayoutDemo"
include(":app")
