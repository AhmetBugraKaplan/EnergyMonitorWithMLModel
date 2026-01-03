package com.energymonitor.app.presentation.cost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.energymonitor.app.data.remote.dto.CostAnalysisDto
import com.energymonitor.app.domain.model.Result
import com.energymonitor.app.domain.repository.EnergyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CostViewModel @Inject constructor(
    private val repository: EnergyRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CostAnalysisDto?>(null)
    val state: StateFlow<CostAnalysisDto?> = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCostData()
    }

    private fun loadCostData() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getCostAnalysis()) {
                is Result.Success -> {
                    _state.value = result.data
                }
                is Result.Error -> {
                    // Hata yönetimi
                }
                is Result.Loading -> {}
            }
            _isLoading.value = false
        }
    }
}
