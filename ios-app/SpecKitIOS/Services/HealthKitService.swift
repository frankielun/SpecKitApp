import Foundation
import shared

/// T091: Swift service layer wrapping KMP SDK
/// This provides a Swift-friendly API for the KMP HealthDataRepository
class HealthKitService {
    
    /// T093: Instance of KMP SDK's HealthDataRepository
    private let repository: HealthDataRepository
    
    init() {
        // T093: Initialize with platform-specific HealthDataProvider
        let provider = HealthDataProviderKt.createHealthDataProvider()
        self.repository = HealthDataRepository(provider: provider)
    }
    
    /// T094: Request HealthKit permissions
    /// - Returns: True if authorized, throws error otherwise
    func requestPermissions() async throws -> Bool {
        // T094: Wrap KMP SDK's requestPermissions() in Swift async
        return try await withCheckedThrowingContinuation { continuation in
            repository.requestPermissions { result, error in
                if let error = error {
                    continuation.resume(throwing: HealthKitError.unknown(error.localizedDescription))
                } else if let healthResult = result {
                    do {
                        let success = try self.mapHealthResult(healthResult)
                        continuation.resume(returning: success)
                    } catch {
                        continuation.resume(throwing: error)
                    }
                }
            }
        }
    }
    
    /// T095: Fetch step count for a date range
    /// - Parameters:
    ///   - startDate: Start of the period
    ///   - endDate: End of the period
    /// - Returns: Step count value
    func fetchStepCount(from startDate: Date, to endDate: Date) async throws -> Int {
        // T095: Convert Swift Date to Long (Unix timestamp milliseconds)
        let startTimestamp = Int64(startDate.timeIntervalSince1970 * 1000)
        let endTimestamp = Int64(endDate.timeIntervalSince1970 * 1000)
        
        // T095: Call KMP SDK's getStepCount()
        return try await withCheckedThrowingContinuation { continuation in
            repository.getStepCount(startDate: startTimestamp, endDate: endTimestamp) { result, error in
                if let error = error {
                    continuation.resume(throwing: HealthKitError.unknown(error.localizedDescription))
                } else if let healthResult = result {
                    do {
                        let metric: HealthMetric = try self.mapHealthResult(healthResult)
                        continuation.resume(returning: Int(metric.value))
                    } catch {
                        continuation.resume(throwing: error)
                    }
                }
            }
        }
    }
    
    /// T336: Fetch heart rate for a date range
    /// - Parameters:
    ///   - startDate: Start of the period
    ///   - endDate: End of the period
    /// - Returns: Array of heart rate measurements
    func fetchHeartRate(from startDate: Date, to endDate: Date) async throws -> [HeartRateMeasurement] {
        let startTimestamp = Int64(startDate.timeIntervalSince1970 * 1000)
        let endTimestamp = Int64(endDate.timeIntervalSince1970 * 1000)
        
        return try await withCheckedThrowingContinuation { continuation in
            repository.getHeartRate(startDate: startTimestamp, endDate: endTimestamp) { result, error in
                if let error = error {
                    continuation.resume(throwing: HealthKitError.unknown(error.localizedDescription))
                } else if let healthResult = result {
                    do {
                        let measurements: [HeartRateMeasurement] = try self.mapHealthResult(healthResult)
                        continuation.resume(returning: measurements)
                    } catch {
                        continuation.resume(throwing: error)
                    }
                }
            }
        }
    }
    
    // MARK: - T096-T097: Result mapping
    
    /// T096: Map KMP SDK's HealthResult to Swift type
    /// T097: Handle errors with user-friendly messages
    private func mapHealthResult<T>(_ healthResult: HealthResult) throws -> T {
        if let success = healthResult as? HealthResultSuccess {
            guard let data = success.data as? T else {
                throw HealthKitError.unknown("Invalid data type")
            }
            return data
        } else if healthResult is HealthResultPermissionDenied {
            throw HealthKitError.permissionDenied
        } else if healthResult is HealthResultDataNotAvailable {
            throw HealthKitError.dataNotAvailable
        } else if let unknown = healthResult as? HealthResultUnknownError {
            throw HealthKitError.unknown(unknown.message)
        } else {
            throw HealthKitError.unknown("Unexpected error type")
        }
    }
}

/// T097: User-friendly error types
enum HealthKitError: LocalizedError {
    case permissionDenied
    case dataNotAvailable
    case unknown(String)
    
    var errorDescription: String? {
        switch self {
        case .permissionDenied:
            return "Health data access denied. Please enable permissions in Settings."
        case .dataNotAvailable:
            return "No health data available for the selected period."
        case .unknown(let message):
            return "An error occurred: \(message)"
        }
    }
}
