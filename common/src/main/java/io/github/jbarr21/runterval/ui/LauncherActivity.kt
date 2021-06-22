package io.github.jbarr21.runterval.ui

import android.content.Intent
import android.os.Bundle
import com.uber.autodispose.android.lifecycle.autoDispose
import io.github.jbarr21.runterval.data.AppStore
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut.WorkoutComplete
import io.github.jbarr21.runterval.data.WorkoutState.WorkoutSelection
import io.github.jbarr21.runterval.data.util.Workout
import io.github.jbarr21.runterval.data.util.observable
import io.github.jbarr21.runterval.ui.util.RuntervalActivity
import org.koin.android.ext.android.inject

/**
 * Springboard activity that decides which screen to launch based on the state of the [Workout].
 */
class LauncherActivity : RuntervalActivity() {

  private val appStore: AppStore by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    appStore.observable()
        .take(1)
        .autoDispose(this)
        .subscribe { appState ->
          when (appState.workoutState) {
            is WorkoutSelection -> startWorkoutSelection()
            is WorkoutComplete -> startWorkoutSelection() // TODO: display temporary animation?
            is WorkingOut -> startTimer(appState.workout)
          }
          finish()
        }
  }

  // TODO: should these be moved elsewhere? action creators, middleware, or reducers?
  private fun startWorkoutSelection() {
    startActivity(Intent(this, WorkoutActivity::class.java))
  }

  private fun startTimer(workout: Workout) {
    startActivity(Intent(this, TimerActivity::class.java))
  }
}
