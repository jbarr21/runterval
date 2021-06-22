package io.github.jbarr21.runterval.app

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.os.Vibrator
import io.github.jbarr21.runterval.data.Action
import io.github.jbarr21.runterval.data.Action.Init
import io.github.jbarr21.runterval.data.AppStore
import io.github.jbarr21.runterval.data.WorkoutState
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut.CoolingDown
import io.github.jbarr21.runterval.data.util.SampleData
import io.github.jbarr21.runterval.data.util.filterAndMap
import io.github.jbarr21.runterval.data.util.observable
import io.github.jbarr21.runterval.service.AmbientUpdateReceiver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import me.tatarka.redux.Dispatcher
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin
import org.threeten.bp.Duration.ofSeconds
import timber.log.Timber
import timber.log.Timber.DebugTree

class App : Application() {

  private val appStore: AppStore by inject()
  private val alarmManager: AlarmManager by inject()
  private val dispatcher: Dispatcher<Action, Action> by inject()
  private val vibrator: Vibrator? by inject()

  override fun onCreate() {
    super.onCreate()
    startKoin(this, Modules.modules)
    Timber.plant(DebugTree())
    setupVibrator()
    setupUpdateLoop()
    dispatcher.dispatch(Init(SampleData.WORKOUTS))
  }

  @SuppressLint("CheckResult")
  private fun setupUpdateLoop() {
    appStore.observable()
        .map { !it.paused }
        .distinctUntilChanged()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { isRunning -> when {
          !isRunning -> alarmManager.cancel(AmbientUpdateReceiver.createUpdatePendingIntent(this))
          else -> {
            alarmManager.cancel(AmbientUpdateReceiver.createUpdatePendingIntent(this))
            AmbientUpdateReceiver.scheduleNextUpdate(this)
          }
        }}
  }

  @SuppressLint("CheckResult")
  private fun setupVibrator() {
    Observable.merge(
          appStore.observable()
              .map { it.workoutState }
              .filterAndMap<WorkoutState, WorkingOut>()
              .map { it.name }
              .distinctUntilChanged(),
          appStore.observable()
              .filter { it.workoutState is CoolingDown }
              .filter { it.remaining.toMillis() == 0L })
        .skip(1)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { vibrator?.vibrate(ofSeconds(1).toMillis()) }
  }
}
