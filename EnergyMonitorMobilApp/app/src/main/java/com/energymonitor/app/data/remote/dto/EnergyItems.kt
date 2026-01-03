package com.energymonitor.app.data.remote.dto

import com.google.gson.annotations.SerializedName

// GET /api/current-status
data class CurrentStatusDto(
    @SerializedName("current_kwh")
    val currentKwh: Double,
    
    @SerializedName("predicted_kwh")
    val predictedKwh: Double,
    
    val difference: Double,
    
    val status: String, // "Normal", "High", "Critical"
    
    val timestamp: String
)

// GET /api/cost-analysis
data class CostAnalysisDto(
    @SerializedName("total_cost")
    val totalCost: Double,
    
    @SerializedName("peak_cost")
    val peakCost: Double,
    
    @SerializedName("off_peak_cost")
    val offPeakCost: Double
)

// GET /api/alerts
data class AlertDto(
    val priority: String, // "High", "Medium", "Low"
    val title: String,
    val message: String,
    val recommendation: String
)

data class SimulationBody(
    @SerializedName("reactive_power")
    val reactivePower: Double,
    
    @SerializedName("power_factor")
    val powerFactor: Double,
    
    @SerializedName("load_type")
    val loadType: String
)
