package io.github.jbarr21.runterval.ui.workout

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.jbarr21.runterval.R
import io.github.jbarr21.runterval.R.layout
import io.github.jbarr21.runterval.data.Action
import io.github.jbarr21.runterval.data.Action.SelectWorkout
import io.github.jbarr21.runterval.data.AppState
import io.github.jbarr21.runterval.data.Workout
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut
import io.github.jbarr21.runterval.data.WorkoutState.WorkoutSelection
import io.github.jbarr21.runterval.data.asFlow
import io.github.jbarr21.runterval.ui.timer.TimerActivity
import io.github.jbarr21.runterval.ui.util.RuntervalActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotterknife.bindView
import me.tatarka.redux.Dispatcher
import me.tatarka.redux.Store
import javax.inject.Inject

/** Displays the list of [workouts][Workout]. */
@AndroidEntryPoint
class WorkoutActivity : RuntervalActivity() {

  @Inject lateinit var store: Store<AppState>
  @Inject lateinit var dispatcher: Dispatcher<Action, Action>

  private val list: WearableRecyclerView by bindView(R.id.list)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_workouts)
    // setAmbientEnabled()

    store.asFlow()
      .map { it.workoutState }
      .onEach {
        when (it) {
          is WorkoutSelection -> setupUi(it.workouts)
          is WorkingOut -> {
            startActivity(Intent(this, TimerActivity::class.java))
            finish()
          }
        }
      }
      .launchIn(lifecycleScope)
  }

  private fun setupUi(workouts: List<Workout>) {
    LinearSnapHelper().attachToRecyclerView(list)
    list.apply {
      isCircularScrollingGestureEnabled = false
      isEdgeItemsCenteringEnabled = true
      layoutManager = WearableLinearLayoutManager(this@WorkoutActivity)
      adapter = WorkoutAdapter(workouts = workouts, onClick = { dispatcher.dispatch(SelectWorkout(it)) })
    }
  }
}
