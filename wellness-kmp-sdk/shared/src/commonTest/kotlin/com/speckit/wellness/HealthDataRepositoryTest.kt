package com.speckit.wellness

import com.speckit.wellness.models.HealthMetric
import com.speckit.wellness.models.HealthResult
import com.speckit.wellness.models.HeartRateMeasurement
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * T028: Unit tests for HealthDataRepository.
 * 
 * These tests use a mock implementation of HealthDataProvider
 * to verify business logic without platform dependencies.
 */
class HealthDataRepositoryTest {
    
    /**
     * T029: Test successful step count fetching.
     */
    @Test
    fun testGetStepCountSuccess() = runTest {
        // Arrange
        val mockProvider = MockHealthDataProvider(
            stepCountResult = HealthResult.Success(
                HealthMetric(
                    type = "step_count",
                    value = 1000.0,
                    unit = "steps",
                    timestamp = 1000L,
                    source = "test"
                )
            )
        )
        val repository = HealthDataRepository(mockProvider)
        
        // Act
        val result = repository.getStepCount(0L, 1000L)
        
        // Assert
        assertTrue(result.isSuccess(), "Expected success result")
        assertEquals(1000.0, (result as HealthResult.Success).data.value)
    }
    
    /**
     * T030: Test permission denied scenario.
     */
    @Test
    fun testGetStepCountPermissionDenied() = runTest {
        // Arrange
        val mockProvider = MockHealthDataProvider(
            stepCountResult = HealthResult.PermissionDenied()
        )
        val repository = HealthDataRepository(mockProvider)
        
        // Act
        val result = repository.getStepCount(0L, 1000L)
        
        // Assert
        assertTrue(result is HealthResult.PermissionDenied, "Expected PermissionDenied result")
    }
    
    /**
     * T031: Test data not available scenario.
     */
    @Test
    fun testGetStepCountDataNotAvailable() = runTest {
        // Arrange
        val mockProvider = MockHealthDataProvider(
            stepCountResult = HealthResult.DataNotAvailable()
        )
        val repository = HealthDataRepository(mockProvider)
        
        // Act
        val result = repository.getStepCount(0L, 1000L)
        
        // Assert
        assertTrue(result is HealthResult.DataNotAvailable, "Expected DataNotAvailable result")
    }
    
    /**
     * T032: Test input validation - startDate > endDate should fail.
     */
    @Test
    fun testGetStepCountInvalidDateRange() = runTest {
        // Arrange
        val mockProvider = MockHealthDataProvider()
        val repository = HealthDataRepository(mockProvider)
        
        // Act
        val result = repository.getStepCount(1000L, 500L)
        
        // Assert
        assertTrue(result is HealthResult.UnknownError, "Expected UnknownError for invalid date range")
        assertTrue(
            (result as HealthResult.UnknownError).message.contains("before"),
            "Error message should mention date ordering"
        )
    }
    
    /**
     * T032: Test input validation - negative timestamps should fail.
     */
    @Test
    fun testGetStepCountNegativeTimestamps() = runTest {
        // Arrange
        val mockProvider = MockHealthDataProvider()
        val repository = HealthDataRepository(mockProvider)
        
        // Act
        val result = repository.getStepCount(-100L, 1000L)
        
        // Assert
        assertTrue(result is HealthResult.UnknownError, "Expected UnknownError for negative timestamp")
        assertTrue(
            (result as HealthResult.UnknownError).message.contains("negative"),
            "Error message should mention negative values"
        )
    }
    
    /**
     * T033: Test requestPermissions success scenario.
     */
    @Test
    fun testRequestPermissionsSuccess() = runTest {
        // Arrange
        val mockProvider = MockHealthDataProvider(
            authResult = HealthResult.Success(true)
        )
        val repository = HealthDataRepository(mockProvider)
        
        // Act
        val result = repository.requestPermissions()
        
        // Assert
        assertTrue(result.isSuccess(), "Expected success result")
        assertEquals(true, (result as HealthResult.Success).data)
    }
    
    /**
     * T033: Test requestPermissions failure scenario.
     */
    @Test
    fun testRequestPermissionsFailure() = runTest {
        // Arrange
        val mockProvider = MockHealthDataProvider(
            authResult = HealthResult.PermissionDenied()
        )
        val repository = HealthDataRepository(mockProvider)
        
        // Act
        val result = repository.requestPermissions()
        
        // Assert
        assertTrue(result is HealthResult.PermissionDenied, "Expected PermissionDenied result")
    }
    
    /**
     * T330: Test successful heart rate fetching.
     */
    @Test
    fun testGetHeartRateSuccess() = runTest {
        // Arrange
        val mockMeasurements = listOf(
            HeartRateMeasurement(beatsPerMinute = 72.0, timestamp = 1000L, source = "test"),
            HeartRateMeasurement(beatsPerMinute = 75.0, timestamp = 2000L, source = "test")
        )
        val mockProvider = MockHealthDataProvider(
            heartRateResult = HealthResult.Success(mockMeasurements)
        )
        val repository = HealthDataRepository(mockProvider)
        
        // Act
        val result = repository.getHeartRate(0L, 3000L)
        
        // Assert
        assertTrue(result.isSuccess(), "Expected success result")
        assertEquals(2, (result as HealthResult.Success).data.size)
        assertEquals(72.0, result.data[0].beatsPerMinute)
        assertEquals(75.0, result.data[1].beatsPerMinute)
    }
    
    /**
     * T331: Test heart rate empty data case.
     */
    @Test
    fun testGetHeartRateEmptyData() = runTest {
        // Arrange
        val mockProvider = MockHealthDataProvider(
            heartRateResult = HealthResult.Success(emptyList())
        )
        val repository = HealthDataRepository(mockProvider)
        
        // Act
        val result = repository.getHeartRate(0L, 1000L)
        
        // Assert
        assertTrue(result.isSuccess(), "Expected success result with empty list")
        assertTrue((result as HealthResult.Success).data.isEmpty(), "Expected empty list")
    }
    
    /**
     * T332: Test heart rate permission denied case.
     */
    @Test
    fun testGetHeartRatePermissionDenied() = runTest {
        // Arrange
        val mockProvider = MockHealthDataProvider(
            heartRateResult = HealthResult.PermissionDenied("Heart rate permission denied")
        )
        val repository = HealthDataRepository(mockProvider)
        
        // Act
        val result = repository.getHeartRate(0L, 1000L)
        
        // Assert
        assertTrue(result is HealthResult.PermissionDenied, "Expected PermissionDenied result")
    }
    
    /**
     * Test heart rate input validation - startDate > endDate should fail.
     */
    @Test
    fun testGetHeartRateInvalidDateRange() = runTest {
        // Arrange
        val mockProvider = MockHealthDataProvider()
        val repository = HealthDataRepository(mockProvider)
        
        // Act
        val result = repository.getHeartRate(1000L, 500L)
        
        // Assert
        assertTrue(result is HealthResult.UnknownError, "Expected UnknownError for invalid date range")
        assertTrue(
            (result as HealthResult.UnknownError).message.contains("before"),
            "Error message should mention date ordering"
        )
    }
    
    /**
     * Test heart rate input validation - negative timestamps should fail.
     */
    @Test
    fun testGetHeartRateNegativeTimestamps() = runTest {
        // Arrange
        val mockProvider = MockHealthDataProvider()
        val repository = HealthDataRepository(mockProvider)
        
        // Act
        val result = repository.getHeartRate(-100L, 1000L)
        
        // Assert
        assertTrue(result is HealthResult.UnknownError, "Expected UnknownError for negative timestamp")
        assertTrue(
            (result as HealthResult.UnknownError).message.contains("negative"),
            "Error message should mention negative values"
        )
    }
}

/**
 * Mock implementation of HealthDataProvider for testing.
 * This allows us to test repository logic without real platform dependencies.
 */
private class MockHealthDataProvider(
    private val authResult: HealthResult<Boolean> = HealthResult.Success(true),
    private val stepCountResult: HealthResult<HealthMetric> = HealthResult.Success(
        HealthMetric("step_count", 0.0, "steps", 0L, "mock")
    ),
    private val heartRateResult: HealthResult<List<HeartRateMeasurement>> = HealthResult.Success(emptyList())
) : HealthDataProvider {
    
    override suspend fun requestAuthorization(): HealthResult<Boolean> {
        return authResult
    }
    
    override suspend fun fetchStepCount(startDate: Long, endDate: Long): HealthResult<HealthMetric> {
        return stepCountResult
    }
    
    override suspend fun fetchHeartRate(startDate: Long, endDate: Long): HealthResult<List<HeartRateMeasurement>> {
        return heartRateResult
    }
}
