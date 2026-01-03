package com.energymonitor.app.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.energymonitor.app.presentation.navigation.Screen
import com.energymonitor.app.presentation.dashboard.DashboardScreen
import com.energymonitor.app.presentation.alerts.AlertsScreen

import com.energymonitor.app.presentation.simulation.SimulationScreen
import com.energymonitor.app.presentation.charts.ChartsScreen
import androidx.compose.material.icons.filled.ShowChart

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    val items = listOf(
        Screen.Dashboard,
        Screen.Alerts,
        Screen.Charts,
        Screen.Simulation
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            when(screen) {
                                Screen.Dashboard -> Icon(Icons.Default.Home, contentDescription = null)
                                Screen.Alerts -> Icon(Icons.Default.Warning, contentDescription = null)
                                Screen.Charts -> Icon(Icons.Default.ShowChart, contentDescription = null)
                                Screen.Simulation -> Icon(Icons.Default.Settings, contentDescription = null)
                            }
                        },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Alerts.route) { AlertsScreen() }
            composable(Screen.Charts.route) { ChartsScreen() }
            composable(Screen.Simulation.route) { SimulationScreen() }
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}
