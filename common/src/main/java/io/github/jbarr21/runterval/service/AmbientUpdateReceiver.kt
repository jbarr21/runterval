package io.github.jbarr21.runterval.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import io.github.jbarr21.runterval.data.Action
import io.github.jbarr21.runterval.data.AppState
import io.github.jbarr21.runterval.data.WorkoutState
import me.tatarka.redux.Dispatcher
import me.tatarka.redux.Store
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
class AmbientUpdateReceiver : BroadcastReceiver() {

  @Inject lateinit var alarmManager: AlarmManager
  @Inject lateinit var store: Store<AppState>
  @Inject lateinit var dispatcher: Dispatcher<Action, Action>

  companion object {
    private val AMBIENT_UPDATE_ACTION = "io.github.jbarr21.runterval.action.AMBIENT_UPDATE"
    private val AMBIENT_INTERVAL = Duration.ofSeconds(1)

    fun scheduleNextUpdate(context: Context, alarmManager: AlarmManager) {
      val triggerTime = Instant.now().plus(AMBIENT_INTERVAL)
      alarmManager.setAlarmClock(
        AlarmManager.AlarmClockInfo(triggerTime.toEpochMilli(), null),
        createUpdatePendingIntent(context))
    }

    fun createUpdatePendingIntent(context: Context) = PendingIntent.getBroadcast(
      context, 0, Intent(
        AMBIENT_UPDATE_ACTION
      ), PendingIntent.FLAG_UPDATE_CURRENT
    )
  }

  override fun onReceive(context: Context, intent: Intent) {
    refreshDisplayAndSetNextUpdate(context)
  }

  private fun refreshDisplayAndSetNextUpdate(context: Context) {
    store.state
      .takeIf { it.workoutState is WorkoutState.WorkingOut }
      .let { dispatcher.dispatch(Action.TimeTick(1, ChronoUnit.SECONDS)) }

    scheduleNextUpdate(context, alarmManager)
  }
}
