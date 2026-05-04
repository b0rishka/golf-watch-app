package se.golfwatch.mobile.ui.theme

import androidx.compose.ui.graphics.Color

object GolfColors {
    // Surfaces
    val surfaceBlack = Color(0xFF000000)
    val surfaceBase = Color(0xFF0F1410)
    val surfaceRaised = Color(0xFF1A201C)
    val surfaceHighlight = Color.White.copy(alpha = 0.06f)

    // Course geometry — used on watch maps and phone hole preview
    val fairway = Color(0xFF3D5440)
    val green = Color(0xFF5A7A55)
    val bunker = Color(0xFFA89A82)
    val water = Color(0xFF3A5566)
    val tee = Color(0xFF888888)

    // Text
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFFD4D4D4)
    val textMuted = Color(0xFF8A8A8A)
    val textCaption = Color(0xFF6A6A6A)
    val textDisabled = Color(0xFF5A5A5A)

    // Accents
    val accentWind = Color(0xFF7A9BB3)
    val accentPlayer = Color(0xFFFFFFFF)
    val actionPrimary = Color(0xFFFFFFFF)
    val actionPrimaryText = Color(0xFF0F1410)

    // Status — intentionally reuse geometry tokens (green dot = green polygons exist)
    val statusDetailed = green
    val statusPartial = bunker
    val statusMissing = textDisabled
}
