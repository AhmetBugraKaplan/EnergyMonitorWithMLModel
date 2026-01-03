package com.energymonitor.app.domain.repository

import com.energymonitor.app.data.remote.dto.AlertDto
import com.energymonitor.app.data.remote.dto.CostAnalysisDto
import com.energymonitor.app.data.remote.dto.CurrentStatusDto
import com.energymonitor.app.domain.model.Result

interface EnergyRepository {
    suspend fun getCurrentStatus(): Result<CurrentStatusDto>
    suspend fun getCostAnalysis(): Result<CostAnalysisDto>
    suspend fun getAlerts(): Result<List<AlertDto>>
    suspend fun updateSimulation(reactivePower: Double, powerFactor: Double, loadType: String): Result<Unit>
}
