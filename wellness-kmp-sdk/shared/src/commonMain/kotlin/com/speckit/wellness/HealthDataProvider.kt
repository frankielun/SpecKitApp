package com.speckit.wellness

import com.speckit.wellness.models.HealthMetric
import com.speckit.wellness.models.HealthResult
import com.speckit.wellness.models.HeartRateMeasurement

/**
 * T019: Platform-specific health data provider using expect/actual pattern.
 * 
 * This interface defines the contract for accessing health data on different platforms.
 * - iOS: Implemented using HealthKit
 * - Android: Implemented using Health Connect
 * 
 * All methods are suspending functions for asynchronous operations.
 */
interface HealthDataProvider {
    /**
     * T020: Request authorization to access health data.
     * 
     * On iOS: Prompts user with HealthKit authorization dialog.
     * On Android: Requests Health Connect permissions.
     * 
     * @return Success(true) if authorized, PermissionDenied if user denies access
     */
    suspend fun requestAuthorization(): HealthResult<Boolean>
    
    /**
     * T021: Fetch step count data for a given time period.
     * 
     * @param startDate Unix timestamp (milliseconds) for the start of the period
     * @param endDate Unix timestamp (milliseconds) for the end of the period
     * @return Success with HealthMetric containing step count, or appropriate error
     */
    suspend fun fetchStepCount(startDate: Long, endDate: Long): HealthResult<HealthMetric>
    
    /**
     * T319: Fetch heart rate data for a given time period.
     * 
     * @param startDate Unix timestamp (milliseconds) for the start of the period
     * @param endDate Unix timestamp (milliseconds) for the end of the period
     * @return Success with list of HeartRateMeasurement, or appropriate error
     */
    suspend fun fetchHeartRate(startDate: Long, endDate: Long): HealthResult<List<HeartRateMeasurement>>
}

/**
 * Factory function to create platform-specific HealthDataProvider.
 */
expect fun createHealthDataProvider(): HealthDataProvider
