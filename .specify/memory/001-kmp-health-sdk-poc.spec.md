# Feature Specification: KMP Health SDK PoC with Multi-Framework Integration

**Feature Branch**: `001-kmp-health-sdk-poc`  
**Created**: 2026-02-01  
**Status**: Draft  
**Input**: Build a proof-of-concept Kotlin Multiplatform SDK for health data access with cross-framework integration

## User Scenarios & Testing *(mandatory)*

### User Story 1 - KMP SDK Step Count Fetching (Priority: P1)

As a mobile app developer, I need to fetch step count data from a user's health data store (HealthKit on iOS, Health Connect on Android) using a unified KMP SDK API, so I can build cross-platform health applications without writing platform-specific code.

**Why this priority**: This is the foundational capability. Without a working KMP SDK that can access health data on both platforms, nothing else matters. This proves the core technical feasibility.

**Independent Test**: Can be fully tested by creating a minimal KMP module with expect/actual implementations, requesting permissions, and fetching step count for a date range on both iOS and Android simulators/devices. Delivers a working SDK artifact.

**Acceptance Scenarios**:

1. **Given** the KMP SDK is integrated in an iOS app with HealthKit permissions granted, **When** the app calls `fetchStepCount(startDate, endDate)`, **Then** the SDK returns the total step count for that date range from HealthKit
2. **Given** the KMP SDK is integrated in an Android app with Health Connect permissions granted, **When** the app calls `fetchStepCount(startDate, endDate)`, **Then** the SDK returns the total step count for that date range from Health Connect
3. **Given** HealthKit/Health Connect permissions are denied, **When** the app calls `fetchStepCount()`, **Then** the SDK returns a `PermissionDenied` error with actionable user guidance
4. **Given** no health data exists for the requested date range, **When** the app calls `fetchStepCount()`, **Then** the SDK returns a successful result with step count = 0
5. **Given** the device OS version doesn't support health APIs, **When** the app initializes the SDK, **Then** the SDK returns an `UnsupportedPlatform` error

---

### User Story 2 - Native iOS Demo App Integration (Priority: P1)

As an iOS developer, I need to integrate the wellness-kmp-sdk into a native Swift/SwiftUI app and display step count data in the Health Dashboard tab, so I can validate the SDK works seamlessly with native iOS development workflows.

**Why this priority**: Validating native iOS integration is critical. This is the most common use case and proves the SDK doesn't break native development patterns.

**Independent Test**: Can be tested independently by building a standalone iOS app with 3 tabs (Home, Profile, Health Dashboard) where Tab 3 uses the KMP SDK to fetch and display step count. The app should compile, run, and successfully show real HealthKit data.

**Acceptance Scenarios**:

1. **Given** the iOS app is installed on a device/simulator with HealthKit data, **When** the user taps on the Health Dashboard tab, **Then** the app displays the current day's step count fetched via the KMP SDK
2. **Given** the user hasn't granted HealthKit permissions, **When** the user opens the Health Dashboard tab, **Then** the app shows a permission request dialog with clear explanation
3. **Given** the KMP SDK throws an error, **When** the Health Dashboard tab attempts to load data, **Then** the app displays a user-friendly error message (not a crash)
4. **Given** the app is building via Xcode, **When** the KMP SDK is imported as a CocoaPod, **Then** the build succeeds without manual framework configuration

---

### User Story 3 - Native Android Demo App Integration (Priority: P1)

As an Android developer, I need to integrate the wellness-kmp-sdk into a native Kotlin/Jetpack Compose app and display step count data in the Health Dashboard tab, so I can validate the SDK works seamlessly with native Android development workflows.

**Why this priority**: Validating native Android integration is equally critical to iOS. This proves the SDK works on the primary Android development stack.

**Independent Test**: Can be tested independently by building a standalone Android app with 3 tabs (Home, Profile, Health Dashboard) where Tab 3 uses the KMP SDK to fetch and display step count. The app should compile, run, and successfully show real Health Connect data.

**Acceptance Scenarios**:

1. **Given** the Android app is installed on a device/emulator with Health Connect data, **When** the user taps on the Health Dashboard tab, **Then** the app displays the current day's step count fetched via the KMP SDK
2. **Given** the user hasn't granted Health Connect permissions, **When** the user opens the Health Dashboard tab, **Then** the app launches the Health Connect permission flow
3. **Given** Health Connect is not installed on the device, **When** the Health Dashboard tab attempts to load data, **Then** the app displays a message prompting the user to install Health Connect
4. **Given** the app is building via Gradle, **When** the KMP SDK is added as a module dependency, **Then** the build succeeds without additional configuration

---

### User Story 4 - React Native Demo App Integration (Priority: P2)

As a React Native developer, I need to integrate the wellness-kmp-sdk into a TypeScript React Native app and display step count data in the Health Dashboard screen, so I can validate the SDK works with cross-platform JavaScript frameworks.

**Why this priority**: React Native is a major cross-platform framework. Proving KMP SDK integration here validates that the SDK can bridge to JavaScript, which is crucial for broader adoption.

**Independent Test**: Can be tested independently by building a React Native app with 3 tabs where Tab 3 uses native modules to call the KMP SDK and display step count. The app should run on both iOS and Android via React Native CLI.

**Acceptance Scenarios**:

1. **Given** the React Native app is running on iOS/Android with health permissions granted, **When** the user navigates to the Health Dashboard screen, **Then** the app displays step count data fetched via the KMP SDK through native modules
2. **Given** permissions are not granted, **When** the Health Dashboard screen loads, **Then** the app shows a native permission request (iOS) or launches Health Connect permissions (Android)
3. **Given** the native bridge fails, **When** the Health Dashboard attempts to fetch data, **Then** the JavaScript layer receives a rejected promise with error details
4. **Given** the developer runs `npm run android` or `npm run ios`, **When** the KMP SDK is linked via native modules, **Then** the app builds successfully on both platforms

---

### User Story 5 - Flutter Demo App Integration (Priority: P2)

As a Flutter developer, I need to integrate the wellness-kmp-sdk into a Dart Flutter app and display step count data in the Health Dashboard screen, so I can validate the SDK works with Flutter's platform channels.

**Why this priority**: Flutter is another major cross-platform framework with a different architecture (Dart + platform channels). Proving integration here demonstrates the SDK's versatility across diverse frameworks.

**Independent Test**: Can be tested independently by building a Flutter app with 3 tabs where Tab 3 uses platform channels to call the KMP SDK and display step count. The app should run on both iOS and Android via Flutter CLI.

**Acceptance Scenarios**:

1. **Given** the Flutter app is running on iOS/Android with health permissions granted, **When** the user navigates to the Health Dashboard screen, **Then** the app displays step count data fetched via the KMP SDK through platform channels
2. **Given** permissions are not granted, **When** the Health Dashboard screen loads, **Then** the app shows a native permission request dialog
3. **Given** the platform channel call fails, **When** the Health Dashboard attempts to fetch data, **Then** the Dart layer receives a PlatformException with error details
4. **Given** the developer runs `flutter run`, **When** the KMP SDK is embedded via platform channels, **Then** the app builds successfully on both iOS and Android

---

### User Story 6 - Heart Rate Data Fetching (Priority: P3)

As a mobile app developer, I need to fetch heart rate data from HealthKit/Health Connect using the KMP SDK, so I can demonstrate that the SDK architecture supports multiple health data types beyond just step count.

**Why this priority**: This validates that the SDK architecture is extensible and not limited to a single data type. However, it's lower priority than proving the core integration works across all 4 frameworks.

**Independent Test**: Can be tested by adding a `fetchHeartRate()` method to the KMP SDK and calling it from any of the demo apps. The test is successful if heart rate data is correctly retrieved and displayed.

**Acceptance Scenarios**:

1. **Given** the KMP SDK is integrated in any demo app with permissions granted, **When** the app calls `fetchHeartRate(startDate, endDate)`, **Then** the SDK returns heart rate measurements for that date range
2. **Given** no heart rate data exists, **When** the app calls `fetchHeartRate()`, **Then** the SDK returns an empty result list
3. **Given** the device doesn't have a heart rate sensor or data source, **When** the app calls `fetchHeartRate()`, **Then** the SDK returns a `DataNotAvailable` error

---

## Goals *(mandatory)*

### Primary Goal
Prove that a Kotlin Multiplatform SDK can successfully provide unified health data access (HealthKit on iOS, Health Connect on Android) and integrate seamlessly across four different mobile frameworks: Native iOS (Swift/SwiftUI), Native Android (Kotlin/Jetpack Compose), React Native (TypeScript), and Flutter (Dart).

### Success Criteria
1. ✅ KMP SDK compiles for both iOS (Framework/CocoaPod) and Android (AAR)
2. ✅ SDK can fetch step count data from HealthKit on iOS
3. ✅ SDK can fetch step count data from Health Connect on Android
4. ✅ Native iOS demo app successfully integrates and displays data from SDK
5. ✅ Native Android demo app successfully integrates and displays data from SDK
6. ✅ React Native demo app successfully integrates and displays data from SDK via native bridge
7. ✅ Flutter demo app successfully integrates and displays data from SDK via platform channels
8. ✅ All demo apps have 80%+ test coverage
9. ✅ SDK follows expect/actual pattern for platform abstraction
10. ✅ Error handling works correctly across all platform boundaries

### Non-Goals (Out of Scope)
- Production-ready SDK (this is a PoC only)
- Publishing to Maven Central / CocoaPods registry
- Writing health data (read-only PoC)
- Complex UI/UX design (basic functional UI is sufficient)
- Supporting health data types beyond step count and heart rate
- Supporting platforms other than iOS/Android (no watchOS, web, desktop)
- Authentication/authorization beyond platform health permissions
- Backend integration or cloud sync
- Offline-first architecture or data caching (except basic in-memory)

---

## Requirements & Constraints *(mandatory)*

### Functional Requirements

**KMP SDK (wellness-kmp-sdk)**:
- FR1: MUST implement `HealthDataProvider` interface in `commonMain` with `expect` declarations
- FR2: MUST implement `actual` HealthKit wrapper in `iosMain` using native iOS APIs
- FR3: MUST implement `actual` Health Connect wrapper in `androidMain` using androidx.health.connect
- FR4: MUST expose `fetchStepCount(startDate: Long, endDate: Long): Result<Long>` method
- FR5: MUST expose `fetchHeartRate(startDate: Long, endDate: Long): Result<List<HeartRateMeasurement>>` method
- FR6: MUST handle permission requests for both platforms
- FR7: MUST return errors using sealed class or Result type (no exceptions thrown to caller)
- FR8: MUST use coroutines for all asynchronous operations

**Demo Apps (All 4)**:
- FR9: MUST implement 3-tab bottom navigation structure (Home, Profile, Health Dashboard)
- FR10: Tab 1 (Home) and Tab 2 (Profile) MUST use only native code (no KMP dependency)
- FR11: Tab 3 (Health Dashboard) MUST integrate the KMP SDK and display step count
- FR12: MUST handle permission flows according to platform conventions
- FR13: MUST display loading states while fetching data
- FR14: MUST display error messages when data fetch fails
- FR15: MUST have at least 80% unit test coverage

**React Native Bridge**:
- FR16: MUST implement native modules for iOS and Android that wrap the KMP SDK
- FR17: MUST expose JavaScript API that matches TypeScript types
- FR18: MUST handle promise rejection for errors

**Flutter Platform Channels**:
- FR19: MUST implement method channels for iOS and Android that wrap the KMP SDK
- FR20: MUST handle MethodChannel exceptions in Dart
- FR21: MUST provide typed Dart API

### Technical Requirements

**Tech Stack**:
- TR1: Kotlin 1.9.22+ for KMP SDK
- TR2: Gradle 8.5+ for build system
- TR3: Compose Multiplatform 1.6.0+ (optional, for shared UI if needed)
- TR4: Swift 5.9+ and SwiftUI for iOS demo app
- TR5: Kotlin 1.9.22+ and Jetpack Compose for Android demo app
- TR6: React Native 0.73+, TypeScript 5.3+ for RN demo app
- TR7: Flutter 3.19+, Dart 3.3+ for Flutter demo app

**Health APIs**:
- TR8: iOS MUST use HealthKit framework (iOS 15.0+ deployment target)
- TR9: Android MUST use Health Connect (androidx.health.connect:connect-client:1.1.0-alpha07+)
- TR10: Android MUST support minSdk 26, targetSdk 34+

**Architecture**:
- TR11: MUST use Repository pattern for data access
- TR12: MUST use dependency injection (constructor injection minimum)
- TR13: MUST separate platform-specific code from business logic
- TR14: MUST use Clean Architecture principles (domain, data, presentation layers)

**Testing**:
- TR15: MUST use kotlin-test + MockK for KMP SDK tests
- TR16: MUST use XCTest for iOS app tests
- TR17: MUST use JUnit + MockK for Android app tests
- TR18: MUST use Jest for React Native app tests
- TR19: MUST use flutter_test + Mockito for Flutter app tests
- TR20: MUST mock platform-specific APIs in unit tests
- TR21: MUST achieve 80%+ test coverage (enforced by CI)

### Non-Functional Requirements

**Performance**:
- NR1: Health data fetching MUST complete within 5 seconds under normal conditions
- NR2: UI MUST remain responsive during data loading (use background threads)
- NR3: App launch time MUST NOT be significantly impacted by KMP SDK initialization (<500ms overhead)

**Reliability**:
- NR4: SDK MUST handle network failures gracefully (though health APIs are mostly local)
- NR5: SDK MUST handle missing permissions without crashing
- NR6: SDK MUST handle missing Health Connect app on Android gracefully

**Maintainability**:
- NR7: Code MUST follow language-specific style guides (enforced by linters)
- NR8: All public APIs MUST have KDoc/DocC/TSDoc/DartDoc documentation
- NR9: Complex logic MUST have inline comments explaining rationale

**Compatibility**:
- NR10: iOS app MUST run on iOS 15.0+ (iPhone 8 and later)
- NR11: Android app MUST run on Android 8.0+ (API 26+)
- NR12: React Native app MUST run on both iOS and Android via RN CLI
- NR13: Flutter app MUST run on both iOS and Android via Flutter CLI

### Constitutional Constraints

Per SpecKitApp Constitution v1.0.0:

- CC1: Cross-Platform Compatibility First (NON-NEGOTIABLE) - SDK MUST work identically on iOS/Android
- CC2: Test-First Development (NON-NEGOTIABLE) - 80-85% coverage required, TDD process
- CC3: Clean Code & SOLID Principles - Self-documenting code, DI, style guides
- CC4: expect/actual Pattern - Platform abstraction via Kotlin Multiplatform
- CC5: Comprehensive Error Handling - Proper logging, user-friendly messages
- CC6: Code Quality Enforcement - Linting via ktlint, SwiftLint, ESLint, dart analyze
- CC7: Documentation as Code - All public APIs documented

---

## System Context *(optional - recommended)*

### System Overview

```
┌─────────────────────────────────────────────────────────────┐
│                   Demo Applications Layer                    │
├───────────────┬───────────────┬──────────────┬──────────────┤
│  iOS App      │ Android App   │  React Native│  Flutter App │
│  (Swift/      │  (Kotlin/     │  App         │  (Dart)      │
│   SwiftUI)    │   Compose)    │  (TypeScript)│              │
│               │               │              │              │
│  Tab 1: Home  │  Tab 1: Home  │  Tab 1: Home │  Tab 1: Home │
│  Tab 2: Profile│ Tab 2: Profile│ Tab 2: Settings│Tab 2: Profile│
│  Tab 3: Health│  Tab 3: Health│  Tab 3: Health│Tab 3: Health │
│      Dashboard│       Dashboard│      Dashboard│     Dashboard│
│      (KMP SDK)│       (KMP SDK)│   (Native    │  (Platform   │
│               │                │    Modules)  │   Channels)  │
└───────┬───────┴───────┬───────┴──────┬───────┴──────┬───────┘
        │               │              │              │
        └───────────────┴──────────────┴──────────────┘
                         │
                         ▼
        ┌────────────────────────────────────────────┐
        │      wellness-kmp-sdk (Kotlin MP)          │
        ├────────────────────────────────────────────┤
        │  commonMain/                               │
        │    - HealthDataProvider (expect)           │
        │    - HealthDataRepository                  │
        │    - Models (HealthMetric, HeartRate)      │
        │    - Result types (sealed classes)         │
        │                                            │
        │  iosMain/                 androidMain/     │
        │    - HealthKitWrapper →   - HealthConnect  │
        │      (actual)               Wrapper (actual)│
        └──────────┬────────────────────┬────────────┘
                   │                    │
                   ▼                    ▼
        ┌──────────────────┐  ┌─────────────────────┐
        │   iOS HealthKit  │  │  Android Health     │
        │   (Native API)   │  │  Connect (androidx) │
        └──────────────────┘  └─────────────────────┘
```

### External Dependencies

- **iOS HealthKit**: Apple's native health data framework (read-only access)
- **Android Health Connect**: Google's unified health platform (androidx library)
- **CocoaPods**: For iOS framework distribution
- **Maven Local**: For Android AAR distribution (or direct Gradle module)
- **React Native Bridge**: For JavaScript ↔ Native communication
- **Flutter Platform Channels**: For Dart ↔ Native communication

### Data Flow

1. User opens Health Dashboard tab in any demo app
2. App UI layer calls KMP SDK's `fetchStepCount()`
3. KMP SDK's `commonMain` routes to platform-specific `actual` implementation
4. iOS: HealthKit wrapper queries HealthKit via native APIs
5. Android: Health Connect wrapper queries Health Connect via androidx APIs
6. Platform wrapper returns `Result<Long>` to `commonMain`
7. `commonMain` returns result to app layer
8. App UI displays step count or error message

---

## Open Questions *(optional)*

1. **Q: Should the KMP SDK expose UI components via Compose Multiplatform, or remain headless?**
   - Current assumption: Headless SDK (data layer only). Demo apps build their own UI.
   - Rationale: Easier to integrate with diverse frameworks if SDK has no UI opinions.

2. **Q: Should we support iOS < 15.0?**
   - Current assumption: iOS 15.0+ only (released Sept 2021, widely adopted)
   - Rationale: HealthKit APIs are more stable in iOS 15+, reduces testing burden.

3. **Q: Should the React Native native modules be written in Swift/Kotlin or Objective-C/Java?**
   - Current assumption: Swift (iOS) + Kotlin (Android) to match modern practices.
   - Rationale: Better language feature support, matches team expertise.

4. **Q: Should we mock HealthKit/Health Connect in unit tests or use real APIs in integration tests?**
   - Current assumption: Mock in unit tests (fast, reliable), optional integration tests on real devices.
   - Rationale: Unit tests run in CI without device access; integration tests are manual for now.

5. **Q: Should the KMP SDK be published to Maven Central / CocoaPods, or remain local?**
   - Current assumption: Local-only for PoC (Maven Local for Android, local CocoaPod for iOS).
   - Rationale: Publishing is out of scope for PoC; can be added later if productionized.

---

## Success Metrics *(optional)*

### Technical Metrics
- **Build Success Rate**: 100% success rate for all 5 projects (KMP SDK + 4 demo apps)
- **Test Coverage**: 80%+ for demo apps, 85%+ for KMP SDK
- **Test Pass Rate**: 100% of tests passing in CI/CD
- **Lint Pass Rate**: 0 lint violations in any project

### Functional Metrics
- **Data Accuracy**: Step count from KMP SDK matches native HealthKit/Health Connect queries (within ±1 step due to rounding)
- **Permission Success Rate**: 100% of permission requests handled gracefully (no crashes)
- **Error Handling Coverage**: All error paths tested (permissions denied, no data, API failures)

### Integration Metrics
- **Framework Compatibility**: 4/4 frameworks successfully integrate and run the KMP SDK
- **Build Time**: KMP SDK build completes in < 2 minutes
- **Demo App Build Time**: Each demo app builds in < 3 minutes

### Developer Experience Metrics
- **Setup Time**: New developer can clone repo and run all apps in < 30 minutes
- **Documentation Completeness**: Every public API has documentation, every module has README
- **Onboarding Friction**: Zero manual binary downloads required (all dependencies via package managers)

---

## References *(optional)*

### Documentation
- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [HealthKit Documentation](https://developer.apple.com/documentation/healthkit)
- [Health Connect Documentation](https://developer.android.com/health-and-fitness/guides/health-connect)
- [React Native Native Modules](https://reactnative.dev/docs/native-modules-ios)
- [Flutter Platform Channels](https://docs.flutter.dev/platform-integration/platform-channels)

### Related Work
- [Touchlab KaMP Kit](https://github.com/touchlab/KaMPKit) - KMP sample architecture
- [JetBrains KMP Samples](https://github.com/Kotlin/kmm-samples)
- [HealthKitReporter](https://github.com/VictorKachalov/HealthKitReporter) - Swift HealthKit wrapper

### Project Context
- Constitution: `.specify/memory/constitution.md` (v1.0.0)
- Detailed Instructions: `.github/copilot/default.instructions.md`
