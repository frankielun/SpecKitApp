package com.speckit.wellness

import com.speckit.wellness.models.HealthMetric
import com.speckit.wellness.models.HealthResult

/**
 * T022: Repository for accessing health data with business logic.
 * 
 * This class follows the Repository pattern and provides a clean API
 * for accessing health data. It delegates to the platform-specific
 * HealthDataProvider and adds input validation.
 * 
 * @property provider The platform-specific health data provider
 */
class HealthDataRepository(
    private val provider: HealthDataProvider // T023: Constructor with dependency
) {
    /**
     * T025: Request permissions to access health data.
     * 
     * @return Success(true) if authorized, appropriate error otherwise
     */
    suspend fun requestPermissions(): HealthResult<Boolean> {
        return provider.requestAuthorization()
    }
    
    /**
     * T024: Get step count for a specific time period.
     * 
     * @param startDate Unix timestamp (milliseconds) for the start of the period
     * @param endDate Unix timestamp (milliseconds) for the end of the period
     * @return Success with step count data, or appropriate error
     */
    suspend fun getStepCount(startDate: Long, endDate: Long): HealthResult<HealthMetric> {
        // T026: Input validation
        if (startDate < 0 || endDate < 0) {
            return HealthResult.UnknownError("Timestamps cannot be negative")
        }
        
        if (startDate >= endDate) {
            return HealthResult.UnknownError("Start date must be before end date")
        }
        
        // Delegate to provider
        return provider.fetchStepCount(startDate, endDate)
    }
}
