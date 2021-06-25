package io.github.jbarr21.runterval.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.jbarr21.runterval.R

val spaceMonoFamily = FontFamily(Font(R.font.space_mono))

val Typography = Typography(
  h1 = TextStyle(
    fontFamily = spaceMonoFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 48.sp,
    color = Color.White
  ),
  body1 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    color = Color.White
  )
)
