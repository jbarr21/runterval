package io.github.jbarr21.runterval.ui.util

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import com.jakewharton.rxrelay2.BehaviorRelay
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import com.uber.autodispose.lifecycle.LifecycleScopes
import io.github.jbarr21.runterval.ui.util.AutoDisposeWearableActivity.ActivityEvent
import io.github.jbarr21.runterval.ui.util.AutoDisposeWearableActivity.ActivityEvent.CREATE
import io.github.jbarr21.runterval.ui.util.AutoDisposeWearableActivity.ActivityEvent.DESTROY
import io.github.jbarr21.runterval.ui.util.AutoDisposeWearableActivity.ActivityEvent.PAUSE
import io.github.jbarr21.runterval.ui.util.AutoDisposeWearableActivity.ActivityEvent.RESUME
import io.github.jbarr21.runterval.ui.util.AutoDisposeWearableActivity.ActivityEvent.START
import io.github.jbarr21.runterval.ui.util.AutoDisposeWearableActivity.ActivityEvent.STOP

abstract class AutoDisposeWearableActivity : WearableActivity(), LifecycleScopeProvider<ActivityEvent> {

  private val lifecycleRelay = BehaviorRelay.create<ActivityEvent>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycleRelay.accept(CREATE)
  }

  override fun onStart() {
    super.onStart()
    lifecycleRelay.accept(START)
  }

  override fun onResume() {
    super.onResume()
    lifecycleRelay.accept(RESUME)
  }

  override fun onPause() {
    super.onPause()
    lifecycleRelay.accept(PAUSE)
  }

  override fun onStop() {
    super.onStop()
    lifecycleRelay.accept(STOP)
  }

  override fun onDestroy() {
    super.onDestroy()
    lifecycleRelay.accept(DESTROY)
  }

  override fun lifecycle() = lifecycleRelay.hide()

  override fun correspondingEvents() = ActivityEvent.LIFECYCLE

  override fun peekLifecycle() = lifecycleRelay.value

  override fun requestScope() = LifecycleScopes.resolveScopeFromLifecycle(this)

  enum class ActivityEvent {
    CREATE,
    START,
    RESUME,
    PAUSE,
    STOP,
    DESTROY;

    companion object {

      /**
       * Figures out which corresponding next lifecycle event in which to unsubscribe, for Activities.
       */
      val LIFECYCLE = CorrespondingEventsFunction<ActivityEvent> { lastEvent ->
        return@CorrespondingEventsFunction when (lastEvent) {
          CREATE -> DESTROY
          START -> STOP
          RESUME -> PAUSE
          PAUSE -> STOP
          STOP -> DESTROY
          DESTROY -> throw LifecycleEndedException("Cannot bind to Activity lifecycle when outside of it.")
          else -> throw UnsupportedOperationException("Binding to $lastEvent not yet implemented")
        }
      }
    }
  }
}
