package com.speckit.wellness.models

import kotlinx.serialization.Serializable

/**
 * T016: Represents a health metric data point.
 * 
 * @property type The type of health metric (e.g., "step_count", "heart_rate")
 * @property value The numeric value of the metric
 * @property unit The unit of measurement (e.g., "steps", "bpm")
 * @property timestamp The Unix timestamp (milliseconds) when this data was recorded
 * @property source The data source platform ("ios" or "android")
 */
@Serializable
data class HealthMetric(
    val type: String,
    val value: Double,
    val unit: String,
    val timestamp: Long,
    val source: String
)
