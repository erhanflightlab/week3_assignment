package com.example.week3.data

enum class DeviceCategory(val label: String) {
    LIGHT("Aydınlatma"),
    CAMERA("Kamera"),
    THERMOSTAT("İklimlendirme"),
    VACUUM("Robot süpürge")
}

enum class HomeRoom(val label: String) {
    SALON("Salon"),
    BAHCE("Bahçe"),
    ANTRE("Antre"),
    YATAK_ODASI("Yatak Odası"),
    MUTFAK("Mutfak"),
    KORIDOR("Koridor"),
    BALKON("Balkon"),
    OFIS("Ofis")
}

data class SmartDevice(
    val id: Int,
    val name: String,
    val category: DeviceCategory,
    val room: HomeRoom,
    val isOnline: Boolean,
    val isActive: Boolean,
    val watts: Double
)
