package io.github.jbarr21.runterval.data

import io.github.jbarr21.runterval.app.App
import io.github.jbarr21.runterval.data.Action.Pause
import io.github.jbarr21.runterval.data.Action.Reset
import io.github.jbarr21.runterval.data.Action.Resume
import io.github.jbarr21.runterval.data.Action.SelectWorkout
import io.github.jbarr21.runterval.data.Action.Stop
import io.github.jbarr21.runterval.data.Action.TimeTick
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut.CoolingDown
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut.Intervals
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut.WarmingUp
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut.WorkoutComplete
import io.github.jbarr21.runterval.data.util.Workout
import org.threeten.bp.Duration
import org.threeten.bp.temporal.TemporalUnit
import redux.api.Reducer

class Reducers {
  companion object {
    val app = me.tatarka.redux.Reducer<Action, AppState> { action, state -> state }

    val appReducer = Reducer { appState: AppState, action: Any ->
      when (action) {
        is SelectWorkout -> appState.copy(workout = action.workout, workoutState = WarmingUp(action.workout.warmup), remaining = action.workout.warmup)
        is TimeTick -> timeTickReducer(appState, action.amount, action.unit)
        is Reset -> appState.copy(remaining = appState.duration, paused = true)
        is Resume -> appState.copy(paused = false)
        is Pause -> appState.copy(paused = true)
        is Stop -> appState.copy(workoutState = WorkoutComplete(Workout.UNSELECTED), paused = true)
        else -> appState
      }
    }

    private fun timeTickReducer(state: AppState, amount: Long, unit: TemporalUnit): AppState {
      if (state.workoutState !is WorkingOut) return state

      val remainAfterUpdate = state.remaining.minus(amount, unit)
      val enoughForUpdate = remainAfterUpdate.toMillis() > 0
      val postUpdateMillis: Long = Math.abs(remainAfterUpdate.toMillis())

      return if (enoughForUpdate) {
        // enough time in current stage, so just subtract time
        state.copy(remaining = remainAfterUpdate)
      } else when (state.workoutState) {
        // move to next stage and subtract off any leftover time
        is WarmingUp -> state.workout.interval.let { state.copy(workoutState = Intervals(it), remaining = it.work.minusMillis(postUpdateMillis)) }
        is Intervals -> timeTickIntervalsReducer(state, state.workoutState, postUpdateMillis)
        is CoolingDown -> state.copy(remaining = Duration.ZERO)
      }
    }

    private fun timeTickIntervalsReducer(state: AppState, workoutState: Intervals, postUpdateMillis: Long): AppState {
      val newWorkoutState = when {
        !workoutState.working -> workoutState.copy(working = true, set = workoutState.set + 1)      // if resting, go to work
        workoutState.set == state.workout.interval.sets - 1 -> CoolingDown(state.workout.cooldown)  // if working and last set, go to cooldown
        else -> workoutState.copy(working = false)                                                  // else working, go to rest
      }
      return state.copy(workoutState = newWorkoutState, remaining = newWorkoutState.duration.minusMillis(postUpdateMillis))
    }
  }
}
