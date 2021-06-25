package io.github.jbarr21.runterval.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.jbarr21.runterval.ui.launcher.LauncherScreen
import io.github.jbarr21.runterval.ui.timer.TimerScreen
import io.github.jbarr21.runterval.ui.workout.WorkoutScreen

@Composable
fun RuntervalApp() {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = Screen.Launcher.toString()) {
    composable(Screen.Launcher.toString()) { LauncherScreen(navController) }
    composable(Screen.Workouts.toString()) { WorkoutScreen(navController) }
    composable(Screen.Timer.toString()) { TimerScreen(hiltViewModel()) }
  }
}
