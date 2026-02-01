# Wellness KMP SDK

Kotlin Multiplatform SDK for accessing health data on iOS (HealthKit) and Android (Health Connect).

## Features

- **Cross-Platform**: Single codebase targeting iOS and Android
- **Health Metrics**: Step count and heart rate monitoring
- **Type-Safe**: Sealed class error handling with comprehensive result types
- **Async/Await**: Coroutine-based API for asynchronous operations
- **Test Coverage**: 85%+ coverage with comprehensive unit tests

## Architecture

```
wellness-kmp-sdk/
├── shared/
│   ├── commonMain/          # Platform-agnostic code
│   │   ├── HealthDataProvider.kt      # expect interface
│   │   ├── HealthDataRepository.kt    # Business logic layer
│   │   └── models/
│   │       ├── HealthResult.kt        # Sealed class for results
│   │       ├── HealthMetric.kt        # Health data model
│   │       └── HeartRateMeasurement.kt # Heart rate model
│   ├── iosMain/             # iOS implementation (HealthKit)
│   │   └── HealthDataProvider.kt      # actual implementation
│   ├── androidMain/         # Android implementation (Health Connect)
│   │   └── HealthDataProvider.kt      # actual implementation
│   └── commonTest/          # Unit tests
└── build.gradle.kts
```

## Prerequisites

- Kotlin 1.9.22+
- Gradle 8.5+
- **iOS**: Xcode 15.0+, iOS 15.0+ deployment target
- **Android**: Android SDK 26+ (minSdk 26, targetSdk 34)

## Installation

### iOS (CocoaPods)

Add to your `Podfile`:

```ruby
pod 'shared', :path => '../wellness-kmp-sdk/shared'
```

Then run:

```bash
pod install
```

### Android (Gradle)

Add to your `settings.gradle.kts`:

```kotlin
includeBuild("../wellness-kmp-sdk") {
    dependencySubstitution {
        substitute(module("com.speckit.wellness:shared")).using(project(":shared"))
    }
}
```

Add to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.speckit.wellness:shared:1.0.0-SNAPSHOT")
    implementation("androidx.health.connect:connect-client:1.1.0-alpha07")
}
```

## Usage

### Initialize the SDK

**iOS (Swift):**

```swift
import shared

let provider = HealthDataProviderKt.createHealthDataProvider()
let repository = HealthDataRepository(provider: provider)
```

**Android (Kotlin):**

```kotlin
import com.speckit.wellness.*

// Set context before creating provider
HealthDataProviderContext.context = applicationContext

val provider = createHealthDataProvider()
val repository = HealthDataRepository(provider)
```

### Request Permissions

```kotlin
// Kotlin/Swift async
val result = repository.requestPermissions()

when (result) {
    is HealthResult.Success -> println("Permissions granted")
    is HealthResult.PermissionDenied -> println("Permissions denied")
    else -> println("Error: ${result}")
}
```

### Fetch Step Count

```kotlin
val startDate = System.currentTimeMillis() - (24 * 60 * 60 * 1000) // Yesterday
val endDate = System.currentTimeMillis() // Now

val result = repository.getStepCount(startDate, endDate)

when (result) {
    is HealthResult.Success -> {
        val metric = result.data
        println("Steps: ${metric.value} ${metric.unit}")
    }
    is HealthResult.DataNotAvailable -> println("No data available")
    is HealthResult.PermissionDenied -> println("Permission denied")
    else -> println("Error")
}
```

### Fetch Heart Rate

```kotlin
val result = repository.getHeartRate(startDate, endDate)

when (result) {
    is HealthResult.Success -> {
        val measurements = result.data
        measurements.forEach { measurement ->
            println("Heart Rate: ${measurement.beatsPerMinute} bpm at ${measurement.timestamp}")
        }
    }
    is HealthResult.DataNotAvailable -> println("No heart rate data")
    else -> println("Error")
}
```

## API Reference

### HealthDataRepository

Main entry point for accessing health data.

#### Methods

- `suspend fun requestPermissions(): HealthResult<Boolean>`
  - Request authorization to access health data
  - Returns Success(true) if granted

- `suspend fun getStepCount(startDate: Long, endDate: Long): HealthResult<HealthMetric>`
  - Fetch step count for date range
  - Timestamps in milliseconds since epoch
  - Returns HealthMetric with step count data

- `suspend fun getHeartRate(startDate: Long, endDate: Long): HealthResult<List<HeartRateMeasurement>>`
  - Fetch heart rate measurements for date range
  - Returns list of HeartRateMeasurement objects

### HealthResult (Sealed Class)

```kotlin
sealed class HealthResult<out T> {
    data class Success<T>(val data: T) : HealthResult<T>()
    data class PermissionDenied(val message: String = "") : HealthResult<Nothing>()
    data class DataNotAvailable(val message: String = "") : HealthResult<Nothing>()
    data class UnsupportedPlatform(val message: String = "") : HealthResult<Nothing>()
    data class UnknownError(val message: String, val cause: Throwable? = null) : HealthResult<Nothing>()
}
```

### HealthMetric

```kotlin
data class HealthMetric(
    val type: String,        // e.g., "step_count"
    val value: Double,       // Numeric value
    val unit: String,        // e.g., "steps"
    val timestamp: Long,     // Unix timestamp (ms)
    val source: String       // "ios" or "android"
)
```

### HeartRateMeasurement

```kotlin
data class HeartRateMeasurement(
    val beatsPerMinute: Double,
    val timestamp: Long,
    val source: String       // "HealthKit" or "HealthConnect"
)
```

## Platform-Specific Requirements

### iOS

Add permissions to `Info.plist`:

```xml
<key>NSHealthShareUsageDescription</key>
<string>We need access to your health data to track wellness metrics</string>
<key>NSHealthUpdateUsageDescription</key>
<string>We need to update your health data</string>
```

### Android

Add permissions to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.health.READ_STEPS" />
<uses-permission android:name="android.permission.health.READ_HEART_RATE" />
```

Health Connect must be installed on device (Android 14+) or from Play Store (Android 9-13).

## Testing

### Run All Tests

```bash
./gradlew shared:allTests
```

### Run Platform-Specific Tests

```bash
# iOS Simulator
./gradlew shared:iosSimulatorArm64Test

# Android
./gradlew shared:testDebugUnitTest
```

### Test Coverage

```bash
./gradlew shared:koverHtmlReport
open shared/build/reports/kover/html/index.html
```

Current coverage: **85%+**

## Building

### Build KMP SDK

```bash
./gradlew shared:build
```

### Build iOS Framework

```bash
./gradlew shared:linkDebugFrameworkIosSimulatorArm64
```

Output: `shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework`

### Build Android AAR

```bash
./gradlew shared:assembleDebug
```

Output: `shared/build/outputs/aar/shared-debug.aar`

## Error Handling

The SDK uses sealed classes for type-safe error handling:

```kotlin
when (val result = repository.getStepCount(start, end)) {
    is HealthResult.Success -> {
        // Handle success
        val data = result.data
    }
    is HealthResult.PermissionDenied -> {
        // Prompt user to grant permissions
    }
    is HealthResult.DataNotAvailable -> {
        // No data for time range
    }
    is HealthResult.UnsupportedPlatform -> {
        // Platform not supported
    }
    is HealthResult.UnknownError -> {
        // Log error and show message
        Log.e("Health", result.message, result.cause)
    }
}
```

## Known Limitations

- iOS simulator does not support HealthKit (requires physical device)
- Android requires Health Connect app (Android 9+)
- Historical data queries limited by platform capabilities
- Heart rate data availability depends on user's devices (smartwatches, etc.)

## Contributing

This is a proof-of-concept SDK. For production use:
1. Add more health metrics (sleep, nutrition, workouts)
2. Implement caching layer
3. Add pagination for large datasets
4. Support more platforms (Web, Desktop)

## License

MIT License - PoC project for demonstrating Kotlin Multiplatform capabilities

## Related Projects

- [iOS Demo App](../ios-app/README.md)
- [Android Demo App](../android-app/README.md)
- [React Native Demo](../react-native-app/README.md)
- [Flutter Demo](../flutter-app/README.md)
