# SpecKitApp - GitHub Copilot Instructions

## Project Overview

**SpecKitApp** is a proof-of-concept (PoC) project demonstrating the creation and integration of a Kotlin Multiplatform (KMP) SDK for health and wellness functionality across multiple native and cross-platform mobile frameworks.

### Project Goals

1. **Create a KMP SDK** (`wellness-kmp-sdk`) with Compose Multiplatform that provides health data access via:
   - **iOS**: HealthKit
   - **Android**: Health Connect

2. **Demonstrate SDK Integration** across four demo applications:
   - Native iOS app (Swift/SwiftUI)
   - Native Android app (Kotlin/Jetpack Compose)
   - React Native app (iOS + Android)
   - Flutter app (iOS + Android)

3. **Validate Cross-Platform Compatibility**: Prove that the KMP SDK can be successfully embedded and function correctly in all target platforms.

---

## Project Structure

```
SpecKitApp/
├── wellness-kmp-sdk/           # KMP SDK (shared Kotlin code + Compose Multiplatform)
│   ├── shared/                 # Shared KMP module
│   │   ├── src/
│   │   │   ├── commonMain/     # Common Kotlin code
│   │   │   ├── commonTest/     # Common unit tests
│   │   │   ├── androidMain/    # Android-specific (Health Connect)
│   │   │   ├── androidTest/    # Android unit tests
│   │   │   ├── iosMain/        # iOS-specific (HealthKit)
│   │   │   └── iosTest/        # iOS unit tests
│   │   └── build.gradle.kts
│   └── build.gradle.kts
├── ios-app/                    # Native iOS demo app (Swift/SwiftUI)
│   ├── SpecKitIOS/
│   ├── SpecKitIOSTests/        # Unit tests
│   └── Podfile
├── android-app/                # Native Android demo app (Kotlin/Compose)
│   ├── app/
│   │   └── src/
│   │       ├── main/
│   │       └── test/           # Unit tests
│   └── build.gradle.kts
├── react-native-app/           # React Native demo app (TypeScript)
│   ├── src/
│   ├── __tests__/              # Jest unit tests
│   ├── android/
│   ├── ios/
│   └── package.json
├── flutter-app/                # Flutter demo app (Dart)
│   ├── lib/
│   ├── test/                   # Flutter unit tests
│   ├── android/
│   ├── ios/
│   └── pubspec.yaml
└── .github/
    └── copilot/
        └── default.instructions.md
```

---

## Technology Stack & Versions

### KMP SDK
- **Kotlin**: 1.9.22+
- **Compose Multiplatform**: 1.6.0+
- **Gradle**: 8.5+
- **Health Connect**: androidx.health.connect:connect-client:1.1.0-alpha07+
- **HealthKit**: Xcode 15+ (native iOS APIs)

### Demo Apps
- **iOS**: Swift 5.9+, SwiftUI, iOS 15.0+ deployment target
- **Android**: Kotlin 1.9.22+, Jetpack Compose, minSdk 26, targetSdk 34+
- **React Native**: 0.73+, TypeScript 5.3+, React 18.2+
- **Flutter**: 3.19+, Dart 3.3+

---

## Coding Standards

### General Principles
- **Industry Standards**: Follow official style guides for each language/framework
- **Clean Code**: Write self-documenting code with meaningful variable/function names
- **SOLID Principles**: Apply where appropriate
- **DRY (Don't Repeat Yourself)**: Extract reusable logic into shared functions
- **Error Handling**: Always handle errors gracefully with proper logging
- **Documentation**: Document public APIs, complex logic, and platform-specific implementations

### Kotlin (KMP SDK + Android App)

**Style Guide**: [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)

#### Key Conventions
```kotlin
// Class names: PascalCase
class HealthDataRepository

// Function names: camelCase
fun fetchStepCount(): Int

// Constants: UPPER_SNAKE_CASE
const val MAX_RETRY_ATTEMPTS = 3

// Prefer data classes for models
data class HealthMetric(
    val value: Double,
    val unit: String,
    val timestamp: Long
)

// Use expect/actual for platform-specific implementations
// commonMain
expect class HealthKitWrapper {
    suspend fun getStepCount(startDate: Long, endDate: Long): Int
}

// iosMain
actual class HealthKitWrapper {
    actual suspend fun getStepCount(startDate: Long, endDate: Long): Int {
        // HealthKit implementation
    }
}

// androidMain
actual class HealthKitWrapper {
    actual suspend fun getStepCount(startDate: Long, endDate: Long): Int {
        // Health Connect implementation
    }
}

// Prefer coroutines over callbacks
suspend fun fetchData(): Result<Data>

// Use sealed classes for result types
sealed class HealthResult {
    data class Success(val data: HealthMetric) : HealthResult()
    data class Error(val message: String) : HealthResult()
}
```

#### Dependency Injection
- Use constructor injection
- Consider Koin for DI in KMP projects

### Swift (iOS App)

**Style Guide**: [Swift API Design Guidelines](https://swift.org/documentation/api-design-guidelines/)

#### Key Conventions
```swift
// Class names: PascalCase
class HealthDataService

// Function names: camelCase
func fetchStepCount() async throws -> Int

// Constants: camelCase
let maxRetryAttempts = 3

// Prefer structs for models
struct HealthMetric {
    let value: Double
    let unit: String
    let timestamp: Date
}

// Use async/await for asynchronous operations
func loadHealthData() async throws -> [HealthMetric] {
    // Implementation
}

// Use Result type for error handling
func fetchData() -> Result<HealthMetric, HealthError>

// SwiftUI View conventions
struct ContentView: View {
    @StateObject private var viewModel = ContentViewModel()
    
    var body: some View {
        // UI implementation
    }
}

// MVVM pattern for SwiftUI
class ContentViewModel: ObservableObject {
    @Published var healthData: [HealthMetric] = []
}
```

### TypeScript (React Native App)

**Style Guide**: [Airbnb JavaScript Style Guide](https://github.com/airbnb/javascript) + TypeScript

#### Key Conventions
```typescript
// Interface names: PascalCase (no 'I' prefix)
interface HealthMetric {
  value: number;
  unit: string;
  timestamp: number;
}

// Type aliases: PascalCase
type HealthResult = HealthMetric | null;

// Function names: camelCase
const fetchStepCount = async (): Promise<number> => {
  // Implementation
};

// React component names: PascalCase
const HealthDashboard: React.FC = () => {
  // Implementation
};

// Custom hooks: use prefix
const useHealthData = () => {
  // Hook implementation
};

// Constants: UPPER_SNAKE_CASE
const MAX_RETRY_ATTEMPTS = 3;

// Prefer functional components with hooks
const HomeScreen: React.FC = () => {
  const [data, setData] = useState<HealthMetric[]>([]);
  
  useEffect(() => {
    // Side effects
  }, []);
  
  return <View>...</View>;
};
```

### Dart (Flutter App)

**Style Guide**: [Effective Dart](https://dart.dev/guides/language/effective-dart)

#### Key Conventions
```dart
// Class names: PascalCase
class HealthDataService

// Function names: camelCase
Future<int> fetchStepCount() async {
  // Implementation
}

// Constants: camelCase
const int maxRetryAttempts = 3;

// Private members: leading underscore
class _HealthDataState extends State<HealthDataWidget> {
  // Implementation
}

// Model classes
class HealthMetric {
  final double value;
  final String unit;
  final DateTime timestamp;
  
  const HealthMetric({
    required this.value,
    required this.unit,
    required this.timestamp,
  });
}

// Stateless vs Stateful widgets
class HealthDashboard extends StatelessWidget {
  const HealthDashboard({Key? key}) : super(key: key);
  
  @override
  Widget build(BuildContext context) {
    return Container();
  }
}

// Use async/await for asynchronous operations
Future<List<HealthMetric>> loadHealthData() async {
  // Implementation
}
```

---

## Linting & Formatting Tools

### Kotlin (KMP SDK + Android)

**Tools**: Detekt + ktlint

#### Setup (build.gradle.kts)
```kotlin
plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
}

ktlint {
    version.set("1.1.0")
    android.set(true)
    ignoreFailures.set(false)
}
```

#### Run Commands
```bash
# Check code style
./gradlew ktlintCheck

# Auto-format code
./gradlew ktlintFormat

# Run static analysis
./gradlew detekt
```

### Swift (iOS)

**Tool**: SwiftLint

#### Setup (.swiftlint.yml)
```yaml
disabled_rules:
  - trailing_whitespace
opt_in_rules:
  - empty_count
  - empty_string
included:
  - ios-app/SpecKitIOS
excluded:
  - Pods
  - ios-app/SpecKitIOS/Generated
line_length: 120
```

#### Run Commands
```bash
# Check and lint
swiftlint lint

# Auto-correct
swiftlint --fix
```

### TypeScript/React Native

**Tools**: ESLint + Prettier

#### Setup (package.json)
```json
{
  "devDependencies": {
    "@typescript-eslint/eslint-plugin": "^6.19.0",
    "@typescript-eslint/parser": "^6.19.0",
    "eslint": "^8.56.0",
    "eslint-config-airbnb": "^19.0.4",
    "eslint-plugin-react": "^7.33.2",
    "eslint-plugin-react-hooks": "^4.6.0",
    "prettier": "^3.2.4"
  },
  "scripts": {
    "lint": "eslint . --ext .ts,.tsx",
    "lint:fix": "eslint . --ext .ts,.tsx --fix",
    "format": "prettier --write \"src/**/*.{ts,tsx,json}\""
  }
}
```

#### Run Commands
```bash
npm run lint
npm run lint:fix
npm run format
```

### Dart/Flutter

**Tool**: dart analyze + dart format

#### Setup (analysis_options.yaml)
```yaml
include: package:flutter_lints/flutter.yaml

linter:
  rules:
    - prefer_const_constructors
    - prefer_const_literals_to_create_immutables
    - prefer_final_fields
    - always_declare_return_types
    - avoid_print
    - prefer_single_quotes
```

#### Run Commands
```bash
# Analyze code
flutter analyze

# Format code
dart format lib/ test/

# Fix issues
dart fix --apply
```

---

## Unit Testing Standards

### Testing Philosophy
- **Comprehensive Coverage**: Aim for 80%+ code coverage across all projects
- **Test Pyramid**: Focus on unit tests > integration tests > UI tests
- **Test-Driven Development (TDD)**: Write tests before implementation when appropriate
- **Mock External Dependencies**: Use mocks/stubs for platform-specific APIs (HealthKit, Health Connect)
- **Test Naming**: Use descriptive names that explain what is being tested

### Kotlin (KMP SDK)

**Framework**: Kotlin Test + MockK

#### Setup (build.gradle.kts)
```kotlin
kotlin {
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.mockk:mockk:1.13.8")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }
    }
}
```

#### Example Test
```kotlin
// commonTest
class HealthDataRepositoryTest {
    private lateinit var repository: HealthDataRepository
    private lateinit var mockHealthKitWrapper: HealthKitWrapper
    
    @BeforeTest
    fun setup() {
        mockHealthKitWrapper = mockk()
        repository = HealthDataRepository(mockHealthKitWrapper)
    }
    
    @Test
    fun `fetchStepCount returns correct value on success`() = runTest {
        // Given
        val expectedSteps = 10000
        coEvery { mockHealthKitWrapper.getStepCount(any(), any()) } returns expectedSteps
        
        // When
        val result = repository.fetchStepCount(startDate = 0L, endDate = 1000L)
        
        // Then
        assertEquals(expectedSteps, result)
    }
    
    @Test
    fun `fetchStepCount handles error gracefully`() = runTest {
        // Given
        coEvery { mockHealthKitWrapper.getStepCount(any(), any()) } throws Exception("API Error")
        
        // When/Then
        assertFailsWith<Exception> {
            repository.fetchStepCount(startDate = 0L, endDate = 1000L)
        }
    }
}
```

#### Run Commands
```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew koverHtmlReport
```

### Swift (iOS App)

**Framework**: XCTest

#### Example Test
```swift
import XCTest
@testable import SpecKitIOS

class HealthDataServiceTests: XCTestCase {
    var sut: HealthDataService!
    var mockHealthKit: MockHealthKitWrapper!
    
    override func setUp() {
        super.setUp()
        mockHealthKit = MockHealthKitWrapper()
        sut = HealthDataService(healthKit: mockHealthKit)
    }
    
    override func tearDown() {
        sut = nil
        mockHealthKit = nil
        super.tearDown()
    }
    
    func testFetchStepCount_Success_ReturnsCorrectValue() async throws {
        // Given
        let expectedSteps = 10000
        mockHealthKit.stepCountToReturn = expectedSteps
        
        // When
        let result = try await sut.fetchStepCount()
        
        // Then
        XCTAssertEqual(result, expectedSteps)
    }
    
    func testFetchStepCount_Failure_ThrowsError() async {
        // Given
        mockHealthKit.shouldThrowError = true
        
        // When/Then
        do {
            _ = try await sut.fetchStepCount()
            XCTFail("Expected error to be thrown")
        } catch {
            XCTAssertNotNil(error)
        }
    }
}
```

#### Run Commands
```bash
# Run tests via Xcode
xcodebuild test -scheme SpecKitIOS -destination 'platform=iOS Simulator,name=iPhone 15'

# Or use Xcode UI: Cmd+U
```

### TypeScript (React Native)

**Framework**: Jest + React Native Testing Library

#### Setup (package.json)
```json
{
  "devDependencies": {
    "@testing-library/react-native": "^12.4.3",
    "@testing-library/jest-native": "^5.4.3",
    "jest": "^29.7.0"
  },
  "scripts": {
    "test": "jest",
    "test:watch": "jest --watch",
    "test:coverage": "jest --coverage"
  }
}
```

#### Example Test
```typescript
import { render, waitFor } from '@testing-library/react-native';
import { HealthDashboard } from '../HealthDashboard';
import { useHealthData } from '../hooks/useHealthData';

jest.mock('../hooks/useHealthData');

describe('HealthDashboard', () => {
  it('renders step count correctly', async () => {
    // Given
    (useHealthData as jest.Mock).mockReturnValue({
      stepCount: 10000,
      loading: false,
      error: null,
    });
    
    // When
    const { getByText } = render(<HealthDashboard />);
    
    // Then
    await waitFor(() => {
      expect(getByText('10000')).toBeTruthy();
    });
  });
  
  it('displays error message on fetch failure', async () => {
    // Given
    (useHealthData as jest.Mock).mockReturnValue({
      stepCount: null,
      loading: false,
      error: 'Failed to fetch data',
    });
    
    // When
    const { getByText } = render(<HealthDashboard />);
    
    // Then
    await waitFor(() => {
      expect(getByText('Failed to fetch data')).toBeTruthy();
    });
  });
});
```

#### Run Commands
```bash
npm test
npm run test:coverage
```

### Dart (Flutter)

**Framework**: flutter_test + Mockito

#### Setup (pubspec.yaml)
```yaml
dev_dependencies:
  flutter_test:
    sdk: flutter
  mockito: ^5.4.4
  build_runner: ^2.4.8
```

#### Example Test
```dart
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';

@GenerateMocks([HealthDataService])
import 'health_data_repository_test.mocks.dart';

void main() {
  group('HealthDataRepository', () {
    late HealthDataRepository repository;
    late MockHealthDataService mockService;
    
    setUp(() {
      mockService = MockHealthDataService();
      repository = HealthDataRepository(service: mockService);
    });
    
    test('fetchStepCount returns correct value on success', () async {
      // Given
      const expectedSteps = 10000;
      when(mockService.getStepCount(any, any))
          .thenAnswer((_) async => expectedSteps);
      
      // When
      final result = await repository.fetchStepCount(
        startDate: DateTime.now(),
        endDate: DateTime.now(),
      );
      
      // Then
      expect(result, expectedSteps);
    });
    
    test('fetchStepCount throws exception on failure', () async {
      // Given
      when(mockService.getStepCount(any, any))
          .thenThrow(Exception('API Error'));
      
      // When/Then
      expect(
        () => repository.fetchStepCount(
          startDate: DateTime.now(),
          endDate: DateTime.now(),
        ),
        throwsException,
      );
    });
  });
}
```

#### Run Commands
```bash
# Run tests
flutter test

# Run with coverage
flutter test --coverage

# View coverage report
genhtml coverage/lcov.info -o coverage/html
open coverage/html/index.html
```

### Test Coverage Requirements

**Minimum Coverage Targets**:
- **KMP SDK**: 85%+ (critical business logic)
- **Native Apps**: 80%+
- **React Native**: 80%+
- **Flutter**: 80%+

**Coverage Enforcement**: Configure CI/CD pipelines to fail builds if coverage drops below thresholds.

---

## HealthKit & Health Connect Integration

### iOS - HealthKit

#### Authorization
```swift
import HealthKit

class HealthKitManager {
    let healthStore = HKHealthStore()
    
    func requestAuthorization() async throws {
        let stepType = HKQuantityType.quantityType(forIdentifier: .stepCount)!
        let typesToRead: Set<HKObjectType> = [stepType]
        
        try await healthStore.requestAuthorization(toShare: [], read: typesToRead)
    }
}
```

#### Fetch Data
```swift
func fetchStepCount(start: Date, end: Date) async throws -> Double {
    let stepType = HKQuantityType.quantityType(forIdentifier: .stepCount)!
    let predicate = HKQuery.predicateForSamples(
        withStart: start,
        end: end,
        options: .strictStartDate
    )
    
    let query = HKStatisticsQuery(
        quantityType: stepType,
        quantitySamplePredicate: predicate,
        options: .cumulativeSum
    ) { _, result, error in
        // Handle result
    }
    
    healthStore.execute(query)
}
```

### Android - Health Connect

#### Setup (AndroidManifest.xml)
```xml
<uses-permission android:name="android.permission.health.READ_STEPS"/>
<uses-permission android:name="android.permission.health.WRITE_STEPS"/>
```

#### Authorization & Fetch
```kotlin
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter

class HealthConnectManager(private val context: Context) {
    private val healthConnectClient = HealthConnectClient.getOrCreate(context)
    
    suspend fun requestPermissions(activity: Activity) {
        val permissions = setOf(
            HealthPermission.getReadPermission(StepsRecord::class)
        )
        
        val granted = healthConnectClient.permissionController
            .getGrantedPermissions()
        
        if (!granted.containsAll(permissions)) {
            // Request permissions via Activity Result API
        }
    }
    
    suspend fun fetchStepCount(startTime: Instant, endTime: Instant): Long {
        val request = ReadRecordsRequest(
            recordType = StepsRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        
        val response = healthConnectClient.readRecords(request)
        return response.records.sumOf { it.count }
    }
}
```

### KMP Wrapper Pattern

#### Common Interface (commonMain)
```kotlin
expect class HealthDataProvider {
    suspend fun requestAuthorization(): Boolean
    suspend fun fetchStepCount(startDate: Long, endDate: Long): Result<Long>
    suspend fun fetchHeartRate(startDate: Long, endDate: Long): Result<Double>
}

sealed class HealthResult<out T> {
    data class Success<T>(val data: T) : HealthResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : HealthResult<Nothing>()
}
```

#### Platform Implementations
- **iosMain**: Wraps HealthKit APIs
- **androidMain**: Wraps Health Connect APIs

#### Unit Testing Strategy
- Mock platform-specific implementations in `commonTest`
- Test business logic independently of platform APIs
- Create fake/stub implementations for testing

---

## Demo App Architecture

### Tab Bar Structure

Each demo app should implement a bottom tab bar with **3 tabs**:

1. **Tab 1**: Native Home Screen
   - Built with platform-native code (no KMP dependency)
   - Simple UI showcasing native capabilities

2. **Tab 2**: Native Profile/Settings Screen
   - Built with platform-native code (no KMP dependency)
   - User settings or profile information

3. **Tab 3**: Health Dashboard (KMP Integration)
   - **Uses the `wellness-kmp-sdk` module**
   - Displays health metrics (steps, heart rate, etc.)
   - Proves KMP SDK integration and functionality

### Integration Validation Checklist

For each demo app, validate:

- ✅ KMP SDK successfully embedded as a dependency
- ✅ SDK can be imported/initialized in native code
- ✅ Health data can be fetched via SDK on both iOS and Android
- ✅ UI renders correctly with data from KMP module
- ✅ Permissions are handled properly (HealthKit/Health Connect)
- ✅ Error handling works across platform boundaries
- ✅ Unit tests pass for SDK integration layer
- ✅ App builds and runs on physical devices
- ✅ Performance is acceptable (no noticeable lag)

---

## Git & CI/CD Guidelines

### Branch Strategy
- `main`: Production-ready code
- `develop`: Integration branch for features
- `feature/*`: New features
- `bugfix/*`: Bug fixes
- `hotfix/*`: Critical production fixes

### Commit Messages
Follow [Conventional Commits](https://www.conventionalcommits.org/):
```
feat(kmp-sdk): add step count fetching
fix(ios-app): resolve HealthKit authorization issue
test(android-app): add unit tests for HealthRepository
docs(readme): update setup instructions
chore(deps): upgrade Kotlin to 1.9.23
```

### Pre-commit Hooks
Set up Git hooks to run linters and formatters before commits:
```bash
# .git/hooks/pre-commit
#!/bin/sh
./gradlew ktlintCheck detekt
cd ios-app && swiftlint
cd react-native-app && npm run lint
cd flutter-app && flutter analyze
```

### CI/CD Pipeline (Recommended)

**GitHub Actions example**:
```yaml
name: CI

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run KMP SDK Tests
        run: ./gradlew test
      - name: Check Code Coverage
        run: ./gradlew koverVerify
```

---

## Documentation Standards

### README Files
Each subfolder should have a README.md with:
- **Overview**: What the module/app does
- **Prerequisites**: Required tools and versions
- **Setup**: Step-by-step installation instructions
- **Usage**: How to run/build the app
- **Testing**: How to run tests
- **Architecture**: High-level design decisions

### Code Documentation

#### Kotlin
```kotlin
/**
 * Repository for managing health data operations.
 * 
 * This class provides a unified interface for accessing health metrics
 * across different platforms (iOS/Android).
 * 
 * @property healthProvider Platform-specific health data provider
 */
class HealthDataRepository(
    private val healthProvider: HealthDataProvider
) {
    /**
     * Fetches the total step count for a given date range.
     * 
     * @param startDate Start timestamp in milliseconds
     * @param endDate End timestamp in milliseconds
     * @return Result containing step count or error
     */
    suspend fun fetchStepCount(startDate: Long, endDate: Long): Result<Long> {
        // Implementation
    }
}
```

#### Swift
```swift
/// Repository for managing health data operations.
///
/// This class provides access to HealthKit data and integrates
/// with the KMP SDK for cross-platform consistency.
class HealthDataRepository {
    /// Fetches the total step count for a given date range.
    ///
    /// - Parameters:
    ///   - startDate: Start date for the query
    ///   - endDate: End date for the query
    /// - Returns: Total step count
    /// - Throws: HealthKitError if authorization fails or data unavailable
    func fetchStepCount(startDate: Date, endDate: Date) async throws -> Int {
        // Implementation
    }
}
```

---

## Best Practices Summary

### KMP SDK Development
1. **Maximize Shared Code**: Keep as much logic as possible in `commonMain`
2. **Use expect/actual Judiciously**: Only for platform-specific APIs (HealthKit, Health Connect)
3. **Kotlinx Libraries**: Prefer `kotlinx.coroutines`, `kotlinx.serialization`, `kotlinx.datetime`
4. **Compose Multiplatform**: Share UI components where feasible
5. **Error Handling**: Use `Result` or sealed classes for error propagation

### Platform Integration
1. **Thin Wrappers**: Keep platform-specific code minimal (just API calls)
2. **Dependency Injection**: Pass dependencies explicitly for testability
3. **Version Compatibility**: Test on multiple OS versions (iOS 15+, Android 8.0+)
4. **Permissions**: Handle runtime permissions gracefully with clear user messaging

### Code Quality
1. **Linting**: Run linters on every commit (pre-commit hooks)
2. **Unit Tests**: Write tests for all business logic (80%+ coverage)
3. **Code Reviews**: Require peer reviews for all PRs
4. **Static Analysis**: Use Detekt, SwiftLint to catch issues early
5. **Documentation**: Keep README and code comments up to date

### Performance
1. **Lazy Loading**: Load health data on demand, not upfront
2. **Caching**: Cache frequently accessed data (with expiration)
3. **Background Tasks**: Fetch large datasets on background threads
4. **Memory Management**: Be mindful of large data sets from Health APIs

---

## Resources & References

### KMP & Compose Multiplatform
- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform by JetBrains](https://www.jetbrains.com/lp/compose-multiplatform/)
- [KMP Samples](https://github.com/Kotlin/kmm-samples)

### Health APIs
- [HealthKit Documentation (Apple)](https://developer.apple.com/documentation/healthkit)
- [Health Connect (Android)](https://developer.android.com/health-and-fitness/guides/health-connect)

### Testing
- [Kotlin Test Documentation](https://kotlinlang.org/api/latest/kotlin.test/)
- [MockK](https://mockk.io/)
- [XCTest (Apple)](https://developer.apple.com/documentation/xctest)
- [Jest](https://jestjs.io/)
- [Flutter Testing](https://docs.flutter.dev/testing)

### Style Guides
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Swift API Design Guidelines](https://swift.org/documentation/api-design-guidelines/)
- [Airbnb JavaScript/React Style Guide](https://github.com/airbnb/javascript)
- [Effective Dart](https://dart.dev/guides/language/effective-dart)

---

## Support & Contribution

For questions, issues, or contributions to this PoC project, please:
1. Check existing documentation in each module's README
2. Review closed issues for similar problems
3. Open a new issue with detailed reproduction steps
4. Follow the coding standards and testing requirements outlined above

**Happy Coding! 🚀**
