import Foundation
import Combine
import shared

/// T098: ViewModel for Health Dashboard (ObservableObject pattern)
@MainActor
class HealthDashboardViewModel: ObservableObject {
    // MARK: - T100-T102: Published properties
    
    /// T100: Current step count
    @Published var stepCount: Int?
    
    /// T337: Current heart rate (average)
    @Published var heartRate: Double?
    
    /// T101: Loading state
    @Published var isLoading: Bool = false
    
    /// T102: Error message
    @Published var errorMessage: String?
    
    // MARK: - Dependencies
    
    private let healthService: HealthKitService
    
    // MARK: - Initialization
    
    init(healthService: HealthKitService = HealthKitService()) {
        self.healthService = healthService
    }
    
    // MARK: - T103-T105: Actions
    
    /// T103: Fetch step count from KMP SDK
    /// T105: Update loading/error states appropriately
    func fetchStepCount() async {
        isLoading = true
        errorMessage = nil
        
        do {
            // Calculate date range (today)
            let endDate = Date()
            let startDate = Calendar.current.startOfDay(for: endDate)
            
            // T103: Call service layer
            let count = try await healthService.fetchStepCount(from: startDate, to: endDate)
            
            // Update UI on main thread
            self.stepCount = count
            self.isLoading = false
        } catch {
            // T105: Handle errors
            self.errorMessage = error.localizedDescription
            self.isLoading = false
        }
    }
    
    /// T337: Fetch heart rate from KMP SDK
    func fetchHeartRate() async {
        isLoading = true
        errorMessage = nil
        
        do {
            // Calculate date range (today)
            let endDate = Date()
            let startDate = Calendar.current.startOfDay(for: endDate)
            
            // Call service layer
            let measurements = try await healthService.fetchHeartRate(from: startDate, to: endDate)
            
            // Calculate average heart rate
            if !measurements.isEmpty {
                let totalBpm = measurements.reduce(0.0) { $0 + $1.beatsPerMinute }
                self.heartRate = totalBpm / Double(measurements.count)
            } else {
                self.heartRate = nil
            }
            
            self.isLoading = false
        } catch {
            // Handle errors
            self.errorMessage = error.localizedDescription
            self.isLoading = false
        }
    }
    
    /// T335: Fetch all health data (steps + heart rate)
    func fetchAllHealthData() async {
        await fetchStepCount()
        await fetchHeartRate()
    }
    
    /// T104: Request health data permissions
    /// T105: Update states appropriately
    func requestPermissions() async {
        isLoading = true
        errorMessage = nil
        
        do {
            // T104: Call service layer
            let granted = try await healthService.requestPermissions()
            
            if granted {
                // If permissions granted, automatically fetch data
                await fetchAllHealthData()
            } else {
                self.errorMessage = "Permissions were not granted"
                self.isLoading = false
            }
        } catch {
            // T105: Handle errors
            self.errorMessage = error.localizedDescription
            self.isLoading = false
        }
    }
}
