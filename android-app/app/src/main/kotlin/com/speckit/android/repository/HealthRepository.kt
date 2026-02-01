package com.speckit.android.repository

import android.content.Context
import com.speckit.wellness.HealthDataRepository
import com.speckit.wellness.HealthDataProviderContext
import com.speckit.wellness.createHealthDataProvider
import com.speckit.wellness.models.HealthMetric
import com.speckit.wellness.models.HealthResult
import java.time.LocalDate
import java.time.ZoneId

/**
 * Android-specific wrapper for the KMP SDK HealthDataRepository.
 * Handles Health Connect availability and provides domain-specific error mapping.
 */
class HealthRepository(context: Context) {
    
    private val repository: HealthDataRepository
    
    init {
        // Set context for Android HealthDataProvider
        HealthDataProviderContext.context = context.applicationContext
        
        // Create KMP SDK repository
        val provider = createHealthDataProvider()
        repository = HealthDataRepository(provider)
    }
    
    /**
     * Requests health data permissions from the user.
     * @return Result indicating success or failure with error message
     */
    suspend fun requestPermissions(): Result<Boolean> {
        return when (val result = repository.requestPermissions()) {
            is HealthResult.Success -> Result.success(result.data)
            is HealthResult.PermissionDenied -> 
                Result.failure(Exception("Health Connect permissions are required to access step count data"))
            is HealthResult.DataNotAvailable -> 
                Result.failure(Exception("Health Connect is not installed on this device"))
            is HealthResult.UnsupportedPlatform -> 
                Result.failure(Exception("This platform is not supported"))
            is HealthResult.UnknownError -> 
                Result.failure(Exception("An unknown error occurred: ${result.message}"))
        }
    }
    
    /**
     * Fetches step count for today.
     * @return Result containing step count or error message
     */
    suspend fun fetchTodayStepCount(): Result<HealthMetric> {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault())
        val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault())
        
        val startTimestamp = startOfDay.toInstant().toEpochMilli()
        val endTimestamp = endOfDay.toInstant().toEpochMilli()
        
        return fetchStepCount(startTimestamp, endTimestamp)
    }
    
    /**
     * Fetches step count for a specific time range.
     * @param startTimestamp Start time in milliseconds since epoch
     * @param endTimestamp End time in milliseconds since epoch
     * @return Result containing HealthMetric or error message
     */
    suspend fun fetchStepCount(startTimestamp: Long, endTimestamp: Long): Result<HealthMetric> {
        return when (val result = repository.getStepCount(startTimestamp, endTimestamp)) {
            is HealthResult.Success -> Result.success(result.data)
            is HealthResult.PermissionDenied -> 
                Result.failure(Exception("Health Connect permissions are denied. Please grant permissions to view step count."))
            is HealthResult.DataNotAvailable -> 
                Result.failure(Exception("Health Connect is not available. Please install Health Connect from the Play Store."))
            is HealthResult.UnsupportedPlatform -> 
                Result.failure(Exception("This platform is not supported"))
            is HealthResult.UnknownError -> 
                Result.failure(Exception("Failed to fetch step count: ${result.message}"))
        }
    }
}
