package io.github.jbarr21.runterval.ui.theme

import androidx.compose.ui.graphics.Color

object TimerColor {
  val Bg = Color(0xFF222c66)
  val ListFabBg = Color(0xFF3f65a6)

  val TimerBgRing = Color(0xFF19214d)

  val TimerFabPurple = Color(0xFF536dfe)
  val RingPurple = Color(0xFF536dfe)
  val RingPurpleDarker = Color(0xFF4a61e2)

  val TimerFabGreen = Color(0xFF00e676)
  val RingGreen = Color(0xFF00e676)
  val RingGreenDarker = Color(0xFF04c76f)

  val TimerFabPink = Color(0xFFf50057)

  val MdDeepPurple500 = Color(0xFF673AB7)
  val MdGreen500 = Color(0xFF4CAF50)
  val MdPink500 = Color(0xFFE91E63)
}

fun Color.withBrightnessAdjustment(adjustment: Float = -0.1f): Color {
  return toHSB().apply {
    this[2] += adjustment
  }.toColor()
}
