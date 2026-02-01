# Tasks: KMP Health SDK PoC with Multi-Framework Integration

**Input**: Design documents from `.specify/memory/001-kmp-health-sdk-poc.*`
**Prerequisites**: spec.md ✅, plan.md ✅

**Feature Branch**: `001-kmp-health-sdk-poc`
**Created**: 2026-02-01

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1-US6)
- All tasks include exact file paths

---

## Phase 0: Research & Validation ✅ (COMPLETED)

**Purpose**: Technical feasibility validation

- [x] T001 Research Kotlin Multiplatform expect/actual mechanism
- [x] T002 [P] Research HealthKit APIs (HKQuantityType, HKStatisticsQuery)
- [x] T003 [P] Research Health Connect APIs (ReadRecordsRequest, StepsRecord)
- [x] T004 [P] Research CocoaPods integration for KMP frameworks
- [x] T005 [P] Research React Native native modules (Swift + Kotlin)
- [x] T006 [P] Research Flutter platform channels (MethodChannel)
- [x] T007 Document findings in `.specify/memory/001-kmp-health-sdk-poc.research.md`

**Status**: Phase 0 considered complete (research findings embedded in plan.md)

---

## Phase 1: KMP SDK Foundation (User Story 1 - Priority P1) 🎯 MVP

**Goal**: Build working KMP SDK that can fetch step count data on iOS & Android

**Independent Test**: Fetch step count via SDK on both iOS/Android simulators, verify data accuracy

### Setup & Project Structure

- [ ] T008 Create `wellness-kmp-sdk/` directory at repository root
- [ ] T009 Initialize KMP project with Gradle (8.5+) in `wellness-kmp-sdk/build.gradle.kts`
- [ ] T010 Create `shared/` module with multiplatform targets (iosArm64, iosSimulatorArm64, android)
- [ ] T011 Configure `wellness-kmp-sdk/shared/build.gradle.kts` with kotlin-multiplatform plugin
- [ ] T012 Add kotlinx dependencies: coroutines:1.7.3, serialization:1.6.0, datetime:0.5.0
- [ ] T013 Add Android-specific dependency: androidx.health.connect:connect-client:1.1.0-alpha07
- [ ] T014 Configure Kotlin compiler options (JVM target 17, iOS compiler flags)
- [ ] T015 Create `wellness-kmp-sdk/gradle.properties` with Android SDK versions (minSdk=26, targetSdk=34)

### Core Data Models (commonMain)

- [ ] T016 [P] [US1] Create `HealthMetric.kt` data class in `shared/src/commonMain/kotlin/com/speckit/wellness/models/`
- [ ] T017 [P] [US1] Create `HealthResult.kt` sealed class in `shared/src/commonMain/kotlin/com/speckit/wellness/models/`
- [ ] T018 [P] [US1] Add `PermissionDenied`, `DataNotAvailable`, `UnsupportedPlatform`, `UnknownError` as sealed subclasses

### Expect Interface (commonMain)

- [ ] T019 [US1] Create `HealthDataProvider.kt` expect class in `shared/src/commonMain/kotlin/com/speckit/wellness/`
- [ ] T020 [US1] Define `suspend fun requestAuthorization(): HealthResult<Boolean>`
- [ ] T021 [US1] Define `suspend fun fetchStepCount(startDate: Long, endDate: Long): HealthResult<HealthMetric>`

### Repository (commonMain)

- [ ] T022 [US1] Create `HealthDataRepository.kt` in `shared/src/commonMain/kotlin/com/speckit/wellness/`
- [ ] T023 [US1] Implement constructor with `HealthDataProvider` dependency
- [ ] T024 [US1] Implement `getStepCount()` that delegates to provider
- [ ] T025 [US1] Implement `requestPermissions()` that delegates to provider
- [ ] T026 [US1] Add input validation (startDate < endDate, non-negative timestamps)

### Unit Tests (commonTest) - WRITE TESTS FIRST! ⚠️

- [ ] T027 [P] [US1] Setup MockK in `shared/src/commonTest/kotlin/` dependencies
- [ ] T028 [US1] Create `HealthDataRepositoryTest.kt` in `shared/src/commonTest/kotlin/com/speckit/wellness/`
- [ ] T029 [US1] Test `getStepCount()` success case (mock provider returning success)
- [ ] T030 [US1] Test `getStepCount()` permission denied case
- [ ] T031 [US1] Test `getStepCount()` data not available case
- [ ] T032 [US1] Test `getStepCount()` input validation (startDate > endDate should fail)
- [ ] T033 [US1] Test `requestPermissions()` success/failure cases
- [ ] T034 [US1] Run tests: `./gradlew shared:cleanTestDebugUnitTest shared:testDebugUnitTest` - MUST FAIL until implementation

### iOS Implementation (iosMain)

- [ ] T035 [US1] Create `HealthDataProvider.kt` actual in `shared/src/iosMain/kotlin/com/speckit/wellness/`
- [ ] T036 [US1] Import HealthKit framework (`import platform.HealthKit.*`)
- [ ] T037 [US1] Implement `requestAuthorization()` using `HKHealthStore.requestAuthorization()`
- [ ] T038 [US1] Request read permission for `HKQuantityTypeIdentifierStepCount`
- [ ] T039 [US1] Implement `fetchStepCount()` using `HKStatisticsQuery` with `cumulativeSum` option
- [ ] T040 [US1] Convert HealthKit dates (NSDate) to Long (Unix timestamp ms)
- [ ] T041 [US1] Convert HealthKit step count (HKQuantity) to Long
- [ ] T042 [US1] Handle HealthKit errors: authorization denied → PermissionDenied, no data → DataNotAvailable
- [ ] T043 [US1] Use `suspendCoroutine` or `suspendCancellableCoroutine` to bridge HealthKit callbacks to coroutines

### iOS Unit Tests (iosTest) - OPTIONAL

- [ ] T044 [P] [US1] Create `HealthKitWrapperTest.kt` in `shared/src/iosTest/kotlin/com/speckit/wellness/`
- [ ] T045 [US1] Test HealthKit date conversion utilities
- [ ] T046 [US1] Test HealthKit error mapping to HealthResult types

### Android Implementation (androidMain)

- [ ] T047 [US1] Create `HealthDataProvider.kt` actual in `shared/src/androidMain/kotlin/com/speckit/wellness/`
- [ ] T048 [US1] Import Health Connect APIs (`import androidx.health.connect.client.*`)
- [ ] T049 [US1] Add `Context` parameter to HealthDataProvider constructor
- [ ] T050 [US1] Initialize `HealthConnectClient.getOrCreate(context)`
- [ ] T051 [US1] Implement `requestAuthorization()` checking `permissionController.getGrantedPermissions()`
- [ ] T052 [US1] Request `HealthPermission.getReadPermission(StepsRecord::class)`
- [ ] T053 [US1] Implement `fetchStepCount()` using `ReadRecordsRequest<StepsRecord>`
- [ ] T054 [US1] Use `TimeRangeFilter.between(startTime, endTime)` with Instant conversion
- [ ] T055 [US1] Sum `StepsRecord.count` values from response
- [ ] T056 [US1] Handle Health Connect not installed: check `HealthConnectClient.isAvailable()` → DataNotAvailable
- [ ] T057 [US1] Handle permission denied → PermissionDenied
- [ ] T058 [US1] Wrap calls in try-catch, map exceptions to UnknownError

### Android Unit Tests (androidTest) - OPTIONAL

- [ ] T059 [P] [US1] Create `HealthConnectWrapperTest.kt` in `shared/src/androidTest/kotlin/com/speckit/wellness/`
- [ ] T060 [US1] Test Health Connect availability check
- [ ] T061 [US1] Test timestamp conversion (Long → Instant)

### iOS Framework Distribution (CocoaPods)

- [ ] T062 [US1] Generate iOS framework: `./gradlew shared:linkReleaseFrameworkIosSimulatorArm64`
- [ ] T063 [US1] Create `WellnessSDK.podspec` in `wellness-kmp-sdk/`
- [ ] T064 [US1] Configure podspec with local path to generated framework
- [ ] T065 [US1] Set iOS deployment target to 15.0 in podspec

### Android AAR Distribution

- [ ] T066 [US1] Configure Gradle to generate AAR: `./gradlew shared:assembleRelease`
- [ ] T067 [US1] Document how to depend on `wellness-kmp-sdk:shared` via `includeBuild()` or Maven Local

### Validation & Coverage

- [ ] T068 [US1] Run all unit tests: `./gradlew shared:allTests`
- [ ] T069 [US1] Generate coverage report: `./gradlew shared:koverHtmlReport`
- [ ] T070 [US1] Verify 85%+ coverage for commonMain code
- [ ] T071 [US1] Manual test on iOS simulator: fetch step count, verify against HealthKit app
- [ ] T072 [US1] Manual test on Android emulator: fetch step count, verify against Health Connect app
- [ ] T073 [US1] Document accuracy: step count from SDK should match native APIs (±1 step tolerance)

### Linting & Documentation

- [ ] T074 [P] [US1] Setup ktlint: `./gradlew shared:ktlintCheck`
- [ ] T075 [P] [US1] Setup Detekt: `./gradlew shared:detekt`
- [ ] T076 [US1] Add KDoc comments to all public APIs (HealthDataProvider, HealthDataRepository, models)
- [ ] T077 [US1] Create `wellness-kmp-sdk/README.md` with setup, usage, testing instructions
- [ ] T078 [US1] Fix any lint violations

**Checkpoint**: KMP SDK is functional and tested. Ready for demo app integration.

---

## Phase 2: Native iOS Demo App (User Story 2 - Priority P1) 🎯

**Goal**: Integrate KMP SDK into Swift/SwiftUI app, display step count in Health Dashboard tab

**Independent Test**: Run iOS app, navigate to Tab 3, see real step count from HealthKit via KMP SDK

### Project Setup

- [ ] T079 [US2] Create `ios-app/` directory at repository root
- [ ] T080 [US2] Create Xcode project: `SpecKitIOS.xcodeproj` in `ios-app/`
- [ ] T081 [US2] Set iOS deployment target to 15.0 in Xcode project settings
- [ ] T082 [US2] Create `ios-app/Podfile` with reference to `wellness-kmp-sdk` podspec
- [ ] T083 [US2] Run `pod install` in `ios-app/` to integrate KMP SDK
- [ ] T084 [US2] Add HealthKit capability in Xcode (Signing & Capabilities tab)
- [ ] T085 [US2] Add `NSHealthShareUsageDescription` to `Info.plist` with permission explanation

### App Structure & Navigation

- [ ] T086 [P] [US2] Create `SpecKitIOSApp.swift` in `ios-app/SpecKitIOS/App/` (SwiftUI App entry point)
- [ ] T087 [US2] Create `ContentView.swift` in `ios-app/SpecKitIOS/Views/` with TabView (3 tabs)
- [ ] T088 [P] [US2] Create `HomeView.swift` in `ios-app/SpecKitIOS/Views/` (Tab 1 - native only, no KMP)
- [ ] T089 [P] [US2] Create `ProfileView.swift` in `ios-app/SpecKitIOS/Views/` (Tab 2 - native only, no KMP)
- [ ] T090 [US2] Create `HealthDashboardView.swift` in `ios-app/SpecKitIOS/Views/` (Tab 3 - integrates KMP SDK)

### Service Layer (Swift Wrapper for KMP SDK)

- [ ] T091 [US2] Create `HealthKitService.swift` in `ios-app/SpecKitIOS/Services/`
- [ ] T092 [US2] Import KMP SDK framework: `import shared`
- [ ] T093 [US2] Create `HealthDataRepository` instance (from KMP SDK)
- [ ] T094 [US2] Wrap `requestPermissions()` in Swift async function
- [ ] T095 [US2] Wrap `getStepCount()` in Swift async function, convert Long timestamps from Date
- [ ] T096 [US2] Map `HealthResult` sealed class to Swift Result type
- [ ] T097 [US2] Handle errors: map PermissionDenied/DataNotAvailable/UnknownError to user-friendly messages

### ViewModel

- [ ] T098 [US2] Create `HealthDashboardViewModel.swift` in `ios-app/SpecKitIOS/ViewModels/`
- [ ] T099 [US2] Conform to `ObservableObject` protocol
- [ ] T100 [US2] Add `@Published var stepCount: Int?`
- [ ] T101 [US2] Add `@Published var isLoading: Bool = false`
- [ ] T102 [US2] Add `@Published var errorMessage: String?`
- [ ] T103 [US2] Implement `fetchStepCount()` async function calling HealthKitService
- [ ] T104 [US2] Implement `requestPermissions()` async function
- [ ] T105 [US2] Update loading/error states appropriately

### UI (SwiftUI View)

- [ ] T106 [US2] Implement `HealthDashboardView` body with step count display
- [ ] T107 [US2] Show ProgressView when `isLoading == true`
- [ ] T108 [US2] Show error message if `errorMessage != nil`
- [ ] T109 [US2] Show step count if `stepCount != nil`
- [ ] T110 [US2] Add button to request permissions if denied
- [ ] T111 [US2] Use `.task {}` modifier to fetch data on appear

### Unit Tests - WRITE FIRST! ⚠️

- [ ] T112 [P] [US2] Create `SpecKitIOSTests/` test target in Xcode
- [ ] T113 [US2] Create `HealthDashboardViewModelTests.swift` in `SpecKitIOSTests/`
- [ ] T114 [US2] Mock HealthKitService using protocol abstraction
- [ ] T115 [US2] Test `fetchStepCount()` success updates `stepCount` published property
- [ ] T116 [US2] Test `fetchStepCount()` failure updates `errorMessage` published property
- [ ] T117 [US2] Test `fetchStepCount()` sets `isLoading` correctly
- [ ] T118 [US2] Test permission request flow
- [ ] T119 [US2] Run tests: Cmd+U in Xcode - MUST FAIL until implementation

### Validation & Coverage

- [ ] T120 [US2] Build iOS app: `xcodebuild -workspace SpecKitIOS.xcworkspace -scheme SpecKitIOS -destination 'platform=iOS Simulator,name=iPhone 15' build`
- [ ] T121 [US2] Run unit tests: `xcodebuild test -workspace ... -scheme SpecKitIOS -destination ...`
- [ ] T122 [US2] Check test coverage in Xcode: verify 80%+ coverage
- [ ] T123 [US2] Manual test: Run app on simulator, grant HealthKit permission, see step count in Tab 3
- [ ] T124 [US2] Manual test: Deny permission, see error message with actionable guidance
- [ ] T125 [US2] Manual test: Verify Tabs 1 & 2 work without KMP SDK dependency

### Linting & Documentation

- [ ] T126 [P] [US2] Setup SwiftLint: create `.swiftlint.yml` in `ios-app/`
- [ ] T127 [US2] Run SwiftLint: `swiftlint lint` in `ios-app/`
- [ ] T128 [US2] Add DocC comments to HealthKitService and ViewModel public methods
- [ ] T129 [US2] Create `ios-app/README.md` with setup (CocoaPods), run, test instructions
- [ ] T130 [US2] Fix any SwiftLint violations

**Checkpoint**: Native iOS app successfully integrates KMP SDK and displays health data.

---

## Phase 3: Native Android Demo App (User Story 3 - Priority P1) 🎯

**Goal**: Integrate KMP SDK into Kotlin/Jetpack Compose app, display step count in Health Dashboard tab

**Independent Test**: Run Android app, navigate to Tab 3, see real step count from Health Connect via KMP SDK

### Project Setup

- [X] T131 [US3] Create `android-app/` directory at repository root
- [X] T132 [US3] Initialize Android project with Gradle in `android-app/build.gradle.kts`
- [X] T133 [US3] Create `android-app/settings.gradle.kts` including `wellness-kmp-sdk:shared` module
- [X] T134 [US3] Configure `android-app/app/build.gradle.kts` with Jetpack Compose dependencies
- [X] T135 [US3] Set `minSdk = 26`, `targetSdk = 34`, `compileSdk = 34`
- [X] T136 [US3] Add dependency: `implementation(project(":wellness-kmp-sdk:shared"))`
- [X] T137 [US3] Add dependency: `androidx.health.connect:connect-client:1.1.0-alpha07`
- [X] T138 [US3] Add Health Connect permissions in `AndroidManifest.xml`: `android.permission.health.READ_STEPS`

### App Structure & Navigation

- [X] T139 [P] [US3] Create `MainActivity.kt` in `android-app/app/src/main/kotlin/com/speckit/android/`
- [X] T140 [US3] Setup Jetpack Compose with Scaffold + BottomNavigation (3 tabs)
- [X] T141 [P] [US3] Create `HomeScreen.kt` in `android-app/app/src/main/kotlin/com/speckit/android/ui/` (Tab 1 - native only)
- [X] T142 [P] [US3] Create `ProfileScreen.kt` in `android-app/app/src/main/kotlin/com/speckit/android/ui/` (Tab 2 - native only)
- [X] T143 [US3] Create `HealthDashboardScreen.kt` in `android-app/app/src/main/kotlin/com/speckit/android/ui/` (Tab 3 - KMP SDK)

### Repository Layer (Kotlin Wrapper for KMP SDK)

- [X] T144 [US3] Create `HealthRepository.kt` in `android-app/app/src/main/kotlin/com/speckit/android/repository/`
- [X] T145 [US3] Accept `Context` as constructor parameter
- [X] T146 [US3] Create `HealthDataProvider` instance from KMP SDK (pass context)
- [X] T147 [US3] Create `HealthDataRepository` instance from KMP SDK
- [X] T148 [US3] Wrap `requestPermissions()` in suspend function
- [X] T149 [US3] Wrap `getStepCount()` in suspend function, convert Long timestamps from LocalDate
- [X] T150 [US3] Handle Health Connect not installed: check availability, return user-friendly error
- [X] T151 [US3] Map `HealthResult` sealed class to domain-specific result types

### ViewModel

- [X] T152 [US3] Create `HealthDashboardViewModel.kt` in `android-app/app/src/main/kotlin/com/speckit/android/viewmodel/`
- [X] T153 [US3] Extend `ViewModel` from androidx.lifecycle
- [X] T154 [US3] Add `StateFlow<Int?> stepCount`
- [X] T155 [US3] Add `StateFlow<Boolean> isLoading`
- [X] T156 [US3] Add `StateFlow<String?> errorMessage`
- [X] T157 [US3] Implement `fetchStepCount()` launching coroutine in `viewModelScope`
- [X] T158 [US3] Implement `requestPermissions()` using Activity Result API
- [X] T159 [US3] Update StateFlows appropriately during loading/success/error

### UI (Jetpack Compose)

- [X] T160 [US3] Implement `HealthDashboardScreen` Composable displaying step count
- [X] T161 [US3] Show CircularProgressIndicator when `isLoading == true`
- [X] T162 [US3] Show error message if `errorMessage != null`
- [X] T163 [US3] Show step count if `stepCount != null`
- [X] T164 [US3] Add Button to request permissions if denied
- [X] T165 [US3] Use `LaunchedEffect` to fetch data on composition
- [X] T166 [US3] Observe ViewModel StateFlows with `collectAsState()`

### Unit Tests - WRITE FIRST! ⚠️

- [X] T167 [P] [US3] Setup test dependencies: JUnit, MockK, kotlinx-coroutines-test in `app/build.gradle.kts`
- [X] T168 [US3] Create `HealthRepositoryTest.kt` in `android-app/app/src/test/kotlin/com/speckit/android/repository/`
- [X] T169 [US3] Mock `HealthDataProvider` from KMP SDK
- [X] T170 [US3] Test `fetchStepCount()` success returns correct value
- [X] T171 [US3] Test `fetchStepCount()` permission denied returns error
- [X] T172 [US3] Test `fetchStepCount()` Health Connect not installed returns error
- [X] T173 [US3] Test date conversion utilities (LocalDate → Long timestamp)
- [X] T174 [US3] Run tests: `./gradlew android-app:app:testDebugUnitTest` - MUST FAIL until implementation

### Validation & Coverage

- [X] T175 [US3] Build Android app: `./gradlew android-app:app:assembleDebug`
- [X] T176 [US3] Run unit tests: `./gradlew android-app:app:testDebugUnitTest`
- [ ] T177 [US3] Generate coverage report: `./gradlew android-app:app:jacocoTestReport`
- [ ] T178 [US3] Verify 80%+ coverage
- [ ] T179 [US3] Manual test: Run app on emulator, install Health Connect, grant permission, see step count
- [ ] T180 [US3] Manual test: Test with Health Connect not installed, see prompt message
- [ ] T181 [US3] Manual test: Verify Tabs 1 & 2 work without KMP SDK dependency

### Linting & Documentation

- [ ] T182 [P] [US3] Setup ktlint: `./gradlew android-app:ktlintCheck`
- [ ] T183 [P] [US3] Setup Detekt: `./gradlew android-app:detekt`
- [ ] T184 [US3] Add KDoc comments to HealthRepository and ViewModel public methods
- [ ] T185 [US3] Create `android-app/README.md` with setup, run, test instructions
- [ ] T186 [US3] Fix any lint violations

**Checkpoint**: Native Android app successfully integrates KMP SDK and displays health data.

---

## Phase 4: React Native Demo App (User Story 4 - Priority P2) 🎯

**Goal**: Integrate KMP SDK into React Native app via native modules, display step count

**Independent Test**: Run RN app on both iOS & Android, navigate to Tab 3, see step count via native bridge

### Project Setup

- [ ] T187 [US4] Create `react-native-app/` directory at repository root
- [ ] T188 [US4] Initialize React Native project: `npx react-native init SpecKitRN --template react-native-template-typescript`
- [ ] T189 [US4] Move generated files to `react-native-app/`
- [ ] T190 [US4] Install React Navigation: `npm install @react-navigation/native @react-navigation/bottom-tabs`
- [ ] T191 [US4] Install TypeScript types: `npm install --save-dev @types/react-native`

### iOS Native Module (Swift Bridge)

- [ ] T192 [US4] Create `ios/SpecKitRN/WellnessSDKBridge.swift` in `react-native-app/ios/SpecKitRN/`
- [ ] T193 [US4] Create `WellnessSDKBridge.m` (Objective-C header) to expose Swift class to RN
- [ ] T194 [US4] Add CocoaPods dependency for `wellness-kmp-sdk` in `react-native-app/ios/Podfile`
- [ ] T195 [US4] Run `pod install` in `react-native-app/ios/`
- [ ] T196 [US4] Implement `@objc` methods: `requestPermissions()`, `fetchStepCount(startDate:endDate:resolver:rejecter:)`
- [ ] T197 [US4] Import KMP SDK framework: `import shared`
- [ ] T198 [US4] Bridge KMP SDK calls to React Native promises (resolve/reject)
- [ ] T199 [US4] Handle HealthKit errors, convert to rejected promises with error codes

### Android Native Module (Kotlin Bridge)

- [ ] T200 [US4] Create `WellnessSDKModule.kt` in `react-native-app/android/app/src/main/java/com/speckit/rn/`
- [ ] T201 [US4] Create `WellnessSDKPackage.kt` to register the module
- [ ] T202 [US4] Add dependency for `wellness-kmp-sdk:shared` in `react-native-app/android/app/build.gradle.kts`
- [ ] T203 [US4] Extend `ReactContextBaseJavaModule`, implement `getName()` returning "WellnessSDK"
- [ ] T204 [US4] Implement `@ReactMethod` methods: `requestPermissions()`, `fetchStepCount(startDate, endDate, promise)`
- [ ] T205 [US4] Import KMP SDK: `import com.speckit.wellness.*`
- [ ] T206 [US4] Bridge KMP SDK calls to React Native promises
- [ ] T207 [US4] Handle Health Connect errors, convert to rejected promises with error codes
- [ ] T208 [US4] Register package in `MainApplication.kt`

### TypeScript Interface

- [ ] T209 [P] [US4] Create `src/services/WellnessSDK.ts` in `react-native-app/src/services/`
- [ ] T210 [US4] Import native module: `import { NativeModules } from 'react-native'`
- [ ] T211 [US4] Define TypeScript types: `HealthMetric`, `WellnessSDKError`
- [ ] T212 [US4] Wrap native module methods: `requestPermissions(): Promise<boolean>`, `fetchStepCount(start, end): Promise<HealthMetric>`
- [ ] T213 [US4] Add error handling and type safety

### React Components & Navigation

- [ ] T214 [P] [US4] Create `src/App.tsx` with Bottom Tab Navigator (3 tabs)
- [ ] T215 [P] [US4] Create `src/screens/HomeScreen.tsx` (Tab 1 - React Native only, no native module)
- [ ] T216 [P] [US4] Create `src/screens/ProfileScreen.tsx` (Tab 2 - React Native only, no native module)
- [ ] T217 [US4] Create `src/screens/HealthDashboardScreen.tsx` (Tab 3 - uses WellnessSDK native module)

### Custom Hook

- [ ] T218 [US4] Create `src/hooks/useHealthData.ts`
- [ ] T219 [US4] Implement `useHealthData()` hook managing state: stepCount, loading, error
- [ ] T220 [US4] Call `WellnessSDK.fetchStepCount()` in useEffect
- [ ] T221 [US4] Handle promise rejection, update error state
- [ ] T222 [US4] Return { stepCount, loading, error } from hook

### UI (React Native View)

- [ ] T223 [US4] Implement `HealthDashboardScreen` rendering step count
- [ ] T224 [US4] Use `useHealthData()` hook
- [ ] T225 [US4] Show ActivityIndicator when `loading == true`
- [ ] T226 [US4] Show error message if `error != null`
- [ ] T227 [US4] Show step count if `stepCount != null`
- [ ] T228 [US4] Add Button to request permissions if needed

### Unit Tests - WRITE FIRST! ⚠️

- [ ] T229 [P] [US4] Setup Jest: configure `jest.config.js` in `react-native-app/`
- [ ] T230 [P] [US4] Install testing library: `npm install --save-dev @testing-library/react-native @testing-library/jest-native`
- [ ] T231 [US4] Create `__tests__/HealthDashboardScreen.test.tsx`
- [ ] T232 [US4] Mock `WellnessSDK` native module using `jest.mock()`
- [ ] T233 [US4] Test step count renders correctly when data loaded
- [ ] T234 [US4] Test error message displays when fetch fails
- [ ] T235 [US4] Test loading indicator shows during fetch
- [ ] T236 [US4] Run tests: `npm test` - MUST FAIL until implementation

### Validation & Coverage

- [ ] T237 [US4] Build iOS: `npm run ios` (or `npx react-native run-ios`)
- [ ] T238 [US4] Build Android: `npm run android` (or `npx react-native run-android`)
- [ ] T239 [US4] Run Jest tests: `npm test`
- [ ] T240 [US4] Generate coverage: `npm test -- --coverage`
- [ ] T241 [US4] Verify 80%+ coverage
- [ ] T242 [US4] Manual test iOS: Grant HealthKit permission, see step count in Tab 3
- [ ] T243 [US4] Manual test Android: Grant Health Connect permission, see step count in Tab 3
- [ ] T244 [US4] Manual test: Deny permissions, see error messages

### Linting & Documentation

- [ ] T245 [P] [US4] Setup ESLint: configure `.eslintrc.js` with TypeScript rules
- [ ] T246 [P] [US4] Setup Prettier: configure `.prettierrc` for code formatting
- [ ] T247 [US4] Run ESLint: `npm run lint`
- [ ] T248 [US4] Run Prettier: `npm run format`
- [ ] T249 [US4] Add TSDoc comments to WellnessSDK interface and useHealthData hook
- [ ] T250 [US4] Create `react-native-app/README.md` with setup, run, test instructions
- [ ] T251 [US4] Fix any lint violations

**Checkpoint**: React Native app successfully integrates KMP SDK via native modules on both platforms.

---

## Phase 5: Flutter Demo App (User Story 5 - Priority P2) 🎯

**Goal**: Integrate KMP SDK into Flutter app via platform channels, display step count

**Independent Test**: Run Flutter app on both iOS & Android, navigate to Tab 3, see step count via platform channel

### Project Setup

- [ ] T252 [US5] Create `flutter-app/` directory at repository root
- [ ] T253 [US5] Initialize Flutter project: `flutter create --org com.speckit --platforms=ios,android flutter_app`
- [ ] T254 [US5] Move generated files to `flutter-app/`
- [ ] T255 [US5] Add dependencies in `pubspec.yaml`: no additional packages needed for platform channels

### iOS Platform Plugin (Swift)

- [ ] T256 [US5] Create `WellnessSDKPlugin.swift` in `flutter-app/ios/Runner/`
- [ ] T257 [US5] Add CocoaPods dependency for `wellness-kmp-sdk` in `flutter-app/ios/Podfile`
- [ ] T258 [US5] Run `pod install` in `flutter-app/ios/`
- [ ] T259 [US5] Implement `FlutterMethodChannel` handler in `AppDelegate.swift`
- [ ] T260 [US5] Register method channel with name "com.speckit.wellness_sdk"
- [ ] T261 [US5] Handle methods: `requestPermissions`, `fetchStepCount` (with startDate, endDate args)
- [ ] T262 [US5] Import KMP SDK: `import shared`
- [ ] T263 [US5] Call KMP SDK methods, return results via `FlutterResult` callback
- [ ] T264 [US5] Handle errors, return `FlutterError` with error codes

### Android Platform Plugin (Kotlin)

- [ ] T265 [US5] Create `WellnessSDKPlugin.kt` in `flutter-app/android/app/src/main/kotlin/com/speckit/flutter/`
- [ ] T266 [US5] Add dependency for `wellness-kmp-sdk:shared` in `flutter-app/android/app/build.gradle.kts`
- [ ] T267 [US5] Implement `MethodCallHandler` in `MainActivity.kt`
- [ ] T268 [US5] Register method channel with name "com.speckit.wellness_sdk"
- [ ] T269 [US5] Handle methods: `requestPermissions`, `fetchStepCount`
- [ ] T270 [US5] Import KMP SDK: `import com.speckit.wellness.*`
- [ ] T271 [US5] Call KMP SDK methods using coroutines, return results via `Result.success()` / `Result.error()`
- [ ] T272 [US5] Handle Health Connect errors, return error codes to Flutter

### Dart Service Layer

- [ ] T273 [P] [US5] Create `lib/services/wellness_sdk_service.dart`
- [ ] T274 [US5] Import MethodChannel: `import 'package:flutter/services.dart'`
- [ ] T275 [US5] Create `WellnessSDKService` class with `MethodChannel` instance
- [ ] T276 [US5] Define channel name: "com.speckit.wellness_sdk"
- [ ] T277 [US5] Implement `Future<bool> requestPermissions()` calling platform method
- [ ] T278 [US5] Implement `Future<HealthMetric> fetchStepCount(DateTime start, DateTime end)` calling platform method
- [ ] T279 [US5] Handle `PlatformException`, throw Dart exceptions with user-friendly messages

### Dart Models

- [ ] T280 [P] [US5] Create `lib/models/health_metric.dart`
- [ ] T281 [US5] Define `HealthMetric` class with fields: value, unit, startDate, endDate, source
- [ ] T282 [US5] Implement `fromJson()` factory constructor for parsing platform channel results

### Flutter UI & Navigation

- [ ] T283 [P] [US5] Create `lib/main.dart` with MaterialApp and BottomNavigationBar (3 tabs)
- [ ] T284 [P] [US5] Create `lib/screens/home_screen.dart` (Tab 1 - Flutter widgets only)
- [ ] T285 [P] [US5] Create `lib/screens/profile_screen.dart` (Tab 2 - Flutter widgets only)
- [ ] T286 [US5] Create `lib/screens/health_dashboard_screen.dart` (Tab 3 - uses WellnessSDKService)

### Health Dashboard Screen

- [ ] T287 [US5] Implement `HealthDashboardScreen` as StatefulWidget
- [ ] T288 [US5] Add state variables: `int? stepCount`, `bool isLoading`, `String? errorMessage`
- [ ] T289 [US5] Create `WellnessSDKService` instance
- [ ] T290 [US5] Implement `fetchStepCount()` async method calling service
- [ ] T291 [US5] Call `fetchStepCount()` in `initState()` or with FutureBuilder
- [ ] T292 [US5] Show `CircularProgressIndicator` when `isLoading == true`
- [ ] T293 [US5] Show error message if `errorMessage != null`
- [ ] T294 [US5] Show step count if `stepCount != null`
- [ ] T295 [US5] Add Button to request permissions if needed

### Unit Tests - WRITE FIRST! ⚠️

- [ ] T296 [P] [US5] Setup test dependencies: `flutter_test`, `mockito` in `pubspec.yaml` dev_dependencies
- [ ] T297 [P] [US5] Install mockito: `flutter pub get`
- [ ] T298 [US5] Create `test/health_dashboard_screen_test.dart`
- [ ] T299 [US5] Mock `MethodChannel` using `TestDefaultBinaryMessengerBinding`
- [ ] T300 [US5] Test step count displays when service returns success
- [ ] T301 [US5] Test error message displays when service throws exception
- [ ] T302 [US5] Test loading indicator shows during fetch
- [ ] T303 [US5] Run tests: `flutter test` - MUST FAIL until implementation

### Validation & Coverage

- [ ] T304 [US5] Build iOS: `flutter run` on iOS simulator
- [ ] T305 [US5] Build Android: `flutter run` on Android emulator
- [ ] T306 [US5] Run Flutter tests: `flutter test`
- [ ] T307 [US5] Generate coverage: `flutter test --coverage`
- [ ] T308 [US5] Verify 80%+ coverage: `lcov --summary coverage/lcov.info`
- [ ] T309 [US5] Manual test iOS: Grant HealthKit permission, see step count in Tab 3
- [ ] T310 [US5] Manual test Android: Grant Health Connect permission, see step count in Tab 3
- [ ] T311 [US5] Manual test: Test platform channel error handling

### Linting & Documentation

- [ ] T312 [P] [US5] Setup dart analyze: configure `analysis_options.yaml` with flutter_lints
- [ ] T313 [US5] Run dart analyze: `flutter analyze`
- [ ] T314 [US5] Run dart format: `dart format lib/ test/`
- [ ] T315 [US5] Add DartDoc comments to WellnessSDKService and HealthMetric classes
- [ ] T316 [US5] Create `flutter-app/README.md` with setup, run, test instructions
- [ ] T317 [US5] Fix any analyzer warnings

**Checkpoint**: Flutter app successfully integrates KMP SDK via platform channels on both platforms.

---

## Phase 6: Heart Rate Feature (User Story 6 - Priority P3) 🎯

**Goal**: Extend SDK to support heart rate data fetching (optional extensibility proof)

**Independent Test**: Fetch heart rate data via SDK, verify against native HealthKit/Health Connect

### KMP SDK Extension

- [ ] T318 [P] [US6] Create `HeartRateMeasurement.kt` data class in `wellness-kmp-sdk/shared/src/commonMain/kotlin/com/speckit/wellness/models/`
- [ ] T319 [US6] Add `suspend fun fetchHeartRate(startDate: Long, endDate: Long): HealthResult<List<HeartRateMeasurement>>` to `HealthDataProvider` expect interface
- [ ] T320 [US6] Update `HealthDataRepository` to expose `getHeartRate()` method

### iOS Implementation (HealthKit Heart Rate)

- [ ] T321 [US6] Implement `fetchHeartRate()` actual in `iosMain` using `HKQuantityType.heartRate`
- [ ] T322 [US6] Use `HKSampleQuery` or `HKStatisticsCollectionQuery` for heart rate samples
- [ ] T323 [US6] Convert HKQuantity (bpm) to Double
- [ ] T324 [US6] Handle no data → return empty list

### Android Implementation (Health Connect Heart Rate)

- [ ] T325 [US6] Implement `fetchHeartRate()` actual in `androidMain` using `HeartRateRecord`
- [ ] T326 [US6] Use `ReadRecordsRequest<HeartRateRecord>` with time range filter
- [ ] T327 [US6] Extract bpm values from response records
- [ ] T328 [US6] Handle no data → return empty list

### Unit Tests

- [ ] T329 [P] [US6] Add tests for `getHeartRate()` in `HealthDataRepositoryTest.kt`
- [ ] T330 [US6] Test heart rate success case (mock provider returning sample data)
- [ ] T331 [US6] Test heart rate empty data case
- [ ] T332 [US6] Test heart rate permission denied case
- [ ] T333 [US6] Run tests: `./gradlew wellness-kmp-sdk:shared:allTests`
- [ ] T334 [US6] Verify 85%+ coverage maintained

### Demo App Integration (Optional - Pick One App)

- [ ] T335 [US6] Update Native iOS app `HealthDashboardView` to display heart rate alongside step count
- [ ] T336 [US6] Fetch heart rate via KMP SDK in `HealthKitService`
- [ ] T337 [US6] Update ViewModel to manage heart rate state
- [ ] T338 [US6] Update UI to show heart rate (e.g., "Heart Rate: 72 bpm")

### Validation

- [ ] T339 [US6] Manual test iOS: Fetch heart rate, verify against HealthKit app
- [ ] T340 [US6] Manual test Android: Fetch heart rate, verify against Health Connect app
- [ ] T341 [US6] Verify data accuracy: heart rate values should match native APIs

**Checkpoint**: Heart rate feature proves SDK architecture is extensible to multiple data types.

---

## Phase 7: Documentation, CI/CD & Polish

**Goal**: Finalize project for demo, ensure all documentation and CI pipelines are complete

### Documentation

- [ ] T342 [P] Update `wellness-kmp-sdk/README.md` with complete API documentation, setup, testing
- [ ] T343 [P] Update `ios-app/README.md` with setup (Podfile), run, test instructions
- [ ] T344 [P] Update `android-app/README.md` with setup, run, test instructions
- [ ] T345 [P] Update `react-native-app/README.md` with native module setup, run, test instructions
- [ ] T346 [P] Update `flutter-app/README.md` with platform channel setup, run, test instructions
- [ ] T347 Create root `README.md` at repository root with project overview, goals, structure, quick start
- [ ] T348 Create `.specify/memory/001-kmp-health-sdk-poc.quickstart.md` with step-by-step setup guide
- [ ] T349 Create `.specify/memory/001-kmp-health-sdk-poc.contracts.md` documenting all SDK APIs

### CI/CD Setup

- [ ] T350 [P] Create `.github/workflows/kmp-sdk-ci.yml` for KMP SDK (build, test, coverage)
- [ ] T351 [P] Create `.github/workflows/ios-app-ci.yml` for iOS app (build, test via xcodebuild)
- [ ] T352 [P] Create `.github/workflows/android-app-ci.yml` for Android app (build, test via Gradle)
- [ ] T353 [P] Create `.github/workflows/react-native-ci.yml` for React Native (lint, test via Jest)
- [ ] T354 [P] Create `.github/workflows/flutter-ci.yml` for Flutter (analyze, test)
- [ ] T355 Configure coverage enforcement: fail CI if coverage drops below thresholds (85% SDK, 80% apps)
- [ ] T356 Setup coverage report artifacts in CI (upload HTML reports)

### Final Linting & Code Quality

- [ ] T357 [P] Run ktlint on KMP SDK: `./gradlew wellness-kmp-sdk:ktlintCheck` - fix violations
- [ ] T358 [P] Run Detekt on KMP SDK: `./gradlew wellness-kmp-sdk:detekt` - fix violations
- [ ] T359 [P] Run SwiftLint on iOS app: `swiftlint lint --path ios-app/` - fix violations
- [ ] T360 [P] Run ktlint on Android app: `./gradlew android-app:ktlintCheck` - fix violations
- [ ] T361 [P] Run ESLint on React Native: `cd react-native-app && npm run lint` - fix violations
- [ ] T362 [P] Run dart analyze on Flutter: `cd flutter-app && flutter analyze` - fix violations

### Final Testing

- [ ] T363 Verify all unit tests pass across all projects
- [ ] T364 Verify all CI pipelines are green (build + test + lint + coverage)
- [ ] T365 Manual integration test: Run all 4 demo apps on simulators/emulators, verify step count fetching
- [ ] T366 Manual integration test: Test permission flows on all 4 apps (grant, deny, re-request)
- [ ] T367 Manual integration test: Test error handling (no Health Connect, no data, etc.)

### Demo Preparation

- [ ] T368 Record demo video: Show KMP SDK structure + all 4 apps running side-by-side
- [ ] T369 Prepare demo script: Highlight cross-platform parity, ease of integration, test coverage
- [ ] T370 Create presentation slides (optional): Overview of architecture, results, lessons learned

### Project Closure

- [ ] T371 Review all acceptance criteria from spec.md - verify all completed
- [ ] T372 Review constitution compliance - verify TDD, clean code, expect/actual pattern followed
- [ ] T373 Create final project summary in root README.md with metrics (coverage %, build times, etc.)
- [ ] T374 Tag release: `git tag v1.0.0-poc` and push to remote
- [ ] T375 Archive project artifacts: demo video, documentation, coverage reports

---

## Summary

**Total Tasks**: 375
**Parallel Tasks**: ~50+ (marked with [P])
**Critical Path**: Phase 0 → Phase 1 → (Phase 2 || Phase 3) → (Phase 4 || Phase 5) → Phase 6 → Phase 7

**P1 Tasks (Must Have)**: T008-T186 (KMP SDK + Native iOS + Native Android)
**P2 Tasks (Should Have)**: T187-T317 (React Native + Flutter)
**P3 Tasks (Nice to Have)**: T318-T341 (Heart Rate)
**Overhead Tasks**: T342-T375 (Documentation + CI/CD)

**Estimated Timeline**: 17-26 days (3-5 weeks) for solo developer

---

## Next Steps

1. ✅ Review this task breakdown
2. Create feature branch: `git checkout -b 001-kmp-health-sdk-poc`
3. Begin Phase 1 with T008 (KMP SDK setup)
4. Follow TDD workflow: Write tests FIRST (tasks marked ⚠️), ensure they FAIL, then implement
5. Use `/speckit.implement` to get AI assistance on specific tasks
6. Commit frequently with descriptive messages (follow Conventional Commits)
7. Update task checkboxes as you complete work
8. Review and test thoroughly before moving between phases
