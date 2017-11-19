package io.github.jbarr21.runterval.app

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.os.Vibrator
import io.github.jbarr21.runterval.data.AmbientStream
import io.github.jbarr21.runterval.data.SampleData
import io.github.jbarr21.runterval.data.State
import io.github.jbarr21.runterval.data.State.WorkingOut
import io.github.jbarr21.runterval.data.State.WorkingOut.CoolingDown
import io.github.jbarr21.runterval.data.State.WorkoutSelection
import io.github.jbarr21.runterval.data.StateStream
import io.github.jbarr21.runterval.data.filterAndMap
import io.github.jbarr21.runterval.service.AmbientUpdateReceiver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.threeten.bp.Duration.ofSeconds
import timber.log.Timber
import timber.log.Timber.DebugTree

class App : Application() {

  val component: Map<Class<*>, *> by lazy { mapOf(
      App::class.java to this,
      StateStream::class.java to StateStream(),
      AmbientStream::class.java to AmbientStream()
  )}

  private val stateStream by bindInstance<StateStream>()
  private val alarmManager by lazy { getSystemService(AlarmManager::class.java) }
  private val vibrator: Vibrator? by lazy { getSystemService(Vibrator::class.java) }

  override fun onCreate() {
    super.onCreate()
    Timber.plant(DebugTree())
    setupVibrator()
    setupUpdateLoop()

    stateStream.setState(WorkoutSelection(SampleData.WORKOUTS))
  }

  private fun setupUpdateLoop() {
    stateStream.stateObservable()
        .filterAndMap<State, WorkingOut>()
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
    stateStream.stateObservable().let {
      Observable.merge(
            it.filterAndMap<State, WorkingOut>()
                .map { it.name }
                .distinctUntilChanged(),
            it.filterAndMap<State, CoolingDown>()
                .filter { it.remaining.toMillis() == 0L })
          .skip(1)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { vibrator?.vibrate(ofSeconds(1).toMillis()) }
    }
  }
}

inline fun <reified T> Context.bindInstance(): Lazy<T> {
  return lazy { (applicationContext as App).component[T::class.java] as T }
}
