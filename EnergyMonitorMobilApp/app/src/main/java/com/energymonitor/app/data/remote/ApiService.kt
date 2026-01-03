package com.energymonitor.app.data.remote

import com.energymonitor.app.data.remote.dto.AlertDto
import com.energymonitor.app.data.remote.dto.CostAnalysisDto
import com.energymonitor.app.data.remote.dto.CurrentStatusDto
import retrofit2.http.GET
import retrofit2.http.Query

import com.energymonitor.app.data.remote.dto.SimulationBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/update-simulation")
    suspend fun updateSimulation(@Body body: SimulationBody)

    @GET("api/current-status")
    suspend fun getCurrentStatus(): CurrentStatusDto

    @GET("api/cost-analysis")
    suspend fun getCostAnalysis(
        @Query("period") period: String = "today"
    ): CostAnalysisDto

    @GET("api/alerts")
    suspend fun getAlerts(): List<AlertDto>
}
