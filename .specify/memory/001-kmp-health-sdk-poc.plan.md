# Implementation Plan: KMP Health SDK PoC

**Branch**: `001-kmp-health-sdk-poc` | **Date**: 2026-02-01 | **Spec**: [001-kmp-health-sdk-poc.spec.md](./001-kmp-health-sdk-poc.spec.md)
**Input**: Feature specification for building a Kotlin Multiplatform health data SDK with cross-framework integration

## Summary

Build a proof-of-concept Kotlin Multiplatform (KMP) SDK that provides unified health data access (HealthKit on iOS, Health Connect on Android) and validate its integration across four mobile frameworks: Native iOS (Swift/SwiftUI), Native Android (Kotlin/Jetpack Compose), React Native (TypeScript), and Flutter (Dart). The SDK will use Kotlin's expect/actual pattern for platform abstraction, expose coroutine-based async APIs, and handle permissions/errors gracefully across platform boundaries.

## Technical Context

**Language/Version**: 
- Kotlin 1.9.22+ (KMP SDK + Android app)
- Swift 5.9+ (iOS app)
- TypeScript 5.3+ (React Native app)
- Dart 3.3+ (Flutter app)

**Primary Dependencies**:
- KMP SDK: kotlinx.coroutines 1.7.3, kotlinx.serialization 1.6.0, kotlinx.datetime 0.5.0
- iOS: HealthKit framework (native), CocoaPods for KMP integration
- Android: androidx.health.connect:connect-client:1.1.0-alpha07
- React Native: React Native 0.73+, native modules for iOS/Android bridge
- Flutter: Flutter 3.19+, platform channels for iOS/Android bridge

**Storage**: 
- Health data is read-only from platform health stores (HealthKit/Health Connect)
- No local database required (all data fetched on-demand)
- Optional in-memory caching for performance optimization

**Testing**:
- KMP SDK: kotlin-test, MockK, kotlinx-coroutines-test
- iOS app: XCTest
- Android app: JUnit + MockK
- React Native: Jest + React Native Testing Library
- Flutter: flutter_test + Mockito

**Target Platform**: 
- iOS 15.0+ (iPhone, iPad)
- Android 8.0+ (API 26+), targetSdk 34
- Requires physical devices or simulators with HealthKit/Health Connect capabilities

**Project Type**: Mobile (multiple sub-projects: 1 SDK + 4 demo apps)

**Performance Goals**: 
- Health data fetching completes in <5 seconds
- UI remains responsive (async/background fetching)
- App launch overhead from KMP SDK <500ms

**Constraints**: 
- Must handle platform-specific permissions without crashes
- Must work identically on iOS and Android (cross-platform parity)
- Must integrate via standard mechanisms (CocoaPods, Gradle, native modules, platform channels)
- Read-only access (no writing health data)

**Scale/Scope**: 
- 1 KMP SDK module (~1000 LOC)
- 4 demo apps (each ~500-800 LOC)
- ~20 unit tests per module/app
- 80-85% test coverage target

## Constitution Check

✅ **I. Cross-Platform Compatibility First (NON-NEGOTIABLE)**
- SDK uses expect/actual for iOS/Android platform abstraction
- All 4 demo apps integrate and validate identical functionality
- Manual testing required on both platforms

✅ **II. Test-First Development (NON-NEGOTIABLE)**
- TDD workflow: Write tests → Implement → Refactor
- 85%+ coverage for KMP SDK, 80%+ for demo apps
- CI enforces coverage thresholds

✅ **III. Clean Code & SOLID Principles**
- Repository pattern for data access
- Dependency injection for platform-specific implementations
- Follow language-specific style guides (Kotlin Conventions, Swift API Guidelines, etc.)

✅ **IV. expect/actual Pattern for Platform Abstraction**
- `HealthDataProvider` interface in commonMain with expect declarations
- Actual implementations in iosMain (HealthKit) and androidMain (Health Connect)
- Thin platform wrappers around native APIs

✅ **V. Comprehensive Error Handling & Observability**
- Result type or sealed classes for error propagation
- User-friendly error messages with actionable guidance
- Logging for debugging platform-specific failures

✅ **VI. Code Quality Enforcement via Linting & Formatting**
- Pre-commit hooks: ktlint/Detekt (Kotlin), SwiftLint (Swift), ESLint (TypeScript), dart analyze (Dart)
- CI fails on lint violations or coverage drops

✅ **VII. Documentation as Code**
- KDoc for KMP SDK public APIs
- DocC comments for Swift APIs
- TSDoc for TypeScript, DartDoc for Dart
- README for each module with setup, usage, testing instructions

## Project Structure

### Documentation (this feature)

```text
.specify/memory/
├── constitution.md                          # Project governance (v1.0.0)
├── 001-kmp-health-sdk-poc.spec.md          # Feature specification
├── 001-kmp-health-sdk-poc.plan.md          # This implementation plan
├── 001-kmp-health-sdk-poc.research.md      # Phase 0: Research findings
├── 001-kmp-health-sdk-poc.data-model.md    # Phase 1: Data models
├── 001-kmp-health-sdk-poc.contracts.md     # Phase 1: API contracts
├── 001-kmp-health-sdk-poc.quickstart.md    # Phase 1: Setup guide
└── 001-kmp-health-sdk-poc.tasks.md         # Phase 2: Task breakdown (via /speckit.tasks)
```

### Source Code (repository root)

```text
SpecKitApp/
├── wellness-kmp-sdk/                        # KMP SDK module
│   ├── shared/                              # Multiplatform shared module
│   │   ├── src/
│   │   │   ├── commonMain/
│   │   │   │   └── kotlin/
│   │   │   │       └── com/speckit/wellness/
│   │   │   │           ├── HealthDataProvider.kt      # expect interface
│   │   │   │           ├── HealthDataRepository.kt    # business logic
│   │   │   │           ├── models/
│   │   │   │           │   ├── HealthMetric.kt
│   │   │   │           │   ├── HeartRateMeasurement.kt
│   │   │   │           │   └── HealthResult.kt         # sealed class
│   │   │   │           └── util/
│   │   │   │               └── DateUtils.kt
│   │   │   ├── commonTest/
│   │   │   │   └── kotlin/
│   │   │   │       └── com/speckit/wellness/
│   │   │   │           └── HealthDataRepositoryTest.kt
│   │   │   ├── iosMain/
│   │   │   │   └── kotlin/
│   │   │   │       └── com/speckit/wellness/
│   │   │   │           └── HealthDataProvider.kt      # actual HealthKit wrapper
│   │   │   ├── iosTest/
│   │   │   │   └── kotlin/
│   │   │   │       └── com/speckit/wellness/
│   │   │   │           └── HealthKitWrapperTest.kt
│   │   │   ├── androidMain/
│   │   │   │   └── kotlin/
│   │   │   │       └── com/speckit/wellness/
│   │   │   │           └── HealthDataProvider.kt      # actual Health Connect wrapper
│   │   │   └── androidTest/
│   │   │       └── kotlin/
│   │   │           └── com/speckit/wellness/
│   │   │               └── HealthConnectWrapperTest.kt
│   │   └── build.gradle.kts
│   ├── build.gradle.kts
│   ├── gradle.properties
│   ├── settings.gradle.kts
│   ├── WellnessSDK.podspec                  # CocoaPods spec for iOS
│   └── README.md
│
├── ios-app/                                 # Native iOS demo app
│   ├── SpecKitIOS.xcodeproj/
│   ├── SpecKitIOS/
│   │   ├── App/
│   │   │   └── SpecKitIOSApp.swift         # SwiftUI app entry
│   │   ├── Views/
│   │   │   ├── ContentView.swift            # Tab container
│   │   │   ├── HomeView.swift               # Tab 1: Native home
│   │   │   ├── ProfileView.swift            # Tab 2: Native profile
│   │   │   └── HealthDashboardView.swift    # Tab 3: KMP SDK integration
│   │   ├── ViewModels/
│   │   │   └── HealthDashboardViewModel.swift
│   │   ├── Services/
│   │   │   └── HealthKitService.swift       # Wrapper around KMP SDK
│   │   └── Info.plist
│   ├── SpecKitIOSTests/
│   │   └── HealthDashboardViewModelTests.swift
│   ├── Podfile                              # Links to wellness-kmp-sdk
│   └── README.md
│
├── android-app/                             # Native Android demo app
│   ├── app/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── kotlin/com/speckit/android/
│   │   │   │   │   ├── MainActivity.kt       # Jetpack Compose entry
│   │   │   │   │   ├── ui/
│   │   │   │   │   │   ├── HomeScreen.kt     # Tab 1: Native home
│   │   │   │   │   │   ├── ProfileScreen.kt  # Tab 2: Native profile
│   │   │   │   │   │   └── HealthDashboardScreen.kt # Tab 3: KMP SDK
│   │   │   │   │   ├── viewmodel/
│   │   │   │   │   │   └── HealthDashboardViewModel.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       └── HealthRepository.kt # Wrapper around KMP SDK
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/
│   │   │       └── kotlin/com/speckit/android/
│   │   │           └── HealthRepositoryTest.kt
│   │   └── build.gradle.kts
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── README.md
│
├── react-native-app/                        # React Native demo app
│   ├── src/
│   │   ├── App.tsx                          # Tab navigation setup
│   │   ├── screens/
│   │   │   ├── HomeScreen.tsx               # Tab 1: Native home
│   │   │   ├── ProfileScreen.tsx            # Tab 2: Native profile
│   │   │   └── HealthDashboardScreen.tsx    # Tab 3: KMP SDK via native module
│   │   ├── hooks/
│   │   │   └── useHealthData.ts
│   │   └── services/
│   │       └── WellnessSDK.ts               # TypeScript wrapper for native module
│   ├── android/
│   │   └── app/src/main/java/com/speckit/rn/
│   │       ├── WellnessSDKModule.kt         # Android native module (wraps KMP SDK)
│   │       └── WellnessSDKPackage.kt
│   ├── ios/
│   │   └── SpecKitRN/
│   │       ├── WellnessSDKBridge.swift      # iOS native module (wraps KMP SDK)
│   │       └── WellnessSDKBridge.m          # Objective-C bridge header
│   ├── __tests__/
│   │   └── HealthDashboardScreen.test.tsx
│   ├── package.json
│   ├── tsconfig.json
│   └── README.md
│
├── flutter-app/                             # Flutter demo app
│   ├── lib/
│   │   ├── main.dart                        # App entry
│   │   ├── screens/
│   │   │   ├── home_screen.dart             # Tab 1: Native home
│   │   │   ├── profile_screen.dart          # Tab 2: Native profile
│   │   │   └── health_dashboard_screen.dart # Tab 3: KMP SDK via platform channel
│   │   ├── services/
│   │   │   └── wellness_sdk_service.dart    # Dart wrapper for platform channel
│   │   └── models/
│   │       └── health_metric.dart
│   ├── android/
│   │   └── app/src/main/kotlin/com/speckit/flutter/
│   │       └── WellnessSDKPlugin.kt         # Android plugin (wraps KMP SDK)
│   ├── ios/
│   │   └── Runner/
│   │       └── WellnessSDKPlugin.swift      # iOS plugin (wraps KMP SDK)
│   ├── test/
│   │   └── health_dashboard_screen_test.dart
│   ├── pubspec.yaml
│   └── README.md
│
├── .github/
│   ├── copilot/
│   │   └── default.instructions.md          # General Copilot instructions
│   ├── agents/
│   │   └── speckit.*.agent.md               # Spec-kit agent configurations
│   └── workflows/
│       ├── kmp-sdk-ci.yml                   # CI for KMP SDK
│       ├── ios-app-ci.yml                   # CI for iOS app
│       ├── android-app-ci.yml               # CI for Android app
│       ├── react-native-ci.yml              # CI for React Native app
│       └── flutter-ci.yml                   # CI for Flutter app
│
├── .specify/                                # Spec-kit configuration
│   ├── memory/                              # Specifications and plans
│   ├── scripts/                             # Spec-kit scripts
│   └── templates/                           # Document templates
│
├── .gitignore
└── README.md                                # Project overview
```

## Architecture & Design Patterns

### KMP SDK Architecture (wellness-kmp-sdk)

**Layered Architecture**:
```
┌─────────────────────────────────────────┐
│      Demo Apps (iOS/Android/RN/Flutter) │
└───────────────┬─────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────┐
│    HealthDataRepository (commonMain)    │  ← Business Logic
│    - fetchStepCount()                   │
│    - fetchHeartRate()                   │
│    - Error handling & validation        │
└───────────────┬─────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────┐
│  HealthDataProvider (expect interface)  │  ← Platform Abstraction
└───────────────┬─────────────────────────┘
                │
        ┌───────┴───────┐
        ▼               ▼
┌──────────────┐  ┌──────────────┐
│  iosMain     │  │ androidMain  │  ← Platform Implementations
│  (HealthKit) │  │ (Health      │
│  (actual)    │  │  Connect)    │
│              │  │  (actual)    │
└──────────────┘  └──────────────┘
```

**Key Design Patterns**:
1. **Repository Pattern**: `HealthDataRepository` encapsulates data fetching logic
2. **expect/actual**: Platform abstraction without runtime overhead
3. **Result Type**: Sealed class for type-safe error handling
4. **Dependency Injection**: Constructor injection for `HealthDataProvider`
5. **Coroutines**: All async operations use `suspend` functions

### Demo App Architecture (All 4 Apps)

**MVVM Pattern** (Model-View-ViewModel):
```
View (UI Layer)
  ↕
ViewModel (Presentation Logic)
  ↕
Repository/Service (Data Layer) → KMP SDK
```

**Tab Structure**:
- Tab 1 (Home): Pure native code, no KMP dependency
- Tab 2 (Profile): Pure native code, no KMP dependency
- Tab 3 (Health Dashboard): Integrates KMP SDK via platform-specific bridge

### React Native Bridge Architecture

```
TypeScript (React Native)
         ↕
WellnessSDK.ts (JS interface)
         ↕
Native Modules (iOS: Swift, Android: Kotlin)
         ↕
wellness-kmp-sdk (Kotlin MP)
         ↕
HealthKit / Health Connect
```

**Bridge Pattern**: Native modules wrap KMP SDK and expose promise-based JavaScript API.

### Flutter Platform Channel Architecture

```
Dart (Flutter)
         ↕
WellnessSDKService (Dart interface)
         ↕
MethodChannel
         ↕
Platform Plugins (iOS: Swift, Android: Kotlin)
         ↕
wellness-kmp-sdk (Kotlin MP)
         ↕
HealthKit / Health Connect
```

**Channel Pattern**: Platform-specific plugins wrap KMP SDK and communicate via method channels.

## Data Models

### Core Data Models (commonMain)

**HealthMetric** (Step Count):
```kotlin
data class HealthMetric(
    val value: Long,           // Step count
    val unit: String,          // "steps"
    val startDate: Long,       // Unix timestamp (ms)
    val endDate: Long,         // Unix timestamp (ms)
    val source: String         // "HealthKit" or "Health Connect"
)
```

**HeartRateMeasurement**:
```kotlin
data class HeartRateMeasurement(
    val bpm: Double,           // Beats per minute
    val timestamp: Long,       // Unix timestamp (ms)
    val source: String         // "HealthKit" or "Health Connect"
)
```

**HealthResult** (Sealed Class for Error Handling):
```kotlin
sealed class HealthResult<out T> {
    data class Success<T>(val data: T) : HealthResult<T>()
    
    sealed class Error : HealthResult<Nothing>() {
        data class PermissionDenied(val message: String) : Error()
        data class DataNotAvailable(val message: String) : Error()
        data class UnsupportedPlatform(val message: String) : Error()
        data class UnknownError(val message: String, val cause: Throwable?) : Error()
    }
}
```

### API Contracts

**HealthDataProvider Interface** (expect/actual):
```kotlin
// commonMain
expect class HealthDataProvider {
    suspend fun requestAuthorization(): HealthResult<Boolean>
    suspend fun fetchStepCount(startDate: Long, endDate: Long): HealthResult<HealthMetric>
    suspend fun fetchHeartRate(startDate: Long, endDate: Long): HealthResult<List<HeartRateMeasurement>>
}
```

**HealthDataRepository** (commonMain):
```kotlin
class HealthDataRepository(
    private val provider: HealthDataProvider
) {
    suspend fun getStepCount(startDate: Long, endDate: Long): HealthResult<HealthMetric> {
        return provider.fetchStepCount(startDate, endDate)
    }
    
    suspend fun getHeartRate(startDate: Long, endDate: Long): HealthResult<List<HeartRateMeasurement>> {
        return provider.fetchHeartRate(startDate, endDate)
    }
    
    suspend fun requestPermissions(): HealthResult<Boolean> {
        return provider.requestAuthorization()
    }
}
```

## Development Phases

### Phase 0: Research & Validation ✅

**Goal**: Validate technical feasibility of KMP + HealthKit/Health Connect integration.

**Tasks**:
1. ✅ Research Kotlin Multiplatform expect/actual mechanism
2. ✅ Research HealthKit APIs (HKQuantityType, HKStatisticsQuery)
3. ✅ Research Health Connect APIs (ReadRecordsRequest, StepsRecord)
4. ✅ Research CocoaPods integration for KMP frameworks
5. ✅ Research React Native native modules (Swift + Kotlin)
6. ✅ Research Flutter platform channels (MethodChannel)
7. ✅ Document findings in `001-kmp-health-sdk-poc.research.md`

**Output**: Research document confirming all integrations are technically feasible.

---

### Phase 1: KMP SDK Foundation (P1)

**Goal**: Build a working KMP SDK that can fetch step count data on both iOS and Android.

**User Stories**: User Story 1 (KMP SDK Step Count Fetching)

**Tasks**:
1. Set up KMP project structure with shared module
2. Define `HealthDataProvider` expect interface in commonMain
3. Define data models (`HealthMetric`, `HealthResult`) in commonMain
4. Implement `HealthDataRepository` business logic in commonMain
5. Write unit tests for `HealthDataRepository` (mocking provider)
6. Implement `HealthDataProvider` actual for iOS (HealthKit wrapper)
7. Implement `HealthDataProvider` actual for Android (Health Connect wrapper)
8. Write platform-specific unit tests (mocking HealthKit/Health Connect)
9. Configure CocoaPods podspec for iOS framework distribution
10. Configure Gradle for Android AAR/module distribution
11. Validate step count accuracy against native HealthKit/Health Connect queries

**Acceptance Criteria**:
- KMP SDK builds successfully for both iOS (Framework) and Android (AAR)
- Unit tests pass with 85%+ coverage
- Step count data matches native queries (within ±1 step)
- Permission errors handled gracefully

---

### Phase 2: Native iOS Demo App (P1)

**Goal**: Integrate KMP SDK into a Swift/SwiftUI app and display health data.

**User Stories**: User Story 2 (Native iOS Demo App Integration)

**Tasks**:
1. Create iOS project with SwiftUI app structure
2. Set up 3-tab navigation (Home, Profile, Health Dashboard)
3. Implement Tab 1 (Home) and Tab 2 (Profile) with native-only code
4. Add CocoaPod dependency for wellness-kmp-sdk
5. Create `HealthKitService` Swift wrapper around KMP SDK
6. Implement `HealthDashboardViewModel` using Combine/async-await
7. Build `HealthDashboardView` UI (loading states, error handling)
8. Handle HealthKit permission flow with user-facing explanations
9. Write unit tests for `HealthDashboardViewModel` (mocking KMP SDK)
10. Manual test on iOS simulator and physical device

**Acceptance Criteria**:
- App builds and runs on iOS 15.0+
- Tab 3 displays real step count data from HealthKit via KMP SDK
- Permission denials show actionable error messages
- Unit tests pass with 80%+ coverage

---

### Phase 3: Native Android Demo App (P1)

**Goal**: Integrate KMP SDK into a Kotlin/Jetpack Compose app and display health data.

**User Stories**: User Story 3 (Native Android Demo App Integration)

**Tasks**:
1. Create Android project with Jetpack Compose app structure
2. Set up 3-tab navigation (Home, Profile, Health Dashboard)
3. Implement Tab 1 (Home) and Tab 2 (Profile) with native-only code
4. Add Gradle module dependency for wellness-kmp-sdk
5. Create `HealthRepository` Kotlin wrapper around KMP SDK
6. Implement `HealthDashboardViewModel` using Kotlin Coroutines + Flow
7. Build `HealthDashboardScreen` Composable (loading states, error handling)
8. Handle Health Connect permission flow (Activity Result API)
9. Write unit tests for `HealthRepository` (mocking KMP SDK)
10. Manual test on Android emulator and physical device

**Acceptance Criteria**:
- App builds and runs on Android 8.0+ (API 26+)
- Tab 3 displays real step count data from Health Connect via KMP SDK
- Missing Health Connect app shows prompt to install
- Unit tests pass with 80%+ coverage

---

### Phase 4: React Native Demo App (P2)

**Goal**: Integrate KMP SDK into a React Native app via native modules.

**User Stories**: User Story 4 (React Native Demo App Integration)

**Tasks**:
1. Create React Native project with TypeScript
2. Set up 3-tab navigation using React Navigation
3. Implement Tab 1 (Home) and Tab 2 (Profile) with React Native code
4. Create iOS native module (Swift) wrapping wellness-kmp-sdk
5. Create Android native module (Kotlin) wrapping wellness-kmp-sdk
6. Write TypeScript interface (`WellnessSDK.ts`) with promise-based API
7. Implement `useHealthData` hook with error handling
8. Build `HealthDashboardScreen` React component (loading/error states)
9. Handle permission flows on both platforms
10. Write Jest tests for `HealthDashboardScreen` (mocking native module)
11. Manual test on both iOS and Android via React Native CLI

**Acceptance Criteria**:
- App runs on both iOS and Android via `npm run ios` / `npm run android`
- Tab 3 displays step count data fetched via native modules → KMP SDK
- Promise rejections handled gracefully in JavaScript
- Jest tests pass with 80%+ coverage

---

### Phase 5: Flutter Demo App (P2)

**Goal**: Integrate KMP SDK into a Flutter app via platform channels.

**User Stories**: User Story 5 (Flutter Demo App Integration)

**Tasks**:
1. Create Flutter project with Dart
2. Set up 3-tab navigation using BottomNavigationBar
3. Implement Tab 1 (Home) and Tab 2 (Profile) with Flutter widgets
4. Create iOS platform plugin (Swift) wrapping wellness-kmp-sdk
5. Create Android platform plugin (Kotlin) wrapping wellness-kmp-sdk
6. Write Dart service (`WellnessSDKService`) using MethodChannel
7. Implement `HealthDashboardScreen` widget with FutureBuilder
8. Handle permission flows on both platforms
9. Write flutter_test tests for `HealthDashboardScreen` (mocking MethodChannel)
10. Manual test on both iOS and Android via Flutter CLI

**Acceptance Criteria**:
- App runs on both iOS and Android via `flutter run`
- Tab 3 displays step count data fetched via platform channels → KMP SDK
- PlatformExceptions handled gracefully in Dart
- flutter_test tests pass with 80%+ coverage

---

### Phase 6: Heart Rate Feature (P3)

**Goal**: Extend SDK to support heart rate data fetching.

**User Stories**: User Story 6 (Heart Rate Data Fetching)

**Tasks**:
1. Add `fetchHeartRate()` method to `HealthDataProvider` interface
2. Implement heart rate fetching in iosMain (HealthKit HKQuantityType.heartRate)
3. Implement heart rate fetching in androidMain (Health Connect HeartRateRecord)
4. Update `HealthDataRepository` to expose heart rate API
5. Write unit tests for heart rate logic
6. Update at least one demo app (e.g., Native iOS) to display heart rate
7. Validate data accuracy against native queries

**Acceptance Criteria**:
- Heart rate data fetched successfully on both platforms
- At least one demo app displays heart rate alongside step count
- Unit tests pass with 85%+ coverage for new code

---

### Phase 7: Documentation & Polish

**Goal**: Finalize documentation, CI/CD, and prepare for demo.

**Tasks**:
1. Write comprehensive README for wellness-kmp-sdk (setup, usage, testing)
2. Write README for each demo app (setup, run, test instructions)
3. Create quickstart guide (`001-kmp-health-sdk-poc.quickstart.md`)
4. Document API contracts (`001-kmp-health-sdk-poc.contracts.md`)
5. Set up GitHub Actions CI for KMP SDK (build, test, coverage)
6. Set up GitHub Actions CI for each demo app
7. Run final lint checks across all projects (ktlint, SwiftLint, ESLint, dart analyze)
8. Record demo video showing all 4 apps working
9. Write final project summary in root README.md

**Acceptance Criteria**:
- All documentation complete and accurate
- CI pipelines green for all projects
- Lint violations = 0
- Demo video showcases successful integration across all 4 frameworks

---

## Testing Strategy

### Unit Testing

**KMP SDK (commonMain)**:
- Mock `HealthDataProvider` using MockK
- Test `HealthDataRepository` business logic
- Test error handling paths (permission denied, no data, etc.)
- Test date range validation

**Platform Implementations (iosMain/androidMain)**:
- Mock HealthKit/Health Connect APIs
- Test actual implementations in isolation
- Test permission request flows
- Test data transformation (native types → SDK models)

**Demo Apps**:
- Mock KMP SDK in ViewModels/Repositories
- Test UI state management (loading, success, error)
- Test permission flow logic
- Test error message formatting

### Integration Testing (Manual)

**Required Device/Simulator Testing**:
- iOS Simulator (iOS 15+) with sample HealthKit data
- Android Emulator (API 26+) with Health Connect installed
- Physical iOS device (optional, recommended)
- Physical Android device (optional, recommended)

**Test Scenarios**:
1. Fresh install → permission request → grant → data loads
2. Fresh install → permission request → deny → error message shown
3. No health data available → empty state or zero count
4. Fetch multiple date ranges → verify correct data returned
5. App backgrounded during fetch → handles gracefully (no crash)

### CI/CD Testing

**GitHub Actions Pipelines**:
1. **KMP SDK CI**: Build iOS framework + Android AAR, run unit tests, check coverage
2. **iOS App CI**: Build via xcodebuild, run XCTests, check coverage
3. **Android App CI**: Build via Gradle, run JUnit tests, check coverage
4. **React Native CI**: Install deps, lint, run Jest tests, check coverage
5. **Flutter CI**: flutter analyze, flutter test, check coverage

**Coverage Enforcement**:
- Fail CI if coverage drops below threshold (85% SDK, 80% apps)
- Generate coverage reports (HTML) as CI artifacts

---

## Deployment & Distribution

### KMP SDK Distribution

**iOS (CocoaPods)**:
- Generate Framework via `./gradlew linkReleaseFrameworkIos`
- Create `WellnessSDK.podspec` with local path reference
- Demo apps install via `pod install`

**Android (Gradle)**:
- Use direct module dependency in demo app's `settings.gradle.kts`
- Alternatively, publish to Maven Local for testing

### Demo Apps Distribution

**For PoC purposes**:
- No App Store / Google Play submission
- Distribute via Xcode (iOS) or Android Studio (Android) for local testing
- React Native / Flutter: Developer builds via CLI

**Future (Out of Scope for PoC)**:
- Publish SDK to Maven Central (Android) and CocoaPods registry (iOS)
- Submit demo apps to TestFlight / Google Play Internal Testing

---

## Risk Mitigation

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| HealthKit/Health Connect APIs change | Low | High | Pin dependency versions; document API versions used |
| CocoaPods integration issues | Medium | High | Use official KMP Cocoa Pods Gradle plugin; test early |
| React Native native module setup complexity | Medium | Medium | Follow official RN docs; test on both platforms early |
| Flutter platform channel communication issues | Medium | Medium | Test platform channels independently before full integration |
| Permission flows differ across OS versions | Medium | Medium | Test on multiple OS versions; handle version-specific edge cases |
| Health Connect not installed on Android | High | Low | Detect and show user-friendly message prompting install |
| Test coverage drops during rapid development | Medium | Medium | Enforce coverage in CI; write tests before implementation (TDD) |
| Project becomes too complex/time-consuming | Low | High | Stick to P1/P2 priorities; defer P3 (heart rate) if needed |

---

## Open Questions & Decisions

### ❓ Question 1: Should KMP SDK expose Compose Multiplatform UI?
**Answer**: No (Deferred)
- **Rationale**: For PoC, headless SDK (data layer only) is simpler and more flexible. Demo apps build their own UI using native frameworks. If productionized, Compose Multiplatform UI could be added as optional module.

### ❓ Question 2: Should we mock HealthKit/Health Connect in unit tests or use real APIs?
**Answer**: Mock in unit tests, real APIs in manual integration tests
- **Rationale**: Unit tests need to run in CI without device access. Mocking ensures fast, reliable tests. Manual integration tests on real devices validate actual API behavior.

### ❓ Question 3: How to handle Health Connect not being installed on Android?
**Answer**: Detect unavailability and show user-friendly prompt
- **Rationale**: Health Connect is a separate app on Android. SDK should detect if it's installed via `HealthConnectClient.isAvailable()` and return `DataNotAvailable` error if missing. Demo app shows message: "Install Health Connect to view your health data."

### ❓ Question 4: Should React Native native modules use Swift/Kotlin or Objective-C/Java?
**Answer**: Swift (iOS) + Kotlin (Android)
- **Rationale**: Modern languages, better feature support, aligns with team expertise and KMP SDK language (Kotlin).

### ❓ Question 5: Should we support iOS < 15.0?
**Answer**: No
- **Rationale**: iOS 15 released Sept 2021, widely adopted. Supporting older iOS versions adds complexity without significant benefit for PoC.

### ✅ Decision 1: Use sealed class `HealthResult<T>` for error handling
- **Rationale**: Type-safe, idiomatic Kotlin. Forces caller to handle all error cases at compile time.

### ✅ Decision 2: Use `Long` (Unix timestamp in ms) for dates in KMP SDK
- **Rationale**: Platform-agnostic, easy to serialize, avoids kotlinx.datetime dependency for PoC. Can upgrade to `Instant` in production.

### ✅ Decision 3: Use TDD workflow where feasible
- **Rationale**: Constitution mandates test-first. Helps prevent bugs and ensures testability from the start.

### ✅ Decision 4: Prioritize P1 stories before P2/P3
- **Rationale**: P1 stories (core SDK + native apps) prove fundamental feasibility. P2 (React Native/Flutter) proves broader compatibility. P3 (heart rate) proves extensibility but is lowest priority.

---

## Success Criteria Summary

### Must Have (P1)
- ✅ KMP SDK builds for iOS + Android
- ✅ Step count data fetches correctly on both platforms
- ✅ Native iOS app integrates SDK and displays data
- ✅ Native Android app integrates SDK and displays data
- ✅ All P1 components have 80-85% test coverage
- ✅ Constitution principles followed (TDD, clean code, expect/actual pattern)

### Should Have (P2)
- ✅ React Native app integrates SDK via native modules
- ✅ Flutter app integrates SDK via platform channels
- ✅ Both P2 apps display step count data correctly

### Nice to Have (P3)
- ✅ Heart rate data fetching implemented
- ✅ At least one app displays heart rate alongside step count

### Project Complete When:
1. All P1 + P2 user stories implemented and tested
2. All CI pipelines green (build + test + coverage)
3. All lint checks pass (zero violations)
4. Documentation complete (READMEs, quickstart, contracts)
5. Demo video recorded showing all 4 apps working
6. Root README updated with project summary

---

## Timeline Estimate (Rough)

| Phase | Duration | Parallel Work Possible? |
|-------|----------|------------------------|
| Phase 0: Research | 1-2 days | No |
| Phase 1: KMP SDK Foundation | 3-5 days | No (prerequisite for all others) |
| Phase 2: Native iOS App | 2-3 days | Yes (parallel with Phase 3) |
| Phase 3: Native Android App | 2-3 days | Yes (parallel with Phase 2) |
| Phase 4: React Native App | 3-4 days | Yes (parallel with Phase 5) |
| Phase 5: Flutter App | 3-4 days | Yes (parallel with Phase 4) |
| Phase 6: Heart Rate Feature | 1-2 days | No (depends on Phase 1) |
| Phase 7: Documentation & Polish | 2-3 days | Partially (can document as you go) |

**Total Estimated Time**: 17-26 days (3-5 weeks)

**Critical Path**: Phase 0 → Phase 1 → (Phase 2 || Phase 3) → (Phase 4 || Phase 5) → Phase 6 → Phase 7

**Assumptions**:
- Solo developer working full-time
- No major blockers (API changes, build system issues, etc.)
- Familiarity with all 4 frameworks (Swift, Kotlin, React Native, Flutter)
- If working part-time or learning frameworks, multiply by 1.5-2x

---

## Next Steps

1. ✅ Review and approve this implementation plan
2. Run `/speckit.tasks` to generate detailed task breakdown
3. Create feature branch `001-kmp-health-sdk-poc`
4. Begin Phase 0: Research & document findings
5. Proceed through phases sequentially (Phase 1 → 2/3 → 4/5 → 6 → 7)
6. Run `/speckit.implement` to execute tasks with AI assistance
