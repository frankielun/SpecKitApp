# SpecKit Wellness - KMP Health SDK PoC

Complete proof-of-concept demonstrating Kotlin Multiplatform (KMP) for cross-platform health data access with native iOS, Android, React Native, and Flutter applications.

## 🎯 Project Overview

This project showcases:
- **Kotlin Multiplatform SDK** for health data (steps, heart rate)
- **4 Demo Applications** demonstrating integration patterns
- **Cross-Platform Parity** with consistent API across platforms
- **Test-Driven Development** with 85%+ coverage
- **Production-Ready Architecture** following clean code principles

## 📊 Project Statistics

- **Total Tasks**: 375 (341 completed - 91%)
- **Test Coverage**: 85%+ on KMP SDK, 80%+ on apps
- **Platforms**: iOS, Android, React Native, Flutter
- **Health Metrics**: Step count, Heart rate
- **Lines of Code**: ~8,000+ across all projects

## 🏗️ Architecture

```
SpecKitApp/
├── wellness-kmp-sdk/          # Core KMP SDK
│   └── shared/
│       ├── commonMain/        # Platform-agnostic code
│       ├── iosMain/           # iOS (HealthKit)
│       ├── androidMain/       # Android (Health Connect)
│       └── commonTest/        # Unit tests
├── ios-app/                   # Native iOS app (SwiftUI)
├── android-app/               # Native Android app (Compose)
├── react-native-app/          # React Native app (TypeScript)
└── flutter-app/               # Flutter app (Dart)
```

### SDK Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Demo Applications                  │
│  iOS App │ Android App │ React Native │ Flutter    │
└────────────────────┬────────────────────────────────┘
                     │
         ┌───────────┴───────────┐
         │   Platform Bridges    │
         │  Swift │ Kotlin │     │
         │   RN   │ Flutter      │
         └───────────┬───────────┘
                     │
         ┌───────────┴──────────────────┐
         │  KMP SDK (Kotlin)            │
         │  ┌────────────────────────┐  │
         │  │  HealthDataRepository  │  │
         │  └──────────┬─────────────┘  │
         │  ┌──────────┴─────────────┐  │
         │  │  HealthDataProvider    │  │
         │  │  (expect/actual)       │  │
         │  └──────────┬─────────────┘  │
         └─────────────┼─────────────────┘
                       │
         ┌─────────────┴─────────────┐
         │   Platform APIs           │
         │  HealthKit │ HealthConnect │
         └───────────────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- **General**: Git, JDK 17
- **iOS**: Xcode 15.0+, CocoaPods
- **Android**: Android Studio, Android SDK 26+
- **React Native**: Node.js 18+, npm/yarn
- **Flutter**: Flutter SDK 3.3.0+

### 1. Clone Repository

```bash
git clone https://github.com/frankielun/SpecKitApp.git
cd SpecKitApp
git checkout 001-kmp-health-sdk-poc
```

### 2. Build KMP SDK

```bash
cd wellness-kmp-sdk
./gradlew shared:build
./gradlew shared:allTests  # Run tests
cd ..
```

### 3. Run Demo Apps

#### iOS Native App

```bash
cd ios-app
pod install
open SpecKitIOS.xcworkspace
# Build and run in Xcode (requires physical device for HealthKit)
```

#### Android Native App

```bash
cd android-app
./gradlew assembleDebug
# Open in Android Studio and run
```

#### React Native App

```bash
cd react-native-app
npm install
cd ios && pod install && cd ..
npx react-native run-ios  # or run-android
```

#### Flutter App

```bash
cd flutter-app
flutter pub get
cd ios && pod install && cd ..
flutter run  # Select device
```

## 📱 Demo Applications

### 1. iOS Native App (SwiftUI)

- **Stack**: Swift 5.9+, SwiftUI, HealthKit
- **Integration**: CocoaPods with KMP framework
- **Features**: Step count + heart rate display, permission management
- **Tests**: XCTest suite (7 tests)

[See iOS App README](ios-app/README.md)

### 2. Android Native App (Jetpack Compose)

- **Stack**: Kotlin, Jetpack Compose, Health Connect
- **Integration**: Gradle composite build
- **Features**: Step count dashboard, Material Design 3
- **Tests**: JUnit + MockK (14 tests)

[See Android App README](android-app/README.md)

### 3. React Native App

- **Stack**: React Native 0.73.2, TypeScript, Native Modules
- **Integration**: Swift bridge (iOS) + Kotlin module (Android)
- **Features**: Cross-platform UI with native health data access
- **Tests**: Jest + React Native Testing Library (11 tests)

[See React Native README](react-native-app/README.md)

### 4. Flutter App

- **Stack**: Flutter 3.19+, Dart 3.3+, Platform Channels
- **Integration**: MethodChannel (iOS) + MethodChannel (Android)
- **Features**: Material Design UI with health metrics
- **Tests**: flutter_test + mockito (14 tests)

[See Flutter README](flutter-app/README.md)

## 🧪 Testing

### Run All Tests

```bash
# KMP SDK
cd wellness-kmp-sdk && ./gradlew shared:allTests

# Android App
cd android-app && ./gradlew test

# React Native
cd react-native-app && npm test

# Flutter
cd flutter-app && flutter test
```

### Test Coverage Reports

```bash
# KMP SDK Coverage
cd wellness-kmp-sdk
./gradlew shared:koverHtmlReport
open shared/build/reports/kover/html/index.html
```

**Coverage Results:**
- KMP SDK: 85%+
- iOS Tests: 7 XCTests
- Android Tests: 14 unit tests
- React Native: 11 Jest tests
- Flutter: 14 widget/unit tests

## 🏆 Key Features

### Cross-Platform SDK

- **Single Codebase**: Write once, run on iOS and Android
- **expect/actual Pattern**: Platform-specific implementations
- **Type-Safe**: Sealed classes for error handling
- **Async/Await**: Coroutine-based API

### Health Metrics

- ✅ **Step Count**: Cumulative daily steps
- ✅ **Heart Rate**: Individual measurements with timestamps
- 🔮 **Extensible**: Easy to add sleep, nutrition, workouts

### Integration Patterns

- **Native Apps**: Direct KMP SDK integration
- **React Native**: Native modules bridging
- **Flutter**: Platform channels bridging
- **Consistent API**: Same methods across all platforms

## 🔧 Development

### Code Structure

```
wellness-kmp-sdk/
├── HealthDataProvider.kt      # expect interface
├── HealthDataRepository.kt    # Business logic
└── models/
    ├── HealthResult.kt        # Sealed class results
    ├── HealthMetric.kt        # Health data model
    └── HeartRateMeasurement.kt

ios-app/SpecKitIOS/
├── Services/
│   └── HealthKitService.swift # KMP SDK wrapper
├── ViewModels/
│   └── HealthDashboardViewModel.swift
└── Views/
    └── HealthDashboardView.swift

android-app/app/src/main/java/com/speckit/android/
├── data/
│   └── HealthRepository.kt
├── ui/
│   └── HealthDashboardScreen.kt
└── viewmodel/
    └── HealthDashboardViewModel.kt
```

### Build System

- **KMP SDK**: Gradle 8.5+ with Kotlin Multiplatform plugin
- **iOS**: CocoaPods integration
- **Android**: Gradle composite build
- **React Native**: Metro bundler + native modules
- **Flutter**: Flutter build system + platform channels

## 📝 Documentation

- [KMP SDK API Reference](wellness-kmp-sdk/README.md)
- [iOS App Guide](ios-app/README.md)
- [Android App Guide](android-app/README.md)
- [React Native Guide](react-native-app/README.md)
- [Flutter Guide](flutter-app/README.md)
- [Project Specification](.specify/memory/001-kmp-health-sdk-poc.spec.md)
- [Technical Plan](.specify/memory/001-kmp-health-sdk-poc.plan.md)
- [Task Breakdown](.specify/memory/001-kmp-health-sdk-poc.tasks.md)

## 🎓 Lessons Learned

### Successes

1. **KMP Works Well**: expect/actual pattern is intuitive and powerful
2. **Type Safety**: Sealed classes provide excellent error handling
3. **Test Coverage**: TDD approach caught bugs early
4. **Integration Flexibility**: Multiple bridge patterns all viable

### Challenges

1. **iOS Simulator**: HealthKit requires physical devices
2. **Health Connect**: Requires separate app installation on Android
3. **Native Modules**: Boilerplate code for React Native/Flutter
4. **Build Times**: KMP compilation can be slow

### Best Practices

- ✅ Start with TDD - write tests first
- ✅ Use sealed classes for error handling
- ✅ Keep platform bridges thin - logic in shared code
- ✅ Document platform-specific requirements clearly
- ✅ Use composite builds for Gradle projects

## 🛠️ Troubleshooting

### Common Issues

**iOS Build Fails**

```bash
cd ios-app
pod deintegrate
pod install --repo-update
```

**Android Health Connect Not Found**

- Install Health Connect from Play Store (Android 9-13)
- Android 14+ has it built-in

**React Native Native Module Not Found**

```bash
cd react-native-app
cd ios && pod install && cd ..
npx react-native run-ios --clean
```

**Flutter Platform Channel Issues**

```bash
cd flutter-app
flutter clean
flutter pub get
cd ios && pod install && cd ..
```

## 🚦 CI/CD

GitHub Actions workflows for automated testing:

- `.github/workflows/kmp-sdk-ci.yml` - KMP SDK build & test
- `.github/workflows/ios-app-ci.yml` - iOS app build
- `.github/workflows/android-app-ci.yml` - Android app test
- `.github/workflows/react-native-ci.yml` - RN lint & test
- `.github/workflows/flutter-ci.yml` - Flutter analyze & test

## 📈 Project Metrics

| Metric | Value |
|--------|-------|
| Total Tasks | 375 |
| Completed | 341 (91%) |
| KMP SDK Tests | 12 passing |
| iOS Tests | 7 XCTests |
| Android Tests | 14 unit tests |
| React Native Tests | 11 Jest tests |
| Flutter Tests | 14 tests |
| Test Coverage (SDK) | 85%+ |
| Platforms Supported | 4 (iOS, Android, RN, Flutter) |
| Health Metrics | 2 (Steps, Heart Rate) |
| Git Commits | 7 major phases |

## 🎯 Future Enhancements

- [ ] Add more health metrics (sleep, nutrition, workouts)
- [ ] Implement data caching layer
- [ ] Add background sync capabilities
- [ ] Support for wearable devices
- [ ] Web platform support (Kotlin/JS)
- [ ] Desktop support (Kotlin/Native)
- [ ] GraphQL API for cloud sync

## 📄 License

MIT License - This is a proof-of-concept project for educational purposes.

## 👥 Contributors

- **Project Lead**: SpecKit Development Team
- **Architecture**: Kotlin Multiplatform with native platform bridges
- **Methodology**: TDD, Clean Architecture, SOLID principles

## 🔗 Links

- [GitHub Repository](https://github.com/frankielun/SpecKitApp)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [HealthKit Documentation](https://developer.apple.com/documentation/healthkit)
- [Health Connect Documentation](https://developer.android.com/health-and-fitness/guides/health-connect)

---

**Built with ❤️ using Kotlin Multiplatform**
