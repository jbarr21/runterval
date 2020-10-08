package io.github.jbarr21.runterval.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.wear.widget.WearableLinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.uber.autodispose.autoDisposable
import io.github.jbarr21.runterval.R
import io.github.jbarr21.runterval.R.id
import io.github.jbarr21.runterval.R.layout
import io.github.jbarr21.runterval.data.Action
import io.github.jbarr21.runterval.data.Action.SelectWorkout
import io.github.jbarr21.runterval.data.AppState
import io.github.jbarr21.runterval.data.AppStore
import io.github.jbarr21.runterval.data.WorkoutState
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut
import io.github.jbarr21.runterval.data.WorkoutState.WorkoutSelection
import io.github.jbarr21.runterval.data.util.Workout
import io.github.jbarr21.runterval.data.util.filterAndMap
import io.github.jbarr21.runterval.data.util.observable
import io.github.jbarr21.runterval.ui.WorkoutActivity.WorkoutAdapter.WorkoutViewHolder
import io.github.jbarr21.runterval.ui.util.AutoDisposeWearableActivity
import kotlinx.android.synthetic.main.activity_workouts.*
import kotterknife.bindView
import me.tatarka.redux.Dispatcher
import org.koin.android.ext.android.inject

/**
 * Displays the list of [workouts][Workout].
 */
class WorkoutActivity : AutoDisposeWearableActivity() {

  private val appStore: AppStore by inject()
  private val dispatcher: Dispatcher<Action, Action> by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_workouts)
    setAmbientEnabled()

    appStore.observable()
        .map { it.workoutState }
        .filterAndMap<WorkoutState, WorkoutSelection>()
        .take(1)
        .autoDisposable(this)
        .subscribe { setupUi(it.workouts) }

    appStore.observable()
        .map { it.workoutState }
        .filterAndMap<WorkoutState, WorkingOut>()
        .take(1)
        .autoDisposable(this)
        .subscribe {
          startActivity(Intent(this, TimerActivity::class.java))
          finish()
        }
  }

  private fun setupUi(workouts: List<Workout>) {
    LinearSnapHelper().attachToRecyclerView(list)
    list.apply {
      isCircularScrollingGestureEnabled = false
      isEdgeItemsCenteringEnabled = true
      layoutManager = WearableLinearLayoutManager(this@WorkoutActivity)
      adapter = WorkoutAdapter(onClick = { dispatcher.dispatch(SelectWorkout(it)) })
    }
  }

  private class WorkoutAdapter(
      private val workouts: MutableList<Workout> = mutableListOf(),
      private val onClick: (Workout) -> Unit) : Adapter<WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
      WorkoutViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.simple_list_item_1, parent, false),
        onClick)

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) = holder.bind(workouts[position])

    override fun getItemCount() = workouts.size

    private class WorkoutViewHolder(view: View, private val onClick: (Workout) -> Unit) : ViewHolder(view) {
      val name: TextView by bindView(id.text1)

      fun bind(workout: Workout) {
        name.text = workout.name
        itemView.setOnClickListener { onClick(workout) }
      }
    }
  }
}
