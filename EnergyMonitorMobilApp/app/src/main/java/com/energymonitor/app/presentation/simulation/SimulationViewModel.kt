package com.energymonitor.app.presentation.simulation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.energymonitor.app.domain.repository.EnergyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimulationViewModel @Inject constructor(
    private val repository: EnergyRepository
) : ViewModel() {

    // Slider değerleri
    private val _powerFactor = MutableStateFlow(98f)
    val powerFactor: StateFlow<Float> = _powerFactor.asStateFlow()

    private val _reactivePower = MutableStateFlow(5f)
    val reactivePower: StateFlow<Float> = _reactivePower.asStateFlow()

    private val _loadType = MutableStateFlow("Medium_Load")
    val loadType: StateFlow<String> = _loadType.asStateFlow()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    fun onPowerFactorChange(value: Float) {
        _powerFactor.value = value
    }

    fun onReactivePowerChange(value: Float) {
        _reactivePower.value = value
    }

    fun onLoadTypeChange(value: String) {
        _loadType.value = value
    }

    fun applySimulation() {
        viewModelScope.launch {
            _message.value = "Güncelleniyor..."
            val result = repository.updateSimulation(
                reactivePower = _reactivePower.value.toDouble(),
                powerFactor = _powerFactor.value.toDouble(),
                loadType = _loadType.value
            )
            
            _message.value = if (result is com.energymonitor.app.domain.model.Result.Success) {
                "Başarılı! Dashboard'a bakın."
            } else {
                "Hata oluştu!"
            }
        }
    }
}
