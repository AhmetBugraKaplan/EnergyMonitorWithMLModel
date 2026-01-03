package com.energymonitor.app.presentation.simulation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SimulationScreen(
    viewModel: SimulationViewModel = hiltViewModel()
) {
    val powerFactor by viewModel.powerFactor.collectAsState()
    val reactivePower by viewModel.reactivePower.collectAsState()
    val loadType by viewModel.loadType.collectAsState()
    val message by viewModel.message.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Manuel Simülasyon",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Güç Faktörü Slider (Lagging_Current_Power_Factor)
        Text(text = "Lagging_Current_Power_Factor: %${String.format("%.1f", powerFactor)}")
        Slider(
            value = powerFactor,
            onValueChange = { viewModel.onPowerFactorChange(it) },
            valueRange = 0f..100f // Kullanıcı %50 neden diye sordu, tam aralık yapıyoruz.
        )

        // Reaktif Güç Slider (Lagging_Current_Reactive.Power_kVarh)
        Text(text = "Lagging_Current_Reactive.Power_kVarh: ${String.format("%.1f", reactivePower)}")
        Slider(
            value = reactivePower,
            onValueChange = { viewModel.onReactivePowerChange(it) },
            valueRange = 0f..100f
        )

        // Yük Tipi Radio Buttons (Load_Type)
        Text(text = "Load_Type")
        LoadTypeRadioButton("Light_Load", "Düşük", loadType) { viewModel.onLoadTypeChange(it) }
        LoadTypeRadioButton("Medium_Load", "Orta", loadType) { viewModel.onLoadTypeChange(it) }
        LoadTypeRadioButton("Maximum_Load", "Maksimum", loadType) { viewModel.onLoadTypeChange(it) }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.applySimulation() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Uygula ve Gönder")
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = if (message.contains("Hata")) Color.Red else Color.Green,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun LoadTypeRadioButton(
    value: String,
    label: String,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        RadioButton(
            selected = (value == selectedValue),
            onClick = { onSelect(value) }
        )
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}
