// settings.gradle.kts for Flutter Android
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

rootProject.name = "flutter_app"
include(":app")

// Include KMP SDK as composite build
includeBuild("../../wellness-kmp-sdk") {
    dependencySubstitution {
        substitute(module("com.speckit.wellness:shared")).using(project(":shared"))
    }
}

val flutterProjectRoot = settingsDir.parentFile
setBinding(mapOf("localProperties" to project.providers.gradleProperty("flutter.sdk")))
apply(from = File(flutterProjectRoot, ".flutter-plugins-dependencies"))
