package com.speckit.android.viewmodel

import com.speckit.android.repository.HealthRepository
import com.speckit.wellness.models.HealthMetric
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HealthDashboardViewModelTest {
    
    private lateinit var mockRepository: HealthRepository
    private lateinit var viewModel: HealthDashboardViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        viewModel = HealthDashboardViewModel(mockRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `fetchStepCount updates stepCount on success`() = runTest(testDispatcher) {
        // Arrange
        val expectedStepCount = 5000
        val mockMetric = HealthMetric(
            type = "steps",
            value = expectedStepCount.toDouble(),
            unit = "count",
            timestamp = System.currentTimeMillis(),
            source = "Health Connect"
        )
        coEvery { mockRepository.fetchTodayStepCount() } returns Result.success(mockMetric)
        
        // Act
        viewModel.fetchStepCount()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert
        assertEquals(expectedStepCount, viewModel.stepCount.first())
        assertNull(viewModel.errorMessage.first())
        assertFalse(viewModel.isLoading.first())
        coVerify { mockRepository.fetchTodayStepCount() }
    }
    
    @Test
    fun `fetchStepCount updates errorMessage on failure`() = runTest(testDispatcher) {
        // Arrange
        val errorMessage = "Health Connect permissions are denied"
        coEvery { mockRepository.fetchTodayStepCount() } returns Result.failure(Exception(errorMessage))
        
        // Act
        viewModel.fetchStepCount()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert
        assertNull(viewModel.stepCount.first())
        assertNotNull(viewModel.errorMessage.first())
        assertTrue(viewModel.errorMessage.first()!!.contains("permissions"))
        assertFalse(viewModel.isLoading.first())
        coVerify { mockRepository.fetchTodayStepCount() }
    }
    
    @Test
    fun `fetchStepCount sets isLoading during fetch`() = runTest(testDispatcher) {
        // Arrange
        val mockMetric = HealthMetric(
            type = "steps",
            value = 1000.0,
            unit = "count",
            timestamp = System.currentTimeMillis(),
            source = "Health Connect"
        )
        coEvery { mockRepository.fetchTodayStepCount() } returns Result.success(mockMetric)
        
        // Act
        viewModel.fetchStepCount()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert - loading should be false after completion
        assertFalse(viewModel.isLoading.first())
        // Verify the fetch was successful
        assertEquals(1000, viewModel.stepCount.first())
    }
    
    @Test
    fun `requestPermissions calls repository and fetches data on success`() = runTest(testDispatcher) {
        // Arrange
        val mockMetric = HealthMetric(
            type = "steps",
            value = 3000.0,
            unit = "count",
            timestamp = System.currentTimeMillis(),
            source = "Health Connect"
        )
        coEvery { mockRepository.requestPermissions() } returns Result.success(true)
        coEvery { mockRepository.fetchTodayStepCount() } returns Result.success(mockMetric)
        
        // Act
        viewModel.requestPermissions()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert
        assertEquals(3000, viewModel.stepCount.first())
        assertNull(viewModel.errorMessage.first())
        coVerify { mockRepository.requestPermissions() }
        coVerify { mockRepository.fetchTodayStepCount() }
    }
    
    @Test
    fun `requestPermissions handles failure correctly`() = runTest(testDispatcher) {
        // Arrange
        val errorMessage = "Failed to request permissions"
        coEvery { mockRepository.requestPermissions() } returns Result.failure(Exception(errorMessage))
        
        // Act
        viewModel.requestPermissions()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert
        assertNotNull(viewModel.errorMessage.first())
        assertEquals(errorMessage, viewModel.errorMessage.first())
        assertFalse(viewModel.isLoading.first())
        coVerify { mockRepository.requestPermissions() }
    }
    
    @Test
    fun `requestPermissions handles denied permissions`() = runTest(testDispatcher) {
        // Arrange
        coEvery { mockRepository.requestPermissions() } returns Result.success(false)
        
        // Act
        viewModel.requestPermissions()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Assert
        assertNotNull(viewModel.errorMessage.first())
        assertEquals("Permissions not granted", viewModel.errorMessage.first())
        assertFalse(viewModel.isLoading.first())
    }
    
    @Test
    fun `initial state is correct`() {
        // Assert
        assertNull(viewModel.stepCount.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)
    }
}
