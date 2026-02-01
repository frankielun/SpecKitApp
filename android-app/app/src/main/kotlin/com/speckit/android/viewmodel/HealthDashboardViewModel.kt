package com.speckit.android.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.speckit.android.repository.HealthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Health Dashboard screen.
 * Manages step count data, loading states, and error messages.
 */
class HealthDashboardViewModel(
    private val healthRepository: HealthRepository
) : ViewModel() {
    
    private val _stepCount = MutableStateFlow<Int?>(null)
    val stepCount: StateFlow<Int?> = _stepCount.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Fetches today's step count from Health Connect via KMP SDK.
     */
    fun fetchStepCount() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            healthRepository.fetchTodayStepCount()
                .onSuccess { metric ->
                    _stepCount.value = metric.value.toInt()
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Unknown error occurred"
                    _isLoading.value = false
                }
        }
    }
    
    /**
     * Requests Health Connect permissions.
     */
    fun requestPermissions() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            healthRepository.requestPermissions()
                .onSuccess { granted ->
                    if (granted) {
                        // Permissions granted, fetch data
                        fetchStepCount()
                    } else {
                        _errorMessage.value = "Permissions not granted"
                        _isLoading.value = false
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Failed to request permissions"
                    _isLoading.value = false
                }
        }
    }
}

/**
 * Factory for creating HealthDashboardViewModel with dependencies.
 */
class HealthDashboardViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthDashboardViewModel::class.java)) {
            val repository = HealthRepository(context)
            return HealthDashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
