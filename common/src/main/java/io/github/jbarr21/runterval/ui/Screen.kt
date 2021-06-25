package io.github.jbarr21.runterval.ui

sealed class Screen(val route: String) {
  object Launcher : Screen("launcher")
  object Workouts : Screen("workouts")
  object Timer : Screen("timer")

  companion object {
    val items = listOf(
      Launcher,
      Workouts,
      Timer
    )
  }
}
