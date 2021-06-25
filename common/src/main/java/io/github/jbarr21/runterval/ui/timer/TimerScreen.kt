package io.github.jbarr21.runterval.ui.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.jbarr21.runterval.data.AmbientStream
import io.github.jbarr21.runterval.data.AppState
import io.github.jbarr21.runterval.ui.theme.RuntervalTheme
import io.github.jbarr21.runterval.ui.theme.TimerColor
import me.tatarka.redux.Dispatcher
import me.tatarka.redux.SimpleStore
import me.tatarka.redux.Store

@Composable
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
  val context = LocalContext.current
  Box(modifier = Modifier
    .fillMaxSize()
    .background(TimerColor.Bg)
    .aspectRatio(1f)
  ) {
    TimerRing(paused = viewModel.paused, viewModel.remaining)
    TimerDisplay(
      paused = viewModel.paused,
      name = viewModel.name,
      time = viewModel.time,
      onPlayPauseClick = viewModel::togglePlayPause,
      onRestartLongClick = viewModel::restartTimer,
      onExitLongClick = { viewModel.exitApp(context) }
    )
  }
}

@Preview
@Composable
fun TimerScreenPreview() {
  val store: Store<AppState> = SimpleStore(AppState())
  RuntervalTheme {
    TimerScreen(
      TimerViewModel(
        store,
        Dispatcher.forStore(store) { _, _ -> AppState() },
        AmbientStream()
      )
    )
  }
}
