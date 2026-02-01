// T202: settings.gradle.kts for React Native
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SpecKitRN"
include(":app")

// Include KMP SDK as composite build
includeBuild("../../wellness-kmp-sdk") {
    dependencySubstitution {
        substitute(module("com.speckit.wellness:shared")).using(project(":shared"))
    }
}
