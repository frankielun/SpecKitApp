// T009: Root build configuration for KMP SDK
plugins {
    // Apply Kotlin Multiplatform plugin to all modules (configured in subprojects)
    kotlin("multiplatform") version "1.9.22" apply false
    kotlin("plugin.serialization") version "1.9.22" apply false
    id("com.android.library") version "8.2.2" apply false
}

allprojects {
    group = "com.speckit.wellness"
    version = "1.0.0-SNAPSHOT"
}
