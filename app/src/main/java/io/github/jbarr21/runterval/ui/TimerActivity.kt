package io.github.jbarr21.runterval.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.support.wear.ambient.AmbientMode
import android.support.wear.ambient.AmbientMode.AmbientCallback
import android.view.View
import android.view.View.GONE
import android.view.View.LAYER_TYPE_SOFTWARE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.jakewharton.rxbinding2.view.RxView
import com.uber.autodispose.autoDisposable
import io.github.jbarr21.runterval.R
import io.github.jbarr21.runterval.R.layout
import io.github.jbarr21.runterval.data.Action.Pause
import io.github.jbarr21.runterval.data.Action.Reset
import io.github.jbarr21.runterval.data.Action.Resume
import io.github.jbarr21.runterval.data.AmbientStream
import io.github.jbarr21.runterval.data.AppState
import io.github.jbarr21.runterval.data.AppStore
import io.github.jbarr21.runterval.data.RxAmbientCallback
import io.github.jbarr21.runterval.data.WorkoutState
import io.github.jbarr21.runterval.data.WorkoutState.WorkingOut
import io.github.jbarr21.runterval.data.util.filterAndMap
import io.github.jbarr21.runterval.data.util.observable
import io.github.jbarr21.runterval.data.util.toTimeText
import io.github.jbarr21.runterval.ui.util.AutoDisposeWearableActivity
import io.github.jbarr21.runterval.ui.util.WearPalette
import io.github.jbarr21.runterval.ui.util.WearPalette.Companion.DEEP_PURPLE
import io.github.jbarr21.runterval.ui.util.WearPalette.Companion.GREEN
import io.reactivex.android.schedulers.AndroidSchedulers
import kotterknife.bindView
import org.koin.android.ext.android.inject
import org.threeten.bp.Duration.ofSeconds
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * Displays the [Workout] timer.
 */
class TimerActivity : AutoDisposeWearableActivity(), AmbientMode.AmbientCallbackProvider {

  private val bg: View by bindView(R.id.bg)
  private val txtTime: TextView by bindView(R.id.time_text)
  private val txtName: TextView by bindView(R.id.name_text)
  private val btnStartPause: FloatingActionButton by bindView(R.id.start_pause_btn)
  private val btnReset: View by bindView(R.id.reset_button)
  private val btnClose: View by bindView(R.id.close_button)

  private val appStore: AppStore by inject()
  private val ambientStream: AmbientStream by inject()

  private val ambientCallback by lazy { RxAmbientCallback(ambientStream) }

  private val deepPurple = WearPalette(DEEP_PURPLE)
  private val green = WearPalette(GREEN)

  private lateinit var ringDrawable: RingDrawable

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_timer)
    setAmbientEnabled()

    setupButtons()
    ringDrawable = RingDrawable(resources)
    bg.background = ringDrawable
    bg.setLayerType(LAYER_TYPE_SOFTWARE, null)

    appStore.observable()
        .observeOn(AndroidSchedulers.mainThread())
        .autoDisposable(this)
        .subscribe(this::setupUi)
  }

  override fun getAmbientCallback(): AmbientCallback = ambientCallback

  @SuppressLint("SetTextI18n")
  private fun setupUi(state: AppState) {
    txtName.text = state.name
    txtTime.text = state.remaining.toTimeText()

    @ColorInt val ringColor: Int = resources.getColor(if (state.paused) R.color.ring_purple else R.color.ring_green)
    @ColorInt val ringColorDarker: Int = resources.getColor(if (state.paused) R.color.ring_purple_darker else R.color.ring_green_darker)
    listOf(btnReset, btnClose).forEach { it.visibility = if (state.paused) VISIBLE else GONE }
    btnStartPause.setImageResource(if (state.paused) R.drawable.ic_play_arrow_24dp else R.drawable.ic_pause_24dp)
    ViewCompat.setBackgroundTintList(btnStartPause, ColorStateList.valueOf(ringColor))

    ringDrawable.apply {
      remainingPct = state.remaining.toMillis() / state.duration.toMillis().toFloat()
      ringColors = Pair(ringColor, ringColorDarker)
      invalidateSelf()
    }

    // TODO: fix animation of timer ring segments
    val animator = ObjectAnimator.ofFloat(ringDrawable, "ringAnimationOffset", 360f).apply {
      duration = ofSeconds(5).toMillis()
      repeatCount = ObjectAnimator.INFINITE
      start()
    }
  }

  private fun setupButtons() {
    RxView.clicks(btnStartPause)
        .debounce(150, MILLISECONDS)
        .map { appStore.state.paused }
        .autoDisposable(this)
        .subscribe { paused -> appStore.dispatch(if (paused) Resume() else Pause()) }

    RxView.clicks(btnReset)
        .autoDisposable(this)
        .subscribe { Toast.makeText(this, "Longpress to Reset", LENGTH_SHORT).show() }

    RxView.longClicks(btnReset)
        .map { appStore.state.workoutState }
        .filterAndMap<WorkoutState, WorkingOut>()
        .autoDisposable(this)
        .subscribe { appStore.dispatch(Reset()) }

    RxView.clicks(btnClose)
        .autoDisposable(this)
        .subscribe { Toast.makeText(this, "Longpress to Exit", LENGTH_SHORT).show() }

    RxView.longClicks(btnClose)
        .autoDisposable(this)
        .subscribe {
          finishAffinity()
          System.exit(0)
        }
  }
}
