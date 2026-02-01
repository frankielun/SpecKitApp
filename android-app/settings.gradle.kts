// T133: Include KMP SDK shared module
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SpecKitAndroid"

include(":app")
includeBuild("../wellness-kmp-sdk") {
    dependencySubstitution {
        substitute(module("com.speckit.wellness:shared")).using(project(":shared"))
    }
}
