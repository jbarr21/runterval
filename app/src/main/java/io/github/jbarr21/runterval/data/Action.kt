package io.github.jbarr21.runterval.data

import io.github.jbarr21.runterval.data.util.Workout
import org.threeten.bp.temporal.TemporalUnit

sealed class Action {
  data class Init(val workouts: List<Workout>)
  data class SelectWorkout(val workout: Workout)
  data class TimeTick(val amount: Long, val unit: TemporalUnit) : Action()
  class Resume : Action()
  class Pause : Action()
  class Reset : Action()
  class Stop : Action()
}
