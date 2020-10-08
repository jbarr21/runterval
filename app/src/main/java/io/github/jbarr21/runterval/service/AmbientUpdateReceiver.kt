package io.github.jbarr21.runterval.service

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.Application
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.jbarr21.runterval.data.Action
import io.github.jbarr21.runterval.data.Action.TimeTick
import io.github.jbarr21.runterval.data.AppStore
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut
import me.tatarka.redux.Dispatcher
import org.koin.android.ext.android.inject
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit

class AmbientUpdateReceiver : BroadcastReceiver() {

  companion object {
    private val AMBIENT_UPDATE_ACTION = "io.github.jbarr21.runterval.action.AMBIENT_UPDATE"
    private val AMBIENT_INTERVAL = Duration.ofSeconds(1)

    fun scheduleNextUpdate(context: Context) {
      val alarmManager: AlarmManager by (context.applicationContext as Application).inject()
      val triggerTime = Instant.now().plus(AMBIENT_INTERVAL)
      alarmManager.setAlarmClock(AlarmClockInfo(triggerTime.toEpochMilli(), null), createUpdatePendingIntent(context))
    }

    fun createUpdatePendingIntent(context: Context) = PendingIntent.getBroadcast(context, 0, Intent(AMBIENT_UPDATE_ACTION), FLAG_UPDATE_CURRENT)

    private fun refreshDisplayAndSetNextUpdate(context: Context) {
      val app = context.applicationContext as Application
      val appStore: AppStore by app.inject()
      val dispatcher: Dispatcher<Action, Action> by app.inject()

      appStore.state
        .takeIf { it.workoutState is WorkingOut }
        .let { dispatcher.dispatch(TimeTick(1, ChronoUnit.SECONDS)) }

      scheduleNextUpdate(context)
    }
  }

  override fun onReceive(context: Context, intent: Intent) {
    refreshDisplayAndSetNextUpdate(context)
  }
}
