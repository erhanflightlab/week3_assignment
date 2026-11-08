package com.example.week3.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.week3.R
import com.example.week3.data.DeviceCategory
import com.example.week3.data.HomeRoom

@Composable
fun EnergyDashboard(
    state: EnergyDashboardState,
    query: String,
    roomMenuOpen: Boolean,
    onRoomMenuChange: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    onRoomSelected: (HomeRoom?) -> Unit,
    onDeviceToggle: (Int) -> Unit
) {
    Scaffold(containerColor = Paper, contentWindowInsets = WindowInsets.safeDrawing) { insets ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(insets).consumeWindowInsets(insets).imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            val compact = this.maxHeight < 500.dp
            Column(Modifier.widthIn(max = 680.dp).fillMaxSize()) {
                if (!compact) DashboardHeading()
                HouseLoadMeter(state, compact, Modifier.padding(horizontal = 18.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 18.dp)
                ) {
                    item(key = "inventory_controls") {
                        InventoryControls(
                            state = state,
                            query = query,
                            menuOpen = roomMenuOpen,
                            onMenuChange = onRoomMenuChange,
                            onQueryChange = onQueryChange,
                            onRoomSelected = onRoomSelected
                        )
                    }
                    if (state.isLoading) {
                        item(key = "loading") {
                            Box(Modifier.fillMaxWidth().padding(22.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(Modifier.size(24.dp))
                            }
                        }
                    } else if (state.visibleDevices.isEmpty()) {
                        item(key = "empty") {
                            Column(Modifier.padding(vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Cihaz bulunamadı", style = MaterialTheme.typography.titleMedium)
                                Text("Başka bir ad veya oda seç.", color = MutedInk)
                            }
                        }
                    }
                    items(items = state.visibleDevices, key = { it.device.id }) { entry ->
                        DeviceLedgerRow(entry, onToggle = { onDeviceToggle(entry.device.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardHeading() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_ev_nabzi),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(38.dp)
        )
        Column(Modifier.weight(1f)) {
            Text(
                "EvNabzı",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )
            Text("Enerji panosu", color = MutedInk, style = MaterialTheme.typography.bodySmall)
        }
        Surface(color = SoftMoss, shape = RoundedCornerShape(6.dp)) {
            Row(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(6.dp).background(Moss, CircleShape))
                Text("CANLI", color = Moss, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun HouseLoadMeter(state: EnergyDashboardState, compact: Boolean, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Surface(
        modifier = modifier.fillMaxWidth().border(1.dp, Divider, shape),
        color = Peach,
        shape = shape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = if (compact) 8.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text("EV TOPLAMI", color = Terracotta, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        state.totalPowerLabel,
                        style = if (compact) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displaySmall,
                        color = Ink,
                        fontWeight = FontWeight.Bold
                    )
                    Text("W", color = Terracotta, style = MaterialTheme.typography.titleMedium)
                }
            }
            if (!state.isLoading) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("${state.activeOnlineCount} açık", color = Ink, style = MaterialTheme.typography.labelLarge)
                    Text("${state.onlineCount} çevrimiçi", color = MutedInk, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun InventoryControls(
    state: EnergyDashboardState,
    query: String,
    menuOpen: Boolean,
    onMenuChange: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    onRoomSelected: (HomeRoom?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Cihazlar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            RoomSelector(state.rooms, state.selectedRoom, menuOpen, onMenuChange, onRoomSelected)
        }
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Cihaz adı") },
            placeholder = { Text("Örn. lamba") },
            leadingIcon = { BoardIcon(R.drawable.ic_search, MutedInk) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(painterResource(R.drawable.ic_close), contentDescription = "Aramayı temizle")
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
        if (!state.isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${state.visibleDevices.size} cihaz",
                    modifier = Modifier.weight(1f),
                    color = MutedInk,
                    style = MaterialTheme.typography.labelMedium
                )
                state.selectedRoomLoadLabel?.let { roomLoad ->
                    Surface(color = SoftMoss, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            "Oda tüketimi: $roomLoad",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            color = Moss,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = Divider)
    }
}

@Composable
private fun RoomSelector(
    rooms: List<HomeRoom>,
    selected: HomeRoom?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (HomeRoom?) -> Unit
) {
    Box {
        OutlinedButton(
            onClick = { onExpandedChange(true) },
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
            modifier = Modifier.widthIn(max = 164.dp)
        ) {
            Text(
                selected?.label ?: "Tüm odalar",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            BoardIcon(R.drawable.ic_chevron_down, Terracotta, Modifier.padding(start = 5.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            DropdownMenuItem(
                text = { RoomOption("Tüm odalar", selected == null) },
                onClick = { onSelect(null) }
            )
            rooms.forEach { room ->
                DropdownMenuItem(
                    text = { RoomOption(room.label, selected == room) },
                    onClick = { onSelect(room) }
                )
            }
        }
    }
}

@Composable
private fun RoomOption(label: String, selected: Boolean) {
    Text(
        if (selected) "$label  ✓" else label,
        color = if (selected) Terracotta else Ink,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
    )
}

@Composable
private fun DeviceLedgerRow(entry: DeviceListEntry, onToggle: () -> Unit) {
    val device = entry.device
    val (status, statusColor) = when {
        !device.isOnline -> "Çevrimdışı" to Terracotta
        device.isActive -> "Açık" to Moss
        else -> "Kapalı" to MutedInk
    }
    val icon = when (device.category) {
        DeviceCategory.LIGHT -> R.drawable.ic_light
        DeviceCategory.CAMERA -> R.drawable.ic_camera
        DeviceCategory.THERMOSTAT -> R.drawable.ic_thermostat
        DeviceCategory.VACUUM -> R.drawable.ic_vacuum
    }

    Surface(color = Sheet, modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 9.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(34.dp).background(SoftStone, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    BoardIcon(icon, Ink)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(device.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "${device.room.label} · ${device.category.label}",
                        color = MutedInk,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(5.dp).background(statusColor, CircleShape))
                        Text(status, color = statusColor, style = MaterialTheme.typography.labelMedium)
                        Text("· ${entry.ratedPowerLabel}", color = MutedInk, style = MaterialTheme.typography.labelMedium)
                    }
                }
                val shape = RoundedCornerShape(8.dp)
                val padding = PaddingValues(horizontal = 9.dp, vertical = 4.dp)
                if (device.isActive && device.isOnline) {
                    OutlinedButton(onClick = onToggle, shape = shape, contentPadding = padding) {
                        Text("Kapat")
                    }
                } else {
                    Button(onClick = onToggle, enabled = device.isOnline, shape = shape, contentPadding = padding) {
                        Text("Aç")
                    }
                }
            }
            HorizontalDivider(color = Divider)
        }
    }
}

@Composable
private fun BoardIcon(@DrawableRes resource: Int, tint: Color, modifier: Modifier = Modifier) {
    Icon(painterResource(resource), contentDescription = null, tint = tint, modifier = modifier.size(18.dp))
}
