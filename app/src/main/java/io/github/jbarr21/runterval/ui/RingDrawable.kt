package io.github.jbarr21.runterval.ui

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.Paint.Style
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.graphics.Path.Direction.CW
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region.Op
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import io.github.jbarr21.runterval.R
import java.lang.Math.round

class RingDrawable(
    val res: Resources,
    var remainingPct: Float = 1f,
    @ColorInt fabColor: Int = 0
  ) : Drawable() {

  var ringAnimationOffset: Float = 0f
  var ringColors: Pair<Int, Int> = Pair(fabColor, fabColor)
    set(value) {
      dashPaintPrimary.color = value.first
      dashPaintDarker.color = value.second
    }

  private val startAngle = -90f
  private val ringClipPath = Path()
  private val tempPoint = PointF()
  private val boundsRing: RectF = RectF()
  private val boundsInner: RectF = RectF()
  private val boundsOuter: RectF = RectF()
  private val ringBgPaint: Paint = Paint().apply {
    style = STROKE
    strokeWidth = 4f
    strokeCap = Cap.BUTT
    isAntiAlias = true
    color = res.getColor(R.color.timer_bg_ring)
  }
  private val ringMaskPaint: Paint = Paint(ringBgPaint).apply {
    set(ringBgPaint)
    strokeCap = Cap.ROUND
  }
  private val dashPaintPrimary: Paint = Paint(ringBgPaint)
  private val dashPaintDarker: Paint = Paint(ringBgPaint)

  override fun onBoundsChange(bounds: Rect) {
    super.onBoundsChange(bounds)
    val strokeWidth = bounds.width() * 0.05f
    arrayOf(ringBgPaint, ringMaskPaint, dashPaintPrimary, dashPaintDarker)
        .forEach { it.strokeWidth = strokeWidth }

    val dashLength = strokeWidth * 2
    dashPaintPrimary.pathEffect = DashPathEffect(floatArrayOf(dashLength, dashLength), 0f)
    dashPaintDarker.pathEffect = DashPathEffect(floatArrayOf(0f, dashLength, dashLength, 0f), 0f)

    val ringInset: Int = round(strokeWidth / 2)
    boundsOuter.set(bounds)
    bounds.inset(ringInset, ringInset)
    boundsRing.set(bounds)
    bounds.inset(ringInset, ringInset)
    boundsInner.set(bounds)
  }

  override fun draw(canvas: Canvas) {
    canvas.apply {
      drawArc(boundsRing,  startAngle, 360f, false, ringBgPaint)
      inNewStackFrame {
        clipPath(updateRingClipPath(), Op.INTERSECT)
        drawArc(boundsRing, startAngle + ringAnimationOffset, 360f, false, dashPaintPrimary)
        drawArc(boundsRing, startAngle + ringAnimationOffset, 360f, false, dashPaintDarker)
      }
      //debugClipPath(this, ringClipPath)
    }
  }

  private fun updateRingClipPath(): Path {
    return ringClipPath.apply {
      reset()
      when {
        remainingPct > .999 -> addCircle(boundsOuter.centerX(), boundsOuter.centerY(), boundsOuter.centerY(), CW)
        remainingPct > .001 -> {
          // pizza slice of remaining area
          moveTo(boundsOuter.centerX(), boundsOuter.centerY())
          lineTo(boundsOuter.centerX(), boundsOuter.top)
          arcTo(boundsOuter, -90f, +360 * remainingPct, true)
          lineTo(boundsOuter.centerX(), boundsOuter.centerY())
          close()

          // circles at start and end points for rounded caps
          addCircle(boundsRing.centerX(), boundsRing.top, ringBgPaint.strokeWidth / 2f, CW)
          pointOnCircle(360.0 * remainingPct, boundsRing.width() / 2f).let {
            addCircle(it.x, it.y, ringBgPaint.strokeWidth / 2f, CW)
          }
        }
      }
    }
  }

  private fun debugClipPath(canvas: Canvas, clipPath: Path) {
    canvas.drawPath(clipPath, ringMaskPaint.apply {
      color = Color.RED
      style = Style.FILL
      strokeWidth = 2f
    })
  }

  /**
   * Given a center point and radius of a circle, update a point on the circle at the given angle.
   * Due north of the center point (x=0) is 0 degrees and increasing the degree value will move
   * the point clockwise around the circle.<br><br>
   *
   * @param angleDegrees  Angle from center (in degrees)
   * @param radius        Radius of circle
   */
  private fun pointOnCircle(angleDegrees: Double, radius: Float): PointF {
    val angleRadians = Math.toRadians(angleDegrees)
    val x = (radius * Math.sin(angleRadians) + boundsOuter.centerX())
    val y = (radius * -Math.cos(angleRadians) + boundsOuter.centerY())
    tempPoint.set(x.toFloat(), y.toFloat())
    return tempPoint
  }

  override fun getOpacity() = 255
  override fun setAlpha(alpha: Int) = Unit
  override fun setColorFilter(filter: ColorFilter?) = Unit
}

private fun Canvas.inNewStackFrame(ops: () -> Unit) {
  save()
  ops()
  restore()
}

private fun Path.moveTo(point: PointF) = moveTo(point.x, point.y)
private fun Path.lineTo(point: PointF) = lineTo(point.x, point.y)
