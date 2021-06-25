package io.github.jbarr21.runterval.ui.timer

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.wear.ambient.AmbientModeSupport
import dagger.hilt.android.AndroidEntryPoint
import io.github.jbarr21.runterval.ui.theme.RuntervalTheme
import io.github.jbarr21.runterval.ui.util.RuntervalActivity

@AndroidEntryPoint
class TimerActivity : RuntervalActivity(), AmbientModeSupport.AmbientCallbackProvider {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      RuntervalTheme {
        TimerScreen()
      }
    }
    // setAmbientEnabled()
  }

  override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback = ambientCallback
}
