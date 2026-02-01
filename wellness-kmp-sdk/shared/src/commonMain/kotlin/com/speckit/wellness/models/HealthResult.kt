package com.speckit.wellness.models

/**
 * T017-T018: Sealed class representing the result of a health data operation.
 * Follows Railway-Oriented Programming pattern for error handling.
 */
sealed class HealthResult<out T> {
    /**
     * Successful operation with result data.
     */
    data class Success<T>(val data: T) : HealthResult<T>()
    
    /**
     * T018: User denied permission to access health data.
     * 
     * @property message User-friendly error message with actionable guidance
     */
    data class PermissionDenied(
        val message: String = "Health data permission denied. Please enable permissions in Settings."
    ) : HealthResult<Nothing>()
    
    /**
     * T018: Requested health data is not available.
     * 
     * @property message Explanation of why data is unavailable
     */
    data class DataNotAvailable(
        val message: String = "Health data not available for the requested time period."
    ) : HealthResult<Nothing>()
    
    /**
     * T018: Operation not supported on this platform.
     * 
     * @property message Platform-specific explanation
     */
    data class UnsupportedPlatform(
        val message: String = "This health data type is not supported on this platform."
    ) : HealthResult<Nothing>()
    
    /**
     * T018: Unknown error occurred during operation.
     * 
     * @property message Error description
     * @property cause Optional underlying exception
     */
    data class UnknownError(
        val message: String = "An unknown error occurred while accessing health data.",
        val cause: Throwable? = null
    ) : HealthResult<Nothing>()
    
    /**
     * Utility: Check if this result is successful.
     */
    fun isSuccess(): Boolean = this is Success
    
    /**
     * Utility: Check if this result is an error.
     */
    fun isError(): Boolean = this !is Success
    
    /**
     * Utility: Get data if successful, null otherwise.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Utility: Get error message if failed, null if successful.
     */
    fun getErrorMessage(): String? = when (this) {
        is PermissionDenied -> message
        is DataNotAvailable -> message
        is UnsupportedPlatform -> message
        is UnknownError -> message
        is Success -> null
    }
}
