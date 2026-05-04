package se.golfwatch.mobile.ui.theme

import androidx.compose.runtime.Composable

// Thin wrapper — individual screens set their own background via GolfColors.surfaceBase.
// We intentionally don't wrap Material3's MaterialTheme here; all styling comes
// directly from GolfColors and GolfTypography to avoid Material defaults leaking in.
@Composable
fun GolfTheme(content: @Composable () -> Unit) {
    content()
}
