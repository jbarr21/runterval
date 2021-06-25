package io.github.jbarr21.runterval.data

import org.threeten.bp.temporal.TemporalUnit

sealed class Action {
  data class Init(val workouts: List<Workout>) : Action()
  data class SelectWorkout(val workout: Workout) : Action()
  data class TimeTick(val amount: Long, val unit: TemporalUnit) : Action()
  object Resume : Action()
  object Pause : Action()
  object Reset : Action()
  object Stop : Action()
}
