package com.speckit.wellness

import com.speckit.wellness.models.HealthMetric
import com.speckit.wellness.models.HealthResult
import com.speckit.wellness.models.HeartRateMeasurement
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.HealthKit.*
import platform.Foundation.*
import kotlin.coroutines.resume

/**
 * T035: iOS implementation of HealthDataProvider using HealthKit.
 */
private class IosHealthDataProvider : HealthDataProvider {
    
    private val healthStore = HKHealthStore()
    
    /**
     * T037-T038: Request authorization to access HealthKit data.
     */
    override suspend fun requestAuthorization(): HealthResult<Boolean> {
        // T038: Define the data types we want to read
        val stepCountType = HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount)
            ?: return HealthResult.UnknownError("Step count type not available")
        
        val heartRateType = HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate)
            ?: return HealthResult.UnknownError("Heart rate type not available")
        
        val typesToRead = setOf(stepCountType, heartRateType)
        
        // T037: Request authorization using suspendCancellableCoroutine (T043)
        return suspendCancellableCoroutine { continuation ->
            healthStore.requestAuthorizationToShareTypes(
                typesToShare = null,
                readTypes = NSSet.setWithSet(typesToRead as Set<*>)
            ) { success, error ->
                when {
                    error != null -> {
                        // T042: Map authorization errors
                        continuation.resume(
                            HealthResult.PermissionDenied(
                                "HealthKit authorization denied: ${error.localizedDescription}"
                            )
                        )
                    }
                    success -> {
                        continuation.resume(HealthResult.Success(true))
                    }
                    else -> {
                        continuation.resume(HealthResult.PermissionDenied())
                    }
                }
            }
        }
    }
    
    /**
     * T039-T043: Fetch step count data using HKStatisticsQuery.
     */
    override suspend fun fetchStepCount(startDate: Long, endDate: Long): HealthResult<HealthMetric> {
        val stepCountType = HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount)
            ?: return HealthResult.UnsupportedPlatform("Step count type not available on this device")
        
        // T040: Convert Unix timestamps to NSDate
        val startNSDate = NSDate.dateWithTimeIntervalSince1970(startDate / 1000.0)
        val endNSDate = NSDate.dateWithTimeIntervalSince1970(endDate / 1000.0)
        
        val predicate = HKQuery.predicateForSamplesWithStartDate(
            startDate = startNSDate,
            endDate = endNSDate,
            options = HKQueryOptionNone
        )
        
        // T039: Use HKStatisticsQuery with cumulativeSum option
        return suspendCancellableCoroutine { continuation ->
            val query = HKStatisticsQuery(
                quantityType = stepCountType,
                quantitySamplePredicate = predicate,
                options = HKStatisticsOptionCumulativeSum
            ) { _, statistics, error ->
                when {
                    error != null -> {
                        // T042: Map HealthKit errors
                        val errorCode = error.code
                        when (errorCode) {
                            HKErrorAuthorizationDenied, HKErrorAuthorizationNotDetermined -> {
                                continuation.resume(
                                    HealthResult.PermissionDenied(
                                        "HealthKit permission denied. Please enable in Settings."
                                    )
                                )
                            }
                            else -> {
                                continuation.resume(
                                    HealthResult.UnknownError(
                                        "HealthKit error: ${error.localizedDescription}"
                                    )
                                )
                            }
                        }
                    }
                    statistics == null -> {
                        // T042: No data available
                        continuation.resume(
                            HealthResult.DataNotAvailable(
                                "No step count data available for the specified period"
                            )
                        )
                    }
                    else -> {
                        // T041: Extract step count from HKQuantity
                        val sum = statistics.sumQuantity()
                        if (sum == null) {
                            continuation.resume(
                                HealthResult.DataNotAvailable(
                                    "No step count data recorded for this period"
                                )
                            )
                        } else {
                            val stepCount = sum.doubleValueForUnit(HKUnit.countUnit())
                            
                            val metric = HealthMetric(
                                type = "step_count",
                                value = stepCount,
                                unit = "steps",
                                timestamp = endDate, // Use end date as the reference timestamp
                                source = "ios"
                            )
                            
                            continuation.resume(HealthResult.Success(metric))
                        }
                    }
                }
            }
            
            // Execute the query
            healthStore.executeQuery(query)
            
            // T043: Handle cancellation
            continuation.invokeOnCancellation {
                healthStore.stopQuery(query)
            }
        }
    }
    
    /**
     * T321-T324: Fetch heart rate data using HKSampleQuery.
     */
    override suspend fun fetchHeartRate(startDate: Long, endDate: Long): HealthResult<List<HeartRateMeasurement>> {
        val heartRateType = HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate)
            ?: return HealthResult.UnsupportedPlatform("Heart rate type not available on this device")
        
        // T322: Convert Unix timestamps to NSDate
        val startNSDate = NSDate.dateWithTimeIntervalSince1970(startDate / 1000.0)
        val endNSDate = NSDate.dateWithTimeIntervalSince1970(endDate / 1000.0)
        
        val predicate = HKQuery.predicateForSamplesWithStartDate(
            startDate = startNSDate,
            endDate = endNSDate,
            options = HKQueryOptionStrictStartDate
        )
        
        // T322: Use HKSampleQuery to fetch individual heart rate samples
        return suspendCancellableCoroutine { continuation ->
            val query = HKSampleQuery(
                sampleType = heartRateType,
                predicate = predicate,
                limit = HKObjectQueryNoLimit.toULong(),
                sortDescriptors = listOf(
                    NSSortDescriptor(key = HKSampleSortIdentifierStartDate, ascending = true)
                )
            ) { _, samples, error ->
                when {
                    error != null -> {
                        val errorCode = error.code
                        when (errorCode) {
                            HKErrorAuthorizationDenied, HKErrorAuthorizationNotDetermined -> {
                                continuation.resume(
                                    HealthResult.PermissionDenied(
                                        "HealthKit permission denied for heart rate. Please enable in Settings."
                                    )
                                )
                            }
                            else -> {
                                continuation.resume(
                                    HealthResult.UnknownError(
                                        "HealthKit error: ${error.localizedDescription}"
                                    )
                                )
                            }
                        }
                    }
                    samples == null || samples.isEmpty() -> {
                        // T324: No data available - return empty list
                        continuation.resume(HealthResult.Success(emptyList()))
                    }
                    else -> {
                        // T323: Extract heart rate values (bpm) from samples
                        val measurements = samples.mapNotNull { sample ->
                            (sample as? HKQuantitySample)?.let { quantitySample ->
                                val bpm = quantitySample.quantity.doubleValueForUnit(
                                    HKUnit.countUnit().unitDividedByUnit(HKUnit.minuteUnit())
                                )
                                val timestamp = (quantitySample.startDate.timeIntervalSince1970 * 1000).toLong()
                                
                                HeartRateMeasurement(
                                    beatsPerMinute = bpm,
                                    timestamp = timestamp,
                                    source = "HealthKit"
                                )
                            }
                        }
                        
                        continuation.resume(HealthResult.Success(measurements))
                    }
                }
            }
            
            // Execute the query
            healthStore.executeQuery(query)
            
            // Handle cancellation
            continuation.invokeOnCancellation {
                healthStore.stopQuery(query)
            }
        }
    }
}

/**
 * Factory function to create iOS HealthDataProvider.
 */
actual fun createHealthDataProvider(): HealthDataProvider = IosHealthDataProvider()
