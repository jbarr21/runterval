package io.github.jbarr21.runterval.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors()

@Composable
fun RuntervalTheme(content: @Composable() () -> Unit) {
  MaterialTheme(
    colors = DarkColorPalette,
    typography = Typography,
    content = content
  )
}
