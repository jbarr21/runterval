package io.github.jbarr21.runterval.app

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.os.Vibrator
import dagger.hilt.android.HiltAndroidApp
import io.github.jbarr21.runterval.data.Action
import io.github.jbarr21.runterval.data.Action.Init
import io.github.jbarr21.runterval.data.AppState
import io.github.jbarr21.runterval.data.SampleData
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut.CoolingDown
import io.github.jbarr21.runterval.data.asFlow
import io.github.jbarr21.runterval.service.AmbientUpdateReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.Duration.ofSeconds
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltAndroidApp
class App : Application(), CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main

  @Inject lateinit var store: me.tatarka.redux.Store<AppState>
  @Inject lateinit var alarmManager: AlarmManager
  @Inject lateinit var dispatcher: me.tatarka.redux.Dispatcher<Action, Action>
  @Inject lateinit var vibrator: Vibrator

  override fun onCreate() {
    super.onCreate()
    Timber.plant(DebugTree())
    setupVibrator()
    setupUpdateLoop()
    dispatcher.dispatch(Init(SampleData.WORKOUTS))
  }

  @SuppressLint("CheckResult")
  private fun setupUpdateLoop() {
    store.asFlow()
        .map { !it.paused }
        .distinctUntilChanged()
        .onEach { isRunning -> when {
          !isRunning -> alarmManager.cancel(AmbientUpdateReceiver.createUpdatePendingIntent(this))
          else -> {
            alarmManager.cancel(AmbientUpdateReceiver.createUpdatePendingIntent(this))
            AmbientUpdateReceiver.scheduleNextUpdate(this, alarmManager)
          }
        }}.launchIn(this)
  }

  @SuppressLint("CheckResult")
  private fun setupVibrator() {
    val workoutStarts = store.asFlow().map { it.workoutState }.filter { it is WorkingOut }.distinctUntilChanged()
    val cooldownStarts = store.asFlow().filter { it.workoutState is CoolingDown && it.remaining.toMillis() == 0L }.drop(1)
    merge(workoutStarts, cooldownStarts)
      .onEach { vibrator?.vibrate(ofSeconds(1).toMillis()) }
      .launchIn(this)
  }
}
