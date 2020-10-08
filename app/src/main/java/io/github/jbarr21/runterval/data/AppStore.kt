package io.github.jbarr21.runterval.data

import io.github.jbarr21.runterval.data.WorkoutState.WorkoutSelection
import io.github.jbarr21.runterval.data.util.SampleData
import me.tatarka.redux.SimpleStore

class AppStore : SimpleStore<AppState>(AppState(workoutState = WorkoutSelection(SampleData.WORKOUTS), paused = true))
