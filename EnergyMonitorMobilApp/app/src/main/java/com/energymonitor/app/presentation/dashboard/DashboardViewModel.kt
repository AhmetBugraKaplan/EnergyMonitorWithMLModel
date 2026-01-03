package com.energymonitor.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.energymonitor.app.domain.model.EnergyStatus
import com.energymonitor.app.domain.model.Result
import com.energymonitor.app.domain.repository.EnergyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.delay

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: EnergyRepository
) : ViewModel() {

    private val _state = MutableStateFlow<EnergyStatus?>(null)
    val state: StateFlow<EnergyStatus?> = _state.asStateFlow()

    init {
        startDataRefresh()
    }

    private fun startDataRefresh() {
        viewModelScope.launch {
            while (true) {
                // Her iki veriyi de paralel veya sıralı çekebiliriz
                loadCurrentStatus()
                loadCostData()
                
                // 3 saniye bekle ve tekrar çek (Canlı izleme hissi için)
                delay(3000)
            }
        }
    }

    private suspend fun loadCurrentStatus() {
        when (val result = repository.getCurrentStatus()) {
            is Result.Success -> {
                val data = result.data
                // Mevcut cost verilerini koru
                val currentCost = _state.value?.cost ?: 0.0
                val currentPeak = _state.value?.peakCost ?: 0.0
                val currentOffPeak = _state.value?.offPeakCost ?: 0.0
                
                // Geçmiş verileri güncelle
                val currentHistory = _state.value?.consumptionHistory?.toMutableList() ?: mutableListOf()
                currentHistory.add(data.currentKwh.toFloat())
                if (currentHistory.size > 20) currentHistory.removeAt(0)

                val costHistory = _state.value?.costHistory?.toMutableList() ?: mutableListOf()
                // Maliyet calculated ise onu ekle, yoksa 0 ekle (ama loadCostData ezebilir, dikkat)
                // Burada basitlik adına currentCost'u ekleyelim.
                costHistory.add(currentCost.toFloat())
                if (costHistory.size > 20) costHistory.removeAt(0)

                _state.value = EnergyStatus(
                    currentKwh = data.currentKwh,
                    cost = currentCost,
                    peakCost = currentPeak,
                    offPeakCost = currentOffPeak,
                    status = data.status,
                    timestamp = data.timestamp,
                    consumptionHistory = currentHistory,
                    costHistory = costHistory
                )
            }
            is Result.Error -> {}
            is Result.Loading -> {}
        }
    }

    private suspend fun loadCostData() {
        when (val result = repository.getCostAnalysis()) {
            is Result.Success -> {
                val costData = result.data
                _state.value = _state.value?.copy(
                    cost = costData.totalCost,
                    peakCost = costData.peakCost,
                    offPeakCost = costData.offPeakCost
                )
            }
            is Result.Error -> {}
            is Result.Loading -> {}
        }
    }
}
