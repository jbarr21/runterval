package io.github.jbarr21.runterval.ui

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Paint.Cap.ROUND
import android.graphics.Paint.Style.STROKE
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import java.lang.Math.round

class RingDrawable(
    @ColorInt timerColor: Int,
    var remainingPct: Float = 1f,
    private val ringPaint: Paint = Paint(),
    private val bounds: RectF = RectF()
  ) : Drawable() {

  @ColorInt var ringColor: Int = timerColor
    set(value) {
      ringPaint.color = value
    }

  init {
    ringPaint.apply {
      style = STROKE
      strokeWidth = 4f
      strokeCap = ROUND
      isAntiAlias = true
    }
  }

  override fun onBoundsChange(bounds: Rect) {
    super.onBoundsChange(bounds)
    val strokeWidth = bounds.width() * 0.05f
    ringPaint.strokeWidth = strokeWidth
    val halfStrokeWidth: Int = round(strokeWidth / 2)
    bounds.inset(halfStrokeWidth, halfStrokeWidth)
    this.bounds.set(bounds)
  }

  override fun draw(canvas: Canvas) {
    canvas.drawArc(bounds, 0f - 90f, 360 * remainingPct, false, ringPaint)
  }

  override fun getOpacity() = 255
  override fun setAlpha(alpha: Int) = Unit
  override fun setColorFilter(filter: ColorFilter) = Unit
}
