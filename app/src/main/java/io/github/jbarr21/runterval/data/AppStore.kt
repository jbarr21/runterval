package io.github.jbarr21.runterval.data

import io.github.jbarr21.runterval.data.WorkoutState.WorkoutSelection
import io.github.jbarr21.runterval.data.util.SampleData
import redux.api.Store
import redux.createStore

class AppStore : Store<AppState> by store {
  companion object {
    val store = createStore(Reducers.appReducer, AppState(workoutState = WorkoutSelection(SampleData.WORKOUTS), paused = true))
  }
}
