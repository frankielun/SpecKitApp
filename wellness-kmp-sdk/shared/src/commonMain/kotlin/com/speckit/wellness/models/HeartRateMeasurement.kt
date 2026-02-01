package com.speckit.wellness.models

/**
 * Heart rate measurement data
 * 
 * @property beatsPerMinute The heart rate in beats per minute
 * @property timestamp The Unix timestamp (milliseconds) when the measurement was taken
 * @property source The data source (e.g., "HealthKit", "HealthConnect")
 */
data class HeartRateMeasurement(
    val beatsPerMinute: Double,
    val timestamp: Long,
    val source: String
) {
    override fun toString(): String {
        return "HeartRateMeasurement(bpm=$beatsPerMinute, timestamp=$timestamp, source=$source)"
    }
}
