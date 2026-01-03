package com.energymonitor.app.domain.model

data class EnergyStatus(
    val currentKwh: Double,
    val cost: Double,
    val peakCost: Double = 0.0,
    val offPeakCost: Double = 0.0,
    val status: String, // "Normal", "Warning", "Critical"
    val timestamp: String,
    val consumptionHistory: List<Float> = emptyList(),
    val costHistory: List<Float> = emptyList()
)
