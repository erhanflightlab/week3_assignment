package com.example.week3.ui

import androidx.lifecycle.viewModelScope
import com.example.week3.data.HomeDeviceCatalog
import com.example.week3.data.HomeRoom
import com.example.week3.data.InMemoryDeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EnergyDashboardViewModelTest {
    @Test
    fun startsWithTenDevicesAnd106Watts() = runTest {
        withDashboard { console ->
            val state = console.uiState.value
            assertFalse(state.isLoading)
            assertEquals(10, state.visibleDevices.size)
            assertEquals(9, state.onlineCount)
            assertEquals(6, state.activeOnlineCount)
            assertEquals(106.0, state.totalWatts, 0.0)
        }
    }

    @Test
    fun combinesRoomAndTurkishNameSearchWithoutChangingPowerTotal() = runTest {
        withDashboard { console ->
            console.changeQuery("  LAMBASI  ")
            assertEquals("  LAMBASI  ", console.query.value)
            runCurrent()
            assertEquals(listOf(1, 10), console.uiState.value.visibleDevices.map { it.device.id })

            console.selectRoom(HomeRoom.SALON)
            runCurrent()
            assertEquals(listOf(1), console.uiState.value.visibleDevices.map { it.device.id })
            assertEquals(106.0, console.uiState.value.totalWatts, 0.0)
            assertEquals("27 W", console.uiState.value.selectedRoomLoadLabel)

            console.selectRoom(HomeRoom.BALKON)
            runCurrent()
            assertTrue(console.uiState.value.visibleDevices.isEmpty())
            assertEquals(106.0, console.uiState.value.totalWatts, 0.0)
            assertEquals("0 W", console.uiState.value.selectedRoomLoadLabel)
        }
    }

    @Test
    fun switchesOnlineDeviceAndUpdatesTheWholeHomeTotal() = runTest {
        withDashboard { console ->
            console.selectRoom(HomeRoom.SALON)
            console.toggleDevice(2)
            runCurrent()
            assertTrue(console.uiState.value.visibleDevices.first { it.device.id == 2 }.device.isActive)
            assertEquals(166.0, console.uiState.value.totalWatts, 0.0)
            assertEquals("87 W", console.uiState.value.selectedRoomLoadLabel)

            console.toggleDevice(2)
            runCurrent()
            assertFalse(console.uiState.value.visibleDevices.first { it.device.id == 2 }.device.isActive)
            assertEquals(106.0, console.uiState.value.totalWatts, 0.0)
            assertEquals("27 W", console.uiState.value.selectedRoomLoadLabel)
        }
    }

    @Test
    fun excludesOfflineDevicesEvenIfTheirLastReportedStateWasActive() = runTest {
        val devices = HomeDeviceCatalog.devices.map { device ->
            if (device.id == 9) device.copy(isActive = true) else device
        }
        withDashboard(InMemoryDeviceRepository(devices)) { console ->
            assertEquals(106.0, console.uiState.value.totalWatts, 0.0)
            assertEquals(6, console.uiState.value.activeOnlineCount)
        }
    }

    @Test
    fun rejectsOfflineToggle() = runTest {
        withDashboard { console ->
            console.selectRoom(HomeRoom.BALKON)
            console.toggleDevice(9)
            runCurrent()
            assertFalse(console.uiState.value.visibleDevices.single().device.isActive)
            assertEquals(106.0, console.uiState.value.totalWatts, 0.0)
        }
    }

    private fun TestScope.withDashboard(
        repository: InMemoryDeviceRepository = InMemoryDeviceRepository(),
        assertions: TestScope.(EnergyDashboardViewModel) -> Unit
    ) {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val console = EnergyDashboardViewModel(repository, dispatcher)
        val collector = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            console.uiState.collect {}
        }
        try {
            runCurrent()
            assertions(console)
        } finally {
            collector.cancel()
            console.viewModelScope.cancel()
            Dispatchers.resetMain()
        }
    }
}
