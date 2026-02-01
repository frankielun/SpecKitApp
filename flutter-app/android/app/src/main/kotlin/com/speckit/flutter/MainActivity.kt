package com.speckit.flutter

import android.content.Context
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import com.speckit.wellness.HealthDataProviderContext
import com.speckit.wellness.HealthDataRepository
import com.speckit.wellness.createHealthDataProvider
import com.speckit.wellness.models.HealthResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : FlutterActivity() {
    private val channelName = "com.speckit.wellness_sdk"
    private lateinit var healthRepository: HealthDataRepository
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // Initialize KMP SDK
        HealthDataProviderContext.context = applicationContext
        val provider = createHealthDataProvider()
        healthRepository = HealthDataRepository(provider)

        // Setup Flutter method channel
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channelName)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "requestPermissions" -> requestPermissions(result)
                    "fetchStepCount" -> fetchStepCount(call, result)
                    else -> result.notImplemented()
                }
            }
    }

    private fun requestPermissions(result: MethodChannel.Result) {
        coroutineScope.launch {
            when (val healthResult = healthRepository.requestPermissions()) {
                is HealthResult.Success -> {
                    result.success(healthResult.data)
                }
                is HealthResult.PermissionDenied -> {
                    result.error(
                        "PERMISSION_DENIED",
                        "Health Connect permissions were denied",
                        null
                    )
                }
                is HealthResult.DataNotAvailable -> {
                    result.error(
                        "DATA_NOT_AVAILABLE",
                        "Health Connect is not available. Please install Health Connect from the Play Store.",
                        null
                    )
                }
                is HealthResult.UnsupportedPlatform -> {
                    result.error(
                        "UNSUPPORTED_PLATFORM",
                        "This platform is not supported",
                        null
                    )
                }
                is HealthResult.UnknownError -> {
                    result.error(
                        "UNKNOWN_ERROR",
                        healthResult.message,
                        null
                    )
                }
            }
        }
    }

    private fun fetchStepCount(call: io.flutter.plugin.common.MethodCall, result: MethodChannel.Result) {
        val startDate = call.argument<Number>("startDate")?.toLong()
        val endDate = call.argument<Number>("endDate")?.toLong()

        if (startDate == null || endDate == null) {
            result.error(
                "INVALID_ARGUMENTS",
                "Missing or invalid arguments",
                null
            )
            return
        }

        coroutineScope.launch {
            when (val healthResult = healthRepository.getStepCount(startDate, endDate)) {
                is HealthResult.Success -> {
                    val metric = healthResult.data
                    val response = mapOf(
                        "type" to metric.type,
                        "value" to metric.value,
                        "unit" to metric.unit,
                        "timestamp" to metric.timestamp.toDouble(),
                        "source" to metric.source
                    )
                    result.success(response)
                }
                is HealthResult.PermissionDenied -> {
                    result.error(
                        "PERMISSION_DENIED",
                        "Health Connect permissions were denied. Please grant permissions in Settings.",
                        null
                    )
                }
                is HealthResult.DataNotAvailable -> {
                    result.error(
                        "DATA_NOT_AVAILABLE",
                        "No step count data available for the specified time range",
                        null
                    )
                }
                is HealthResult.UnsupportedPlatform -> {
                    result.error(
                        "UNSUPPORTED_PLATFORM",
                        "This platform is not supported",
                        null
                    )
                }
                is HealthResult.UnknownError -> {
                    result.error(
                        "UNKNOWN_ERROR",
                        healthResult.message,
                        null
                    )
                }
            }
        }
    }
}
