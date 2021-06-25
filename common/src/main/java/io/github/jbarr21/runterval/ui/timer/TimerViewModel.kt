package io.github.jbarr21.runterval.ui.timer

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jbarr21.runterval.data.Action
import io.github.jbarr21.runterval.data.AmbientStream
import io.github.jbarr21.runterval.data.AppState
import io.github.jbarr21.runterval.data.asFlow
import io.github.jbarr21.runterval.data.toTimeText
import io.github.jbarr21.runterval.ui.util.RxAmbientCallback
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.redux.Dispatcher
import me.tatarka.redux.Store
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
  private val store: Store<AppState>,
  private val dispatcher: Dispatcher<Action, Action>,
  private val ambientStream: AmbientStream
) : ViewModel() {

  var paused by mutableStateOf(false)
    private set
  var name by mutableStateOf("")
    private set
  var time by mutableStateOf("00:00")
    private set
  var remaining by mutableStateOf(1f)
    private set

  private val ambientCallback by lazy { RxAmbientCallback(ambientStream) }

  init {
    store.asFlow()
      .onEach {
        paused = it.paused
        name = it.name
        time = it.remaining.toTimeText()
        remaining = it.remaining.toMillis().toFloat() / it.duration.toMillis().toFloat()
      }
      .launchIn(viewModelScope)
  }

  fun togglePlayPause() {
    dispatcher.dispatch(if (paused) Action.Resume else Action.Pause)
  }

  fun restartTimer() {
    dispatcher.dispatch(Action.Reset)
  }

  fun exitApp(context: Context) {
    (context as Activity).finishAffinity()
    System.exit(0)
  }
}
