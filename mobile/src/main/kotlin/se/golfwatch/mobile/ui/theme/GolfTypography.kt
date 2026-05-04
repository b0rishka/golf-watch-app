package se.golfwatch.mobile.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Phone type scale — DESIGN.md §3.
// Weights: 300 / 400 / 500 only. No 600 or 700.
// Tabular numerals ("tnum") on any style used for frequently-updated numbers.
object GolfTypography {
    val headlineLarge = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.W400)
    val headlineMedium = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.W400)
    val displayStat =
        TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.W400,
            fontFeatureSettings = "tnum",
        )
    val bodyLarge = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W500)
    val bodyMedium = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.W400)
    val bodySmall = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.W500)
    val labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.W400)
    val caption =
        TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight.W500,
            letterSpacing = 1.5.sp,
        )
}
