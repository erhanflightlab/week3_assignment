package com.example.week3.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal val Paper = Color(0xFFFAF6EF)
internal val Sheet = Color(0xFFFFFCF8)
internal val Divider = Color(0xFFE5DCCE)
internal val Ink = Color(0xFF20312D)
internal val MutedInk = Color(0xFF59625A)
internal val Terracotta = Color(0xFF973B24)
internal val Peach = Color(0xFFF8E3D0)
internal val Moss = Color(0xFF286449)
internal val SoftMoss = Color(0xFFE3EEE2)
internal val SoftStone = Color(0xFFF0E9DF)

private val EnergyBoardColors = lightColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,
    primaryContainer = Peach,
    onPrimaryContainer = Terracotta,
    background = Paper,
    onBackground = Ink,
    surface = Sheet,
    onSurface = Ink,
    surfaceVariant = SoftStone,
    onSurfaceVariant = MutedInk,
    outline = Color(0xFF8B8172),
    outlineVariant = Divider,
    secondary = Moss,
    secondaryContainer = SoftMoss,
    onSecondaryContainer = Moss
)

@Composable
fun EvNabziTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EnergyBoardColors, content = content)
}
