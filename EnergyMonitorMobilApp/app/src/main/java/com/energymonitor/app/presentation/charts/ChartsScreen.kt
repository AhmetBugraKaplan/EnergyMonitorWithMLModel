package com.energymonitor.app.presentation.charts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.energymonitor.app.presentation.dashboard.DashboardViewModel
import com.energymonitor.app.ui.theme.StatusGreen
import com.energymonitor.app.ui.theme.StatusRed

@Composable
fun ChartsScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Detaylı Grafikler",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        state?.let { data ->
            // Tüketim Grafiği
            ChartCard(
                title = "Anlık Tüketim (kWh)",
                dataPoints = data.consumptionHistory,
                lineColor = StatusGreen
            )

            // Maliyet Grafiği
            ChartCard(
                title = "Anlık Maliyet (TL)",
                dataPoints = data.costHistory,
                lineColor = StatusRed // Maliyet kırmızı olsun
            )
        } ?: Text("Veri bekleniyor...")
    }
}

@Composable
fun ChartCard(
    title: String,
    dataPoints: List<Float>,
    lineColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (dataPoints.isNotEmpty()) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Biraz daha büyük olsun
                ) {
                    val path = Path()
                    // Dinamik ölçekleme
                    val maxVal = (dataPoints.maxOrNull() ?: 100f).coerceAtLeast(10f) * 1.2f
                    val widthPerPoint = size.width / (dataPoints.size - 1).coerceAtLeast(1)

                    dataPoints.forEachIndexed { index, value ->
                        val x = index * widthPerPoint
                        val y = size.height - (value / maxVal * size.height)
                        
                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 6.dp.toPx())
                    )
                }
            } else {
                Text("Veri yok...", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
