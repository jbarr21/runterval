package io.github.jbarr21.runterval.ui

import android.content.Intent
import android.os.Bundle
import com.uber.autodispose.kotlin.autoDisposeWith
import io.github.jbarr21.runterval.app.bindInstance
import io.github.jbarr21.runterval.data.State.WorkingOut
import io.github.jbarr21.runterval.data.State.WorkingOut.WorkoutComplete
import io.github.jbarr21.runterval.data.State.WorkoutSelection
import io.github.jbarr21.runterval.data.StateStream
import io.github.jbarr21.runterval.data.Workout

class LauncherActivity : AutoDisposeWearableActivity() {

  private val stateStream by bindInstance<StateStream>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    stateStream.stateObservable()
        .take(1)
        .autoDisposeWith(this)
        .subscribe {
          when (it) {
            is WorkoutSelection -> startWorkoutSelection()
            is WorkoutComplete -> startWorkoutSelection() // TODO: display temporary animation?
            is WorkingOut -> startTimer(it.workout)
            else -> startWorkoutSelection()
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
