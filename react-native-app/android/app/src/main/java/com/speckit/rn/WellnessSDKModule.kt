package com.speckit.rn

import android.content.Context
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeMap
import com.speckit.wellness.HealthDataProviderContext
import com.speckit.wellness.HealthDataRepository
import com.speckit.wellness.createHealthDataProvider
import com.speckit.wellness.models.HealthResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * React Native native module bridging KMP SDK to JavaScript.
 * Provides Health Connect integration via the KMP SDK.
 */
class WellnessSDKModule(reactContext: ReactApplicationContext) : 
    ReactContextBaseJavaModule(reactContext) {
    
    private val healthRepository: HealthDataRepository
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    
    init {
        // Set context for Android HealthDataProvider
        HealthDataProviderContext.context = reactContext.applicationContext
        
        // Initialize KMP SDK repository
        val provider = createHealthDataProvider()
        healthRepository = HealthDataRepository(provider)
    }
    
    override fun getName(): String {
        return "WellnessSDK"
    }
    
    @ReactMethod
    fun requestPermissions(promise: Promise) {
        coroutineScope.launch {
            when (val result = healthRepository.requestPermissions()) {
                is HealthResult.Success -> {
                    promise.resolve(result.data)
                }
                is HealthResult.PermissionDenied -> {
                    promise.reject(
                        "PERMISSION_DENIED",
                        "Health Connect permissions were denied"
                    )
                }
                is HealthResult.DataNotAvailable -> {
                    promise.reject(
                        "DATA_NOT_AVAILABLE",
                        "Health Connect is not available. Please install Health Connect from the Play Store."
                    )
                }
                is HealthResult.UnsupportedPlatform -> {
                    promise.reject(
                        "UNSUPPORTED_PLATFORM",
                        "This platform is not supported"
                    )
                }
                is HealthResult.UnknownError -> {
                    promise.reject(
                        "UNKNOWN_ERROR",
                        result.message
                    )
                }
            }
        }
    }
    
    @ReactMethod
    fun fetchStepCount(startDate: Double, endDate: Double, promise: Promise) {
        coroutineScope.launch {
            // Convert JavaScript timestamp (milliseconds) to KMP SDK Long format
            val startTimestamp = startDate.toLong()
            val endTimestamp = endDate.toLong()
            
            when (val result = healthRepository.getStepCount(startTimestamp, endTimestamp)) {
                is HealthResult.Success -> {
                    val metric = result.data
                    val response: WritableMap = WritableNativeMap().apply {
                        putString("type", metric.type)
                        putDouble("value", metric.value)
                        putString("unit", metric.unit)
                        putDouble("timestamp", metric.timestamp.toDouble())
                        putString("source", metric.source)
                    }
                    promise.resolve(response)
                }
                is HealthResult.PermissionDenied -> {
                    promise.reject(
                        "PERMISSION_DENIED",
                        "Health Connect permissions were denied. Please grant permissions in Settings."
                    )
                }
                is HealthResult.DataNotAvailable -> {
                    promise.reject(
                        "DATA_NOT_AVAILABLE",
                        "No step count data available for the specified time range"
                    )
                }
                is HealthResult.UnsupportedPlatform -> {
                    promise.reject(
                        "UNSUPPORTED_PLATFORM",
                        "This platform is not supported"
                    )
                }
                is HealthResult.UnknownError -> {
                    promise.reject(
                        "UNKNOWN_ERROR",
                        result.message
                    )
                }
            }
        }
    }
}
