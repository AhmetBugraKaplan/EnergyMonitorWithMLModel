package com.energymonitor.app.presentation.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.energymonitor.app.data.remote.dto.AlertDto
import com.energymonitor.app.domain.model.Result
import com.energymonitor.app.domain.repository.EnergyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repository: EnergyRepository
) : ViewModel() {

    private val _state = MutableStateFlow<List<AlertDto>>(emptyList())
    val state: StateFlow<List<AlertDto>> = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        startAlertsRefresh()
    }

    private fun startAlertsRefresh() {
        viewModelScope.launch {
            while (true) {
                // Her 3 saniyede bir uyarıları güncelle
                loadAlerts()
                kotlinx.coroutines.delay(3000)
            }
        }
    }

    private suspend fun loadAlerts() {
        _isLoading.value = true
        when (val result = repository.getAlerts()) {
            is Result.Success -> {
                _state.value = result.data
            }
            is Result.Error -> {
                // Hata durumu
            }
            is Result.Loading -> {}
        }
        _isLoading.value = false
    }
}
