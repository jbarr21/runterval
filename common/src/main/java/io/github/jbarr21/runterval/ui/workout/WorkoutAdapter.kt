package io.github.jbarr21.runterval.ui.workout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.jbarr21.runterval.R
import io.github.jbarr21.runterval.data.Workout
import kotterknife.bindView

internal class WorkoutAdapter(
  private val workouts: List<Workout> = mutableListOf(),
  private val onClick: (Workout) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    WorkoutViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.simple_list_item_1, parent, false),
      onClick)

  override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) = holder.bind(workouts[position])

  override fun getItemCount() = workouts.size

  class WorkoutViewHolder(view: View, private val onClick: (Workout) -> Unit) : RecyclerView.ViewHolder(view) {
    val name: TextView by bindView(R.id.text1)

    fun bind(workout: Workout) {
      name.text = workout.name
      itemView.setOnClickListener { onClick(workout) }
    }
  }
}
