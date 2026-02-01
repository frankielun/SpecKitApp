// T011: Configure shared module with kotlin-multiplatform plugin
plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
}

kotlin {
    // T010: Configure multiplatform targets
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17" // T014
            }
        }
    }
    
    iosArm64() // T010: Physical iOS devices
    iosSimulatorArm64() // T010: iOS Simulator (Apple Silicon)
    
    // T014: Configure iOS compiler flags
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations.all {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xallocator=custom")
            }
        }
        
        // Export framework for CocoaPods
        binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        // T012: Add kotlinx dependencies
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
                // T027: MockK will be added in test tasks
            }
        }
        
        val androidMain by getting {
            dependencies {
                // T013: Add Android-specific dependency
                implementation("androidx.health.connect:connect-client:1.1.0-alpha07")
            }
        }
        
        val iosMain by creating {
            dependsOn(commonMain)
        }
        
        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
        
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
    }
}

android {
    namespace = "com.speckit.wellness"
    compileSdk = 34 // T015
    
    defaultConfig {
        minSdk = 26 // T015
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // T014
        targetCompatibility = JavaVersion.VERSION_17
    }
}
