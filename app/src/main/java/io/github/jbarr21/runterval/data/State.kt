package io.github.jbarr21.runterval.data

import io.github.jbarr21.runterval.data.State.CoolingDown
import io.github.jbarr21.runterval.data.State.Intervals
import io.github.jbarr21.runterval.data.State.WarmingUp
import org.threeten.bp.Duration
import org.threeten.bp.Duration.ofMillis
import org.threeten.bp.temporal.TemporalUnit

sealed class State {
  data class WorkoutSelection(val workouts: List<Workout>) : State()

  data class WarmingUp(
      override val workout: Workout,
      override val remaining: Duration = workout.warmup,
      override val paused: Boolean = true) : State(), WorkingOut {

    override fun name() = "Warmup"
    override fun togglePause(paused: Boolean) = copy(paused = paused)
    override fun reset() = copy(paused = paused, remaining = duration())
    override fun duration() = workout.warmup
  }

  data class Intervals(
      override val workout: Workout,
      val interval: Interval = workout.interval,
      override val remaining: Duration = workout.interval.work,
      val set: Int = 0,
      val working: Boolean = true,
      override val paused: Boolean = false) : State(), WorkingOut {

    override fun name() = "${if (working) "Work" else "Rest"} ${set + 1}"
    override fun togglePause(paused: Boolean) = copy(paused = paused)
    override fun reset() = copy(paused = paused, remaining = duration())
    override fun duration() = if (working) interval.work else interval.rest
  }

  data class CoolingDown(
      override val workout: Workout,
      override val remaining: Duration,
      override val paused: Boolean = false) : State(), WorkingOut {

    override fun name() = "Cooldown"
    override fun togglePause(paused: Boolean) = copy(paused = paused)
    override fun reset() = copy(paused = paused, remaining = workout.cooldown)
    override fun duration() = workout.cooldown
  }

  data class WorkoutComplete(val workout: Workout) : State()
}

interface WorkingOut {
  val workout: Workout
  val remaining: Duration
  val paused: Boolean

  fun name(): String
  fun togglePause(paused: Boolean): WorkingOut
  fun reset(): WorkingOut
  fun duration(): Duration

  fun updateAfterTick(amount: Long, unit: TemporalUnit): State {
    val remainAfterUpdate = remaining.minusMillis(amount)
    val enoughForUpdate = remainAfterUpdate.toMillis() >= 0
    val remainAfterUpdateMillis: Long = Math.abs(remainAfterUpdate.toMillis())

    // TODO: fix carrying of the PAUSED
    return when (this) {
      is WarmingUp -> {
        if (enoughForUpdate) {
          copy(remaining = remaining.minus(amount, unit))
        } else {
          // begin intervals minus leftover time
          Intervals(workout, remaining = workout.interval.work.minusMillis(remainAfterUpdateMillis))
        }
      }
      is Intervals -> {
        when {
          // continue in state
          enoughForUpdate -> copy(remaining = remaining.minus(amount, unit))
          // if no resting, go to work
          !working -> copy(workout, working = true, remaining = interval.work.minusMillis(remainAfterUpdateMillis), set = set + 1)
          // if working and last set, go to cooldown
          set == interval.sets - 1 -> CoolingDown(workout, remaining = workout.cooldown.minusMillis(remainAfterUpdateMillis))
          // else working, go to rest
          else -> copy(workout, working = false, remaining = interval.rest.minusMillis(remainAfterUpdateMillis))
        }
      }
      is CoolingDown ->  {
        if (enoughForUpdate) {
          copy(remaining = remaining.minus(amount, unit))
        } else {
          copy(remaining = ofMillis(0), paused = true)
        }
      }
      else -> throw IllegalStateException("Invalidate state when toggling pause")
    }
  }
}
