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
import com.uber.autodispose.kotlin.autoDisposeWith
import io.github.jbarr21.runterval.R.id
import io.github.jbarr21.runterval.R.layout
import io.github.jbarr21.runterval.app.bindInstance
import io.github.jbarr21.runterval.data.State
import io.github.jbarr21.runterval.data.State.WarmingUp
import io.github.jbarr21.runterval.data.State.WorkoutSelection
import io.github.jbarr21.runterval.data.StateStream
import io.github.jbarr21.runterval.data.Workout
import io.github.jbarr21.runterval.data.filterAndMap
import io.github.jbarr21.runterval.ui.WorkoutActivity.WorkoutAdapter.WorkoutViewHolder
import kotlinx.android.synthetic.main.activity_workouts.*
import kotterknife.bindView

class WorkoutActivity : AutoDisposeWearableActivity() {

  private val stateStream by bindInstance<StateStream>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_workouts)
    setAmbientEnabled()

    stateStream.stateObservable()
        .filterAndMap<State, WorkoutSelection>()
        .take(1)
        .autoDisposeWith(this)
        .subscribe { setupUi(it.workouts) }

    stateStream.stateObservable()
        .filterAndMap<State, WarmingUp>()
        .take(1)
        .autoDisposeWith(this)
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
    stateStream.setState(WarmingUp(workout))
  }

  private class WorkoutAdapter(
      private val workouts: MutableList<Workout>,
      private val onClick: (Workout) -> Unit) : Adapter<WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(layout.simple_list_item_1, parent, false)
      return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
      with(workouts[position]) {
        holder.name.text = name
        holder.name.setOnClickListener { onClick(this) }
      }
    }

    override fun getItemCount() = workouts.size

    private class WorkoutViewHolder(view: View) : ViewHolder(view) {
      val name: TextView by bindView(id.text1)
    }
  }
}
