package io.github.jbarr21.runterval.ui.timer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import io.github.jbarr21.runterval.R
import io.github.jbarr21.runterval.ui.theme.RuntervalTheme
import io.github.jbarr21.runterval.ui.theme.TimerColor
import io.github.jbarr21.runterval.ui.theme.withBrightnessAdjustment
import io.github.jbarr21.runterval.ui.util.TextSwitcher

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimerDisplay(
  paused: Boolean,
  name: String,
  time: String,
  onPlayPauseClick: () -> Unit = {},
  onRestartLongClick: () -> Unit = {},
  onExitLongClick: () -> Unit = {}
) {
  val fabTint = if (paused) TimerColor.RingPurple else TimerColor.RingGreen
  val fabColor by animateColorAsState(fabTint)
  val fabColorDarker by animateColorAsState(fabTint.withBrightnessAdjustment(-0.75f))

  ConstraintLayout(modifier = Modifier.fillMaxSize()) {
    val (nameText, timeText, restartButton, startButton, closeButton) = createRefs()
    val nameGuideline = createGuidelineFromTop(0.125f)

    TextSwitcher(
      text = name,
      style = MaterialTheme.typography.body1,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .padding(vertical = 8.dp)
        .fillMaxWidth()
        .constrainAs(nameText) {
          top.linkTo(nameGuideline)
          bottom.linkTo(timeText.top)
        }
    )

    Text(
      text = time,
      style = MaterialTheme.typography.h1,
      modifier = Modifier
        .constrainAs(timeText) {
          top.linkTo(parent.top)
          bottom.linkTo(startButton.top)
          start.linkTo(parent.start)
          end.linkTo(parent.end)
        }
    )

    AnimatedVisibility(
      visible = paused,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.size(40.dp).constrainAs(restartButton) {
          start.linkTo(timeText.start)
          top.linkTo(timeText.bottom, margin = 8.dp)
        }
    ) {
      FloatingActionButton(
        contentColor = Color.White,
        backgroundColor = fabColorDarker,
        onClick = onRestartLongClick,
        // Toast.makeText(this, "Longpress to Reset", LENGTH_SHORT).show()
      ) {
        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Restart")
      }
    }

    AnimatedVisibility(
      visible = paused,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.size(40.dp).constrainAs(closeButton) {
        end.linkTo(timeText.end)
        top.linkTo(timeText.bottom, margin = 8.dp)
      }
    ) {
      FloatingActionButton(
        contentColor = Color.White,
        backgroundColor = fabColorDarker,
        onClick = onExitLongClick
        // Toast.makeText(this, "Longpress to Exit", LENGTH_SHORT).show()
      ) {
        Icon(imageVector = Icons.Default.Close, contentDescription = "Exit")
      }
    }

    FloatingActionButton(
      contentColor = Color.White,
      backgroundColor = fabColor,
      onClick = onPlayPauseClick,
      modifier = Modifier.size(40.dp).constrainAs(startButton) {
        top.linkTo(timeText.bottom)
        bottom.linkTo(parent.bottom)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
      }
    ) {
      if (paused) {
        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start")
      } else {
        Icon(painter = painterResource(R.drawable.ic_pause_24dp), contentDescription = "Pause")
      }
    }
  }
}

@Preview(widthDp = 250, heightDp = 250)
@Composable
fun TimerDisplayPausedPreview() {
  RuntervalTheme {
    Column {
      TimerDisplay(paused = true, name = "Warmup", "00:05")
    }
  }
}


@Preview(widthDp = 250, heightDp = 250)
@Composable
fun TimerDisplayPreview() {
  RuntervalTheme {
    Column {
      TimerDisplay(paused = false, name = "Warmup", "00:05")
    }
  }
}
