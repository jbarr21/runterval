package io.github.jbarr21.runterval.app

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.os.Vibrator
import io.github.jbarr21.runterval.data.Action.Init
import io.github.jbarr21.runterval.data.AmbientStream
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
import org.threeten.bp.Duration.ofSeconds
import timber.log.Timber
import timber.log.Timber.DebugTree

class App : Application() {

  val component: Map<Class<*>, *> by lazy { mapOf(
      App::class.java to this,
      AmbientStream::class.java to AmbientStream(),
      AppStore::class.java to AppStore()
  )}

  private val appStore by bindInstance<AppStore>()
  private val alarmManager by lazy { getSystemService(AlarmManager::class.java) }
  private val vibrator: Vibrator? by lazy { getSystemService(Vibrator::class.java) }

  override fun onCreate() {
    super.onCreate()
    Timber.plant(DebugTree())
    setupVibrator()
    setupUpdateLoop()
    appStore.dispatch(Init(SampleData.WORKOUTS))
  }

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

inline fun <reified T> Context.bindInstance(): Lazy<T> {
  return lazy { (applicationContext as App).component[T::class.java] as T }
}
