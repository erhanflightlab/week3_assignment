package com.example.week3.data

import com.example.week3.data.DeviceCategory.*
import com.example.week3.data.HomeRoom.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface DeviceRepository {
    val devices: StateFlow<List<SmartDevice>>
    fun toggleDevice(id: Int)
}

class InMemoryDeviceRepository(
    initialDevices: List<SmartDevice> = HomeDeviceCatalog.devices
) : DeviceRepository {
    private val inventory = MutableStateFlow(initialDevices.toList())
    override val devices = inventory.asStateFlow()

    override fun toggleDevice(id: Int) {
        inventory.update { devices ->
            devices.map { device ->
                if (device.id == id && device.isOnline) {
                    device.copy(isActive = !device.isActive)
                } else {
                    device
                }
            }
        }
    }
}

object HomeDeviceCatalog {
    val devices: List<SmartDevice> = listOf(
        SmartDevice(1, "Köşe Lambası", LIGHT, SALON, isOnline = true, isActive = true, watts = 15.0),
        SmartDevice(2, "Tavan Avizesi", LIGHT, SALON, isOnline = true, isActive = false, watts = 60.0),
        SmartDevice(3, "Bahçe Kamerası", CAMERA, BAHCE, isOnline = true, isActive = true, watts = 8.5),
        SmartDevice(4, "Giriş Kamerası", CAMERA, ANTRE, isOnline = true, isActive = true, watts = 8.5),
        SmartDevice(5, "Merkezi Termostat", THERMOSTAT, SALON, isOnline = true, isActive = true, watts = 12.0),
        SmartDevice(6, "Yatak Odası Kliması", THERMOSTAT, YATAK_ODASI, isOnline = true, isActive = false, watts = 1200.0),
        SmartDevice(7, "Mutfak Spotları", LIGHT, MUTFAK, isOnline = true, isActive = false, watts = 45.0),
        SmartDevice(8, "Roborock S7", VACUUM, KORIDOR, isOnline = true, isActive = true, watts = 50.0),
        SmartDevice(9, "Balkon Aydınlatması", LIGHT, BALKON, isOnline = false, isActive = false, watts = 25.0),
        SmartDevice(10, "Çalışma Odası Lambası", LIGHT, OFIS, isOnline = true, isActive = true, watts = 12.0)
    )
}
