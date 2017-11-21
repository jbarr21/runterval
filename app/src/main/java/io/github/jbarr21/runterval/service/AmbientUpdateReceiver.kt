package io.github.jbarr21.runterval.service

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.jbarr21.runterval.app.bindInstance
import io.github.jbarr21.runterval.data.Action.TimeTick
import io.github.jbarr21.runterval.data.AppStore
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit

class AmbientUpdateReceiver : BroadcastReceiver() {

  companion object {
    private val AMBIENT_UPDATE_ACTION = "io.github.jbarr21.runterval.action.AMBIENT_UPDATE"
    private val AMBIENT_INTERVAL = Duration.ofSeconds(1)

    fun refreshDisplayAndSetNextUpdate(context: Context) {
      val appStore by context.bindInstance<AppStore>()
      appStore.state
          .takeIf { it.workoutState is WorkingOut }
          .let { appStore.dispatch(TimeTick(1, ChronoUnit.SECONDS)) }

      scheduleNextUpdate(context)
    }

    fun scheduleNextUpdate(context: Context) {
      val triggerTime = Instant.now().plus(AMBIENT_INTERVAL)
      val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)
      alarmManager.setAlarmClock(AlarmClockInfo(triggerTime.toEpochMilli(), null), createUpdatePendingIntent(context))
    }

    fun createUpdatePendingIntent(context: Context): PendingIntent {
      return PendingIntent.getBroadcast(context, 0, Intent(AMBIENT_UPDATE_ACTION), FLAG_UPDATE_CURRENT)
    }
  }

  override fun onReceive(context: Context, intent: Intent) {
    refreshDisplayAndSetNextUpdate(context)
  }
}
