package com.example.week3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.week3.ui.EvNabziTheme
import com.example.week3.ui.EnergyDashboard
import com.example.week3.ui.EnergyDashboardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, 0xFFF6EDE2.toInt()),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, 0xFFF6EDE2.toInt())
        )
        setContent {
            EvNabziTheme {
                val dashboard: EnergyDashboardViewModel = viewModel()
                val uiState by dashboard.uiState.collectAsStateWithLifecycle()
                val query by dashboard.query.collectAsStateWithLifecycle()
                val roomMenuOpen by dashboard.isRoomMenuOpen.collectAsStateWithLifecycle()

                EnergyDashboard(
                    state = uiState,
                    query = query,
                    roomMenuOpen = roomMenuOpen,
                    onRoomMenuChange = dashboard::setRoomMenuOpen,
                    onQueryChange = dashboard::changeQuery,
                    onRoomSelected = dashboard::selectRoom,
                    onDeviceToggle = dashboard::toggleDevice
                )
            }
        }
    }
}
