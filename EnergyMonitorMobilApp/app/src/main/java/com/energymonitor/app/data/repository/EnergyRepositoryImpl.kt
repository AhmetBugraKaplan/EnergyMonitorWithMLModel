package com.energymonitor.app.data.repository

import com.energymonitor.app.data.remote.ApiService
import com.energymonitor.app.data.remote.dto.AlertDto
import com.energymonitor.app.data.remote.dto.CostAnalysisDto
import com.energymonitor.app.data.remote.dto.CurrentStatusDto
import com.energymonitor.app.data.remote.dto.SimulationBody
import com.energymonitor.app.domain.model.Result
import com.energymonitor.app.domain.repository.EnergyRepository
import javax.inject.Inject

class EnergyRepositoryImpl @Inject constructor(
    private val api: ApiService
) : EnergyRepository {

    override suspend fun getCurrentStatus(): Result<CurrentStatusDto> {
        return try {
            val response = api.getCurrentStatus()
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Bir hata oluştu")
        }
    }

    override suspend fun getCostAnalysis(): Result<CostAnalysisDto> {
        return try {
            val response = api.getCostAnalysis()
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Bir hata oluştu")
        }
    }

    override suspend fun getAlerts(): Result<List<AlertDto>> {
        return try {
            val response = api.getAlerts()
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Bir hata oluştu")
        }
    }
    override suspend fun updateSimulation(reactivePower: Double, powerFactor: Double, loadType: String): Result<Unit> {
        return try {
            api.updateSimulation(
                SimulationBody(
                    reactivePower = reactivePower,
                    powerFactor = powerFactor,
                    loadType = loadType
                )
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Bir hata oluştu")
        }
    }
}
