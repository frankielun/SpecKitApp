import XCTest
@testable import SpecKitIOS

/// T113: Unit tests for HealthDashboardViewModel
/// Following TDD approach - these tests should drive the implementation
@MainActor
final class HealthDashboardViewModelTests: XCTestCase {
    
    var viewModel: HealthDashboardViewModel!
    var mockService: MockHealthKitService!
    
    override func setUp() async throws {
        // T114: Setup with mock service
        mockService = MockHealthKitService()
        viewModel = HealthDashboardViewModel(healthService: mockService)
    }
    
    override func tearDown() async throws {
        viewModel = nil
        mockService = nil
    }
    
    // MARK: - T115: Test successful step count fetch
    
    func testFetchStepCountSuccess() async throws {
        // Arrange
        let expectedStepCount = 10000
        mockService.mockStepCount = expectedStepCount
        
        // Act
        await viewModel.fetchStepCount()
        
        // Assert
        XCTAssertEqual(viewModel.stepCount, expectedStepCount, "Step count should match mock data")
        XCTAssertFalse(viewModel.isLoading, "Loading should be false after completion")
        XCTAssertNil(viewModel.errorMessage, "Error message should be nil on success")
    }
    
    // MARK: - T116: Test failure updates error message
    
    func testFetchStepCountFailure() async throws {
        // Arrange
        mockService.shouldFail = true
        mockService.mockError = HealthKitError.dataNotAvailable
        
        // Act
        await viewModel.fetchStepCount()
        
        // Assert
        XCTAssertNil(viewModel.stepCount, "Step count should be nil on failure")
        XCTAssertFalse(viewModel.isLoading, "Loading should be false after error")
        XCTAssertNotNil(viewModel.errorMessage, "Error message should be set on failure")
        XCTAssertTrue(
            viewModel.errorMessage!.contains("not available"),
            "Error message should indicate data unavailable"
        )
    }
    
    func testFetchStepCountPermissionDenied() async throws {
        // Arrange
        mockService.shouldFail = true
        mockService.mockError = HealthKitError.permissionDenied
        
        // Act
        await viewModel.fetchStepCount()
        
        // Assert
        XCTAssertNotNil(viewModel.errorMessage, "Error message should be set")
        XCTAssertTrue(
            viewModel.errorMessage!.contains("permission"),
            "Error message should mention permissions"
        )
    }
    
    // MARK: - T117: Test loading state
    
    func testFetchStepCountLoadingState() async throws {
        // Arrange
        mockService.shouldDelay = true
        
        // Act - Start async task
        let task = Task {
            await viewModel.fetchStepCount()
        }
        
        // Assert - Check loading state immediately
        // Note: In real scenario, would need to check timing, but for unit test we verify state changes
        XCTAssertTrue(viewModel.isLoading, "Loading should be true at start")
        
        // Wait for completion
        await task.value
        
        XCTAssertFalse(viewModel.isLoading, "Loading should be false after completion")
    }
    
    // MARK: - T118: Test permission request flow
    
    func testRequestPermissionsSuccess() async throws {
        // Arrange
        mockService.mockPermissionGranted = true
        mockService.mockStepCount = 5000
        
        // Act
        await viewModel.requestPermissions()
        
        // Assert
        XCTAssertFalse(viewModel.isLoading, "Loading should be false after completion")
        XCTAssertNil(viewModel.errorMessage, "Error message should be nil on success")
        XCTAssertNotNil(viewModel.stepCount, "Step count should be fetched after permissions granted")
    }
    
    func testRequestPermissionsFailure() async throws {
        // Arrange
        mockService.shouldFail = true
        mockService.mockError = HealthKitError.permissionDenied
        
        // Act
        await viewModel.requestPermissions()
        
        // Assert
        XCTAssertFalse(viewModel.isLoading, "Loading should be false after error")
        XCTAssertNotNil(viewModel.errorMessage, "Error message should be set on failure")
    }
}

// MARK: - T114: Mock HealthKitService for testing

class MockHealthKitService: HealthKitService {
    var shouldFail = false
    var shouldDelay = false
    var mockStepCount: Int = 10000
    var mockPermissionGranted: Bool = true
    var mockError: Error = HealthKitError.unknown("Mock error")
    
    override func requestPermissions() async throws -> Bool {
        if shouldDelay {
            try? await Task.sleep(nanoseconds: 100_000_000) // 0.1 second
        }
        
        if shouldFail {
            throw mockError
        }
        
        return mockPermissionGranted
    }
    
    override func fetchStepCount(from startDate: Date, to endDate: Date) async throws -> Int {
        if shouldDelay {
            try? await Task.sleep(nanoseconds: 100_000_000) // 0.1 second
        }
        
        if shouldFail {
            throw mockError
        }
        
        return mockStepCount
    }
}
