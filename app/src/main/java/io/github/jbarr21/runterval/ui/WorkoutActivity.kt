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
import io.github.jbarr21.runterval.data.Action.SelectWorkout
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
import org.koin.android.ext.android.inject

/**
 * Displays the list of [workouts][Workout].
 */
class WorkoutActivity : AutoDisposeWearableActivity() {

  private val appStore: AppStore by inject()

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
    val snapHelper = LinearSnapHelper()
    snapHelper.attachToRecyclerView(list)

    list.apply {
      isCircularScrollingGestureEnabled = false
      isEdgeItemsCenteringEnabled = true
      layoutManager = WearableLinearLayoutManager(this@WorkoutActivity)
      adapter = WorkoutAdapter(workouts.toMutableList(), this@WorkoutActivity::onWorkoutClicked)
    }
  }

  private fun onWorkoutClicked(workout: Workout) {
    appStore.dispatch(SelectWorkout(workout))
  }

  private class WorkoutAdapter(
      private val workouts: MutableList<Workout>,
      private val onClick: (Workout) -> Unit) : Adapter<WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.simple_list_item_1, parent, false)
      return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
      with(workouts[position]) {
        holder.name.text = name
        holder.itemView.setOnClickListener { onClick(this) }
      }
    }

    override fun getItemCount() = workouts.size

    private class WorkoutViewHolder(view: View) : ViewHolder(view) {
      val name: TextView by bindView(id.text1)
    }
  }
}
