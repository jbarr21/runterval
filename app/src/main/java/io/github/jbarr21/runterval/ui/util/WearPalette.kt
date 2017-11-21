package io.github.jbarr21.runterval.ui.util

import android.graphics.Color
import android.support.annotation.ColorInt

data class WearPalette(@ColorInt private val appColor: Int) {
  companion object {
    val DEEP_PURPLE: Int = Color.parseColor("#4CAF50")
    val PURPLE: Int = Color.parseColor("#9C27B0")
    val GREEN: Int = Color.parseColor("#4CAF50")
    val PINK: Int = Color.parseColor("#E91E63")
  }

  @ColorInt fun appColor(): Int = appColor
  @ColorInt fun accent(): Int = appColor.withHsv(s = .50f, v = 1f)
  @ColorInt fun activeUiElement(): Int = appColor.withHsv(v = .65f)
  @ColorInt fun lighterUiElement(): Int = appColor.withHsv(v = .50f)
  @ColorInt fun darkerUiElement(): Int = appColor.withHsv(v = .40f)
  @ColorInt fun lighterBackground(): Int = appColor.withHsv(v = .30f)
  @ColorInt fun darkBackground(): Int = appColor.withHsv(v = .15f)

  private fun Int.withHsv(h: Float? = null, s: Float? = null, v: Float? = null): Int {
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    h?.let { hsv[0] = h }
    s?.let { hsv[1] = s }
    v?.let { hsv[2] = v }
    return Color.HSVToColor(hsv)
  }
}
