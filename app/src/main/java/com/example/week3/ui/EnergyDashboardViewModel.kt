package com.example.week3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.week3.data.DeviceRepository
import com.example.week3.data.HomeRoom
import com.example.week3.data.InMemoryDeviceRepository
import com.example.week3.data.SmartDevice
import java.text.Normalizer
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DeviceListEntry(
    val device: SmartDevice,
    val ratedPowerLabel: String
)

data class EnergyDashboardState(
    val isLoading: Boolean = true,
    val rooms: List<HomeRoom> = HomeRoom.entries,
    val selectedRoom: HomeRoom? = null,
    val selectedRoomLoadLabel: String? = null,
    val visibleDevices: List<DeviceListEntry> = emptyList(),
    val totalWatts: Double = 0.0,
    val totalPowerLabel: String = "—",
    val activeOnlineCount: Int = 0,
    val onlineCount: Int = 0
)

private data class HouseholdReading(
    val dashboard: EnergyDashboardState,
    val roomLoadLabels: Map<HomeRoom, String>
)

class EnergyDashboardViewModel(
    private val repository: DeviceRepository = InMemoryDeviceRepository(),
    private val calculationDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {
    private val queryInput = MutableStateFlow("")
    private val roomSelection = MutableStateFlow<HomeRoom?>(null)
    private val roomMenuOpen = MutableStateFlow(false)
    val isRoomMenuOpen: StateFlow<Boolean> = roomMenuOpen.asStateFlow()

    // Show keystrokes immediately while the list is filtered in the background.
    val query: StateFlow<String> = queryInput.asStateFlow()

    private val unfilteredHomeState = repository.devices.map { devices ->
        val activeOnline = devices.filter { it.isOnline && it.isActive }
        val watts = activeOnline.sumOf { it.watts }
        val formatter = NumberFormat.getNumberInstance(TURKISH).apply {
            maximumFractionDigits = 1
        }

        HouseholdReading(
            dashboard = EnergyDashboardState(
                isLoading = false,
                visibleDevices = devices.map { device ->
                    DeviceListEntry(device, "${formatter.format(device.watts)} W")
                },
                totalWatts = watts,
                totalPowerLabel = formatter.format(watts),
                activeOnlineCount = activeOnline.size,
                onlineCount = devices.count { it.isOnline }
            ),
            roomLoadLabels = activeOnline.groupBy { it.room }.mapValues { (_, devicesInRoom) ->
                "${formatter.format(devicesInRoom.sumOf { it.watts })} W"
            }
        )
    }

    val uiState: StateFlow<EnergyDashboardState> =
        combine(unfilteredHomeState, queryInput, roomSelection) { reading, query, room ->
            val snapshot = reading.dashboard
            val term = query.trim().searchKey()
            val matches = snapshot.visibleDevices.filter { row ->
                val device = row.device
                (room == null || device.room == room) &&
                    (term.isEmpty() || device.name.searchKey().contains(term))
            }
            snapshot.copy(
                selectedRoom = room,
                selectedRoomLoadLabel = room?.let { reading.roomLoadLabels[it] ?: "0 W" },
                visibleDevices = matches
            )
        }
            .flowOn(calculationDispatcher)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = EnergyDashboardState()
            )

    fun changeQuery(value: String) {
        queryInput.value = value
    }

    fun selectRoom(room: HomeRoom?) {
        roomSelection.value = room
        roomMenuOpen.value = false
    }

    fun setRoomMenuOpen(open: Boolean) {
        roomMenuOpen.value = open
    }

    fun toggleDevice(id: Int) {
        viewModelScope.launch(calculationDispatcher) {
            repository.toggleDevice(id)
        }
    }

    private fun String.searchKey(): String =
        Normalizer.normalize(lowercase(TURKISH), Normalizer.Form.NFD)
            .replace(COMBINING_MARKS, "")
            .replace('ı', 'i')

    private companion object {
        val TURKISH: Locale = Locale.forLanguageTag("tr-TR")
        val COMBINING_MARKS = Regex("\\p{M}+")
    }
}
