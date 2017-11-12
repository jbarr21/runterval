package io.github.jbarr21.runterval.app

import android.app.Application
import android.content.Context
import io.github.jbarr21.runterval.data.SampleData
import io.github.jbarr21.runterval.data.State
import io.github.jbarr21.runterval.data.State.WorkoutSelection
import io.github.jbarr21.runterval.data.StateStream
import io.github.jbarr21.runterval.data.WorkingOut
import io.github.jbarr21.runterval.data.filterAndMap
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.util.concurrent.TimeUnit.MILLISECONDS

class App : Application() {

  val component: Map<Class<*>, *> by lazy { mapOf(
      App::class.java to this,
      StateStream::class.java to StateStream()
  ) }

  private val stateStream by bindInstance<StateStream>()

  override fun onCreate() {
    super.onCreate()
    Timber.plant(DebugTree())

    stateStream.stateObservable()
        .filterAndMap<State, WorkingOut>()
        .map { !it.paused }
        .distinctUntilChanged()
        .observeOn(AndroidSchedulers.mainThread())
        .switchMap { isRunning ->
          //Toast.makeText(this, "${if (isRunning) "Started" else "Paused"}!", Toast.LENGTH_SHORT).show()
          return@switchMap when {
            isRunning -> Observable.interval(16, MILLISECONDS)
                .map { stateStream.peekState() }
                .filterAndMap<State, WorkingOut>()
                .map { it.updateAfterTick(16, ChronoUnit.MILLIS) }
            else -> Observable.never<State>()
          }
        }
        //.doOnNext { Timber.d("Updating state, isRunning = ${!it.paused}, remaining = ${it.remaining.toTimeText()}") }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          stateStream.setState(it)
        }

    stateStream.setState(WorkoutSelection(SampleData.WORKOUTS))
  }
}

inline fun <reified T> Context.bindInstance(): Lazy<T> {
  return lazy { (applicationContext as App).component[T::class.java] as T }
}
