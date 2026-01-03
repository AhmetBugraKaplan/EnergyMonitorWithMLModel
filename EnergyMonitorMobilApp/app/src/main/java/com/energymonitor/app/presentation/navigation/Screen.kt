package com.energymonitor.app.presentation.navigation

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Ana Sayfa")
    object Alerts : Screen("alerts", "Uyarılar")
    object Simulation : Screen("simulation", "Simülasyon")
    object Charts : Screen("charts", "Grafikler")
}
