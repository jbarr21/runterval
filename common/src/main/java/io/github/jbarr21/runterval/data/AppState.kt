package io.github.jbarr21.runterval.data;

import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut
import io.github.jbarr21.runterval.data.WorkoutState.WorkoutSelection
import io.github.jbarr21.runterval.data.util.SampleData
import io.github.jbarr21.runterval.data.util.Workout
import org.threeten.bp.Duration
import org.threeten.bp.Duration.ZERO

data class AppState(
    val workoutState: WorkoutState = WorkoutSelection(SampleData.WORKOUTS),
    val workouts: List<Workout> = SampleData.WORKOUTS,
    val workout: Workout = Workout.UNSELECTED,
    val remaining: Duration = ZERO,
    val paused: Boolean = true
  ) {

  val name: CharSequence = (workoutState as? WorkingOut)?.name ?: ""
  val duration: Duration = (workoutState as? WorkingOut)?.duration ?: ZERO
}
