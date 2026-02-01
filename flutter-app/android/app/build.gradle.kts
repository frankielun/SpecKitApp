// Android app build.gradle.kts for Flutter
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.speckit.flutter"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.speckit.flutter"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // KMP SDK dependency
    implementation("com.speckit.wellness:shared:1.0.0-SNAPSHOT")
    
    // Health Connect
    implementation("androidx.health.connect:connect-client:1.1.0-alpha07")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
