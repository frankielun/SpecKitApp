import Foundation
import Combine

/// T098: ViewModel for Health Dashboard (ObservableObject pattern)
@MainActor
class HealthDashboardViewModel: ObservableObject {
    // MARK: - T100-T102: Published properties
    
    /// T100: Current step count
    @Published var stepCount: Int?
    
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
            // Calculate date range (last 7 days)
            let endDate = Date()
            let startDate = Calendar.current.date(byAdding: .day, value: -7, to: endDate) ?? endDate
            
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
                await fetchStepCount()
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
