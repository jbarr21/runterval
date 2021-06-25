package io.github.jbarr21.runterval.ui.timer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jbarr21.runterval.ui.theme.TimerColor
import io.github.jbarr21.runterval.ui.theme.withBrightnessAdjustment
import kotlin.math.min

@Composable
fun TimerRing(paused: Boolean, remainingPct: Float) {
  val numSegments = 32f

  val infiniteTransition = rememberInfiniteTransition()
  val segmentOffset by infiniteTransition.animateFloat(
    initialValue = 0f, targetValue = (1f / numSegments * 2f),
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1500, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    )
  )

  val strokeWidth = 8.dp
  val segmentTint = if (paused) TimerColor.RingPurple else TimerColor.RingGreen
  val segmentColor by animateColorAsState(segmentTint)
  val segmentColorDarker by animateColorAsState(segmentTint.withBrightnessAdjustment(-0.1f))
  Canvas(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(1f)
      .padding(strokeWidth)
  ) {
    drawArc(
      color = TimerColor.TimerBgRing,
      startAngle = 0f,
      sweepAngle = 360f,
      useCenter = false,
      style = Stroke(width = strokeWidth.toPx()),
    )

    drawArc(
      brush = dashPathBrush(
        numSegments = numSegments,
        offset = segmentOffset,
        colorLighter = segmentColor,
        colorDarker = segmentColorDarker
      ),
      startAngle = 270f,
      sweepAngle = 360f * remainingPct,
      useCenter = false,
      style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
    )

/*
    drawArc(
      color = segmentColorDarker,
      startAngle = 270f,
      sweepAngle = 360f * remainingPct,
      useCenter = false,
      style = Stroke(
        width = strokeWidth.toPx(),
        cap = StrokeCap.Square,
        pathEffect = dashPathEffect(floatArrayOf(arcSegmentLength, arcSegmentLength), 0f)
      ),
    )
*/

/*
    withTransform(transformBlock = { rotate(angle, middle) }, {
      drawCircle(
        color = segmentColorDarker,
        center = middle,
        radius = radius,
        style = Stroke(
          width = strokeWidth.toPx(),
          pathEffect = dashPathEffect(floatArrayOf(arcSegmentLength, arcSegmentLength), 0f)
        ),
      )
    })
*/
  }
}

fun dashPathBrush(numSegments: Float, offset: Float, colorLighter: Color, colorDarker: Color): Brush {
  val segmentPct = 1f / numSegments
  val colorStops = mutableListOf<Pair<Float, Color>>()
  var useLighter = offset < segmentPct
  val offset = if (offset > segmentPct) offset - segmentPct else offset

  (0 until numSegments.toInt()).forEach { i ->
    val color = if (useLighter) colorLighter else colorDarker
    val start = segmentPct * i + offset
    val end = min(start + segmentPct, 1f)
    if (start < 1f) {
      colorStops += start to color
      colorStops += end to color
      useLighter = !useLighter
    }
  }

  val firstStop = segmentPct - (colorStops.last().first - colorStops[colorStops.size - 2].first)
  if (offset > 0f) {
    colorStops.add(0, 0f to colorStops.last().second)
    colorStops.add(1,  firstStop to colorStops.last().second)
  }
  return Brush.sweepGradient(*colorStops.toTypedArray())
}

@Preview
@Composable
fun TimerRingPausedPreview() {
  TimerRing(true, 1f)
}

@Preview
@Composable
fun TimerRingPreview() {
  TimerRing(false, 0.75f)
}

@Preview
@Composable
fun TimerRingAlmostDonePreview() {
  TimerRing(false, 0.25f)
}
