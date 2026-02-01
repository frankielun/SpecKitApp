# Flutter Demo App - SpecKit Wellness SDK

Flutter demonstration app showcasing the Kotlin Multiplatform (KMP) Health SDK integration via platform channels.

## Features

- **Step Count Tracking**: Fetch daily step count data from HealthKit (iOS) and Health Connect (Android)
- **Native Integration**: Platform channels bridging Flutter to KMP SDK
- **Cross-Platform UI**: Material Design 3 interface with bottom navigation
- **Permission Management**: Request and manage health data permissions
- **Real-Time Updates**: Pull-to-refresh and manual refresh capabilities
- **Progress Tracking**: Visual progress indicators for daily step goals

## Architecture

### Platform Channel Layer
- **iOS**: Swift bridge using FlutterMethodChannel
  - Integrates with KMP SDK via CocoaPods
  - Bridges HealthKit data to Flutter
- **Android**: Kotlin bridge using MethodChannel
  - Integrates with KMP SDK via composite build
  - Bridges Health Connect data to Flutter

### Dart Layer
- **Services**: `WellnessSDKService` - Platform channel wrapper with error handling
- **Models**: `HealthMetric` - Type-safe health data representation
- **Screens**: Home, Health Dashboard, Profile with Material Design 3
- **State Management**: StatefulWidget with async/await patterns

## Prerequisites

- Flutter SDK >=3.3.0
- Dart SDK >=3.3.0
- **iOS**:
  - Xcode 15.0+
  - CocoaPods
  - iOS 15.0+ deployment target
- **Android**:
  - Android Studio
  - JDK 17
  - Android SDK 26+ (minSdk 26, targetSdk 34)
  - Health Connect app installed on device

## Installation

### 1. Install Dependencies

```bash
cd flutter-app
flutter pub get
```

### 2. iOS Setup

```bash
cd ios
pod install
cd ..
```

**Info.plist Permissions** (Add to `ios/Runner/Info.plist`):

```xml
<key>NSHealthShareUsageDescription</key>
<string>We need access to your health data to track your wellness metrics</string>
<key>NSHealthUpdateUsageDescription</key>
<string>We need to update your health data for accurate tracking</string>
```

### 3. Android Setup

No additional setup required. Health Connect permissions are declared in `AndroidManifest.xml`.

**Note**: Health Connect must be installed on the Android device (Android 14+) or as a separate app (Android 9-13).

## Running the App

### iOS

```bash
flutter run -d iPhone
```

Or open in Xcode:

```bash
open ios/Runner.xcworkspace
```

### Android

```bash
flutter run -d <android-device-id>
```

Or open in Android Studio and run.

## Testing

### Run All Tests

```bash
flutter test
```

### Run with Coverage

```bash
flutter test --coverage
```

### Test Structure

- **Service Tests**: `test/services/wellness_sdk_service_test.dart`
  - Platform channel mocking
  - Error handling scenarios
  - Data parsing validation
- **Screen Tests**: `test/screens/`
  - Widget rendering
  - User interactions
  - State management

## Project Structure

```
flutter-app/
├── lib/
│   ├── main.dart                   # App entry point with navigation
│   ├── models/
│   │   └── health_metric.dart      # Health data model
│   ├── services/
│   │   └── wellness_sdk_service.dart # Platform channel wrapper
│   └── screens/
│       ├── home_screen.dart        # Welcome screen
│       ├── profile_screen.dart     # User profile
│       └── health_dashboard_screen.dart # Health data display
├── ios/
│   ├── Runner/
│   │   └── AppDelegate.swift       # iOS platform channel bridge
│   └── Podfile                     # CocoaPods dependencies
├── android/
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── kotlin/com/speckit/flutter/
│   │   │   │   └── MainActivity.kt # Android platform channel bridge
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts        # App-level build config
│   ├── build.gradle.kts            # Root-level build config
│   └── settings.gradle.kts         # KMP SDK integration
├── test/                           # Unit and widget tests
├── pubspec.yaml                    # Flutter dependencies
└── README.md
```

## Platform Channel Contract

### Methods

#### `requestPermissions()`
- **Returns**: `bool` - true if granted, false if denied
- **Errors**: 
  - `PERMISSION_DENIED`: User denied permissions
  - `DATA_NOT_AVAILABLE`: Health platform not available

#### `fetchStepCount(startDate, endDate)`
- **Parameters**:
  - `startDate`: `int` (milliseconds since epoch)
  - `endDate`: `int` (milliseconds since epoch)
- **Returns**: `Map<String, dynamic>` - HealthMetric JSON
  ```dart
  {
    "type": "STEP_COUNT",
    "value": 5000.0,
    "unit": "steps",
    "timestamp": 1234567890000,
    "source": "HealthKit" // or "HealthConnect"
  }
  ```
- **Errors**:
  - `PERMISSION_DENIED`: Missing permissions
  - `DATA_NOT_AVAILABLE`: No data in time range
  - `INVALID_ARGUMENTS`: Invalid date parameters

## Key Dependencies

```yaml
dependencies:
  flutter:
    sdk: flutter
  cupertino_icons: ^1.0.6

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^3.0.0
  mockito: ^5.4.4
  build_runner: ^2.4.8
```

## Platform-Specific Implementation

### iOS (Swift)

```swift
// FlutterMethodChannel setup in AppDelegate.swift
let channel = FlutterMethodChannel(
  name: "com.speckit.wellness_sdk",
  binaryMessenger: controller.binaryMessenger
)

channel.setMethodCallHandler { call, result in
  switch call.method {
  case "fetchStepCount":
    // Bridge to KMP SDK HealthDataRepository
  }
}
```

### Android (Kotlin)

```kotlin
// MethodChannel setup in MainActivity.kt
MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channelName)
  .setMethodCallHandler { call, result ->
    when (call.method) {
      "fetchStepCount" -> fetchStepCount(call, result)
    }
  }
```

## Error Handling

The app handles various error scenarios:

1. **Permission Denied**: Shows permission request UI
2. **Data Not Available**: Displays empty state with guidance
3. **Platform Errors**: Shows error message with retry option
4. **Network Issues**: Handled by native health platforms

## UI Screens

### Home Screen
- Welcome message
- App description
- Navigation hints

### Health Dashboard
- Real-time step count display
- Progress toward daily goal (10,000 steps)
- Source and timestamp information
- Pull-to-refresh support
- Manual refresh button

### Profile Screen
- User information (demo data)
- Settings navigation
- Notifications toggle
- Help & support links

## Troubleshooting

### iOS

**Issue**: CocoaPods installation fails
- **Solution**: Run `pod repo update` and `pod install --repo-update`

**Issue**: HealthKit permissions not showing
- **Solution**: Ensure Info.plist has `NSHealthShareUsageDescription` key

### Android

**Issue**: Health Connect not available
- **Solution**: Install Health Connect from Play Store (Android 9-13)

**Issue**: Gradle build fails
- **Solution**: Ensure JDK 17 is configured and `JAVA_HOME` is set

### General

**Issue**: Platform channel not working
- **Solution**: Check channel name matches in Dart and native code (`com.speckit.wellness_sdk`)

**Issue**: Tests failing
- **Solution**: Run `flutter clean && flutter pub get` and retry

## Known Limitations

- Heart rate monitoring not yet implemented (coming in Phase 6)
- iOS requires physical device for HealthKit testing (simulator not supported)
- Android Health Connect requires Android 9+ (API 28+)
- Historical data queries limited to single-day ranges in this demo

## Related Documentation

- [KMP SDK Documentation](../wellness-kmp-sdk/README.md)
- [iOS App Documentation](../ios-app/README.md)
- [Android App Documentation](../android-app/README.md)
- [React Native App Documentation](../react-native-app/README.md)

## License

Part of the SpecKit Wellness SDK PoC project.
