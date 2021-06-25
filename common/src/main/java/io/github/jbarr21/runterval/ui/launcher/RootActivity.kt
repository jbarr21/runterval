package io.github.jbarr21.runterval.ui.launcher

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.jbarr21.runterval.data.AppState
import io.github.jbarr21.runterval.data.Workout
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut.WorkoutComplete
import io.github.jbarr21.runterval.data.WorkoutState.WorkoutSelection
import io.github.jbarr21.runterval.data.asFlow
import io.github.jbarr21.runterval.ui.timer.TimerActivity
import io.github.jbarr21.runterval.ui.util.RuntervalActivity
import io.github.jbarr21.runterval.ui.workout.WorkoutActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import me.tatarka.redux.Store
import javax.inject.Inject

/** Springboard activity that decides which screen to launch based on the state of the [Workout]. */
@AndroidEntryPoint
class RootActivity : RuntervalActivity() {

  @Inject lateinit var store: Store<AppState>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    store.asFlow()
      .take(1)
      .onEach {
        val state = store.state
        when (state.workoutState) {
          is WorkoutSelection -> startWorkoutSelection()
          is WorkoutComplete -> startWorkoutSelection()
          is WorkingOut -> startTimer()
        }
        finish()
      }
      .launchIn(lifecycleScope)
  }

  private fun startWorkoutSelection() = startActivity(Intent(this, WorkoutActivity::class.java))

  private fun startTimer() = startActivity(Intent(this, TimerActivity::class.java))
}
