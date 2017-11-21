package io.github.jbarr21.runterval.ui

import android.content.Intent
import android.os.Bundle
import com.uber.autodispose.kotlin.autoDisposeWith
import io.github.jbarr21.runterval.app.bindInstance
import io.github.jbarr21.runterval.data.AppStore
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut.WorkoutComplete
import io.github.jbarr21.runterval.data.WorkoutState.WorkoutSelection
import io.github.jbarr21.runterval.data.util.Workout
import io.github.jbarr21.runterval.data.util.observable
import io.github.jbarr21.runterval.ui.util.AutoDisposeWearableActivity

class LauncherActivity : AutoDisposeWearableActivity() {

  private val appStore by bindInstance<AppStore>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    appStore.observable()
        .take(1)
        .autoDisposeWith(this)
        .subscribe {
          when (it.workoutState) {
            is WorkoutSelection -> startWorkoutSelection()
            is WorkoutComplete -> startWorkoutSelection() // TODO: display temporary animation?
            is WorkingOut -> startTimer(it.workout)
          }
          finish()
        }
  }

  private fun startWorkoutSelection() {
    startActivity(Intent(this, WorkoutActivity::class.java))
  }

  private fun startTimer(workout: Workout) {
    startActivity(Intent(this, TimerActivity::class.java))
  }
}
