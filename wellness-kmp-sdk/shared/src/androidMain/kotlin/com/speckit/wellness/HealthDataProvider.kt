package com.speckit.wellness

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.speckit.wellness.models.HealthMetric
import com.speckit.wellness.models.HealthResult
import com.speckit.wellness.models.HeartRateMeasurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

/**
 * T047: Android implementation of HealthDataProvider using Health Connect.
 * 
 * @property context Android context required for Health Connect client (T049)
 */
private class AndroidHealthDataProvider(
    private val context: Context // T049
) : HealthDataProvider {
    
    // T050: Initialize Health Connect client
    private val healthConnectClient: HealthConnectClient? by lazy {
        try {
            HealthConnectClient.getOrCreate(context)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * T051-T052: Request authorization to access Health Connect data.
     */
    override suspend fun requestAuthorization(): HealthResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            // T056: Check if Health Connect is available
            val client = healthConnectClient
                ?: return@withContext HealthResult.DataNotAvailable(
                    "Health Connect is not installed on this device. Please install it from the Play Store."
                )
            
            // T052: Define the permissions we need
            val permissions = setOf(
                HealthPermission.getReadPermission(StepsRecord::class),
                HealthPermission.getReadPermission(HeartRateRecord::class)
            )
            
            // T051: Check if permissions are already granted
            val grantedPermissions = client.permissionController.getGrantedPermissions()
            
            val hasPermission = permissions.all { it in grantedPermissions }
            
            if (hasPermission) {
                HealthResult.Success(true)
            } else {
                // Note: Actual permission request requires an Activity context via contract API
                // This method only checks existing permissions. UI layer must handle permission request.
                HealthResult.PermissionDenied(
                    "Health Connect permission not granted. Please grant step count permission in app settings."
                )
            }
        } catch (e: Exception) {
            // T058: Wrap exceptions in UnknownError
            HealthResult.UnknownError(
                "Failed to check Health Connect authorization: ${e.message}",
                cause = e
            )
        }
    }
    
    /**
     * T053-T058: Fetch step count data using Health Connect.
     */
    override suspend fun fetchStepCount(startDate: Long, endDate: Long): HealthResult<HealthMetric> = withContext(Dispatchers.IO) {
        try {
            // T056: Check if Health Connect is available
            val client = healthConnectClient
                ?: return@withContext HealthResult.DataNotAvailable(
                    "Health Connect is not installed on this device"
                )
            
            // T054: Convert timestamps to Instant and create time range filter
            val startTime = Instant.ofEpochMilli(startDate)
            val endTime = Instant.ofEpochMilli(endDate)
            
            val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            
            // T053: Create read request for steps records
            val request = ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = timeRangeFilter
            )
            
            // Execute the query
            val response = client.readRecords(request)
            
            if (response.records.isEmpty()) {
                return@withContext HealthResult.DataNotAvailable(
                    "No step count data available for the specified period"
                )
            }
            
            // T055: Sum all step counts from the records
            val totalSteps = response.records.sumOf { it.count }
            
            val metric = HealthMetric(
                type = "step_count",
                value = totalSteps.toDouble(),
                unit = "steps",
                timestamp = endDate, // Use end date as reference timestamp
                source = "android"
            )
            
            HealthResult.Success(metric)
            
        } catch (securityException: SecurityException) {
            // T058: Handle permission errors
            HealthResult.PermissionDenied(
                "Permission denied. Please grant Health Connect permissions."
            )
        } catch (e: Exception) {
            // T058: Wrap all other exceptions
            HealthResult.UnknownError(
                "Failed to fetch step count from Health Connect: ${e.message}",
                cause = e
            )
        }
    }
    
    /**
     * T325-T328: Fetch heart rate data using Health Connect.
     */
    override suspend fun fetchHeartRate(startDate: Long, endDate: Long): HealthResult<List<HeartRateMeasurement>> = withContext(Dispatchers.IO) {
        try {
            // T326: Check if Health Connect is available
            val client = healthConnectClient
                ?: return@withContext HealthResult.DataNotAvailable(
                    "Health Connect is not installed on this device"
                )
            
            // T326: Convert timestamps to Instant and create time range filter
            val startTime = Instant.ofEpochMilli(startDate)
            val endTime = Instant.ofEpochMilli(endDate)
            
            val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            
            // T325: Create read request for heart rate records
            val request = ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = timeRangeFilter
            )
            
            // Execute the query
            val response = client.readRecords(request)
            
            if (response.records.isEmpty()) {
                // T328: No data - return empty list
                return@withContext HealthResult.Success(emptyList())
            }
            
            // T327: Extract bpm values from response records
            val measurements = response.records.flatMap { record ->
                record.samples.map { sample ->
                    HeartRateMeasurement(
                        beatsPerMinute = sample.beatsPerMinute.toDouble(),
                        timestamp = sample.time.toEpochMilli(),
                        source = "HealthConnect"
                    )
                }
            }
            
            HealthResult.Success(measurements)
            
        } catch (securityException: SecurityException) {
            HealthResult.PermissionDenied(
                "Permission denied for heart rate. Please grant Health Connect permissions."
            )
        } catch (e: Exception) {
            HealthResult.UnknownError(
                "Failed to fetch heart rate from Health Connect: ${e.message}",
                cause = e
            )
        }
    }
}

/**
 * Context holder for Android HealthDataProvider.
 * Must be initialized before using createHealthDataProvider().
 */
object HealthDataProviderContext {
    var context: Context? = null
}

/**
 * Factory function to create Android HealthDataProvider.
 * Requires HealthDataProviderContext.context to be set first.
 */
actual fun createHealthDataProvider(): HealthDataProvider {
    val ctx = HealthDataProviderContext.context
        ?: throw IllegalStateException("HealthDataProviderContext.context must be initialized before creating HealthDataProvider")
    return AndroidHealthDataProvider(ctx)
}
