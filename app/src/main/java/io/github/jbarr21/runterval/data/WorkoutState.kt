package io.github.jbarr21.runterval.data

import io.github.jbarr21.runterval.data.util.Interval
import io.github.jbarr21.runterval.data.util.Workout
import org.threeten.bp.Duration

sealed class WorkoutState {
  data class WorkoutSelection(val workouts: List<Workout>) : WorkoutState()

  sealed class WorkingOut : WorkoutState() {
    abstract val name: CharSequence
    abstract val duration: Duration

    data class WarmingUp(override val duration: Duration) : WorkingOut() {
      override val name = "Warmup"
    }

    data class Intervals(
        val interval: Interval,
        val set: Int = 0,
        val working: Boolean = true) : WorkingOut() {

      override val name = "${if (working) "Work" else "Rest"} ${set + 1}"
      override val duration = if (working) interval.work else interval.rest
    }

    data class CoolingDown(override val duration: Duration) : WorkingOut() {
      override val name = "Cooldown"
    }

    data class WorkoutComplete(val workout: Workout) : WorkoutState()
  }
}
