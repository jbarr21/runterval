package io.github.jbarr21.runterval.data

import org.threeten.bp.Duration

object SampleData {
  val WORKOUTS = listOf(
      Workout("Test", 5.sec(), 4.sec(), Interval(3.sec(), 2.sec(), 2)),
      Workout("Work Workout", 0.sec(), 0.sec(), Interval(40.sec(), 15.sec(), 6)),
      Workout("3x3x3", 10.min(), 10.min(), Interval(3.min(), 3.min(), 3)),
      Workout("Tabata", 5.min(), 5.min(), Interval(20.sec(), 10.sec(), 8)),
      Workout("Sprints", 10.min(), 10.min(), Interval(30.sec(), 1.min(), 10)),
      Workout("3x3x3", 10.min(), 10.min(), Interval(3.min(), 3.min(), 3)),
      Workout("Tabata", 5.min(), 5.min(), Interval(20.sec(), 10.sec(), 8)),
      Workout("Sprints", 10.min(), 10.min(), Interval(30.sec(), 1.min(), 10)),
      Workout("3x3x3", 10.min(), 10.min(), Interval(3.min(), 3.min(), 3)),
      Workout("Tabata", 5.min(), 5.min(), Interval(20.sec(), 10.sec(), 8)),
      Workout("Sprints", 10.min(), 10.min(), Interval(30.sec(), 1.min(), 10)),
      Workout("3x3x3", 10.min(), 10.min(), Interval(3.min(), 3.min(), 3)),
      Workout("Tabata", 5.min(), 5.min(), Interval(20.sec(), 10.sec(), 8)),
      Workout("Sprints", 10.min(), 10.min(), Interval(30.sec(), 1.min(), 10))
  )
}

private fun Int.min(): Duration = Duration.ofMinutes(toLong())
private fun Int.sec(): Duration = Duration.ofSeconds(toLong())
