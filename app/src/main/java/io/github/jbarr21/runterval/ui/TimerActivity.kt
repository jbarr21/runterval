package io.github.jbarr21.runterval.ui

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Vibrator
import android.support.annotation.ColorInt
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.uber.autodispose.kotlin.autoDisposeWith
import io.github.jbarr21.runterval.R
import io.github.jbarr21.runterval.R.layout
import io.github.jbarr21.runterval.app.bindInstance
import io.github.jbarr21.runterval.data.State
import io.github.jbarr21.runterval.data.State.CoolingDown
import io.github.jbarr21.runterval.data.StateStream
import io.github.jbarr21.runterval.data.WorkingOut
import io.github.jbarr21.runterval.data.filterAndMap
import io.github.jbarr21.runterval.data.toTimeText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotterknife.bindView
import java.util.concurrent.TimeUnit.MILLISECONDS

class TimerActivity : AutoDisposeWearableActivity() {

  private val bg: View by bindView(R.id.bg)
  private val txtTime: TextView by bindView(R.id.time_text)
  private val txtName: TextView by bindView(R.id.name_text)
  private val btnStartPause: FloatingActionButton by bindView(R.id.start_pause_btn)
  private val btnReset: View by bindView(R.id.reset_button)
  private val btnClose: View by bindView(R.id.close_button)

  private val stateStream by bindInstance<StateStream>()

  private lateinit var ringDrawable: RingDrawable

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_timer)
    setAmbientEnabled()
    setupButtons()
    ringDrawable = RingDrawable(resources.getColor(R.color.purple))
    bg.background = ringDrawable

    stateStream.stateObservable()
        .filterAndMap<State, WorkingOut>()
        .observeOn(AndroidSchedulers.mainThread())
        .autoDisposeWith(this)
        .subscribe(this::setupUi)

    val stateChanges = stateStream.stateObservable()
        .filterAndMap<State, WorkingOut>()
        .map { it.name() }
        .distinctUntilChanged()

    val workoutCompletions = stateStream.stateObservable()
        .filterAndMap<State, CoolingDown>()
        .filter { it.remaining.toMillis() == 0L }

    Observable.merge(stateChanges, workoutCompletions)
        .skip(1)
        .observeOn(AndroidSchedulers.mainThread())
        .autoDisposeWith(this)
        .subscribe { getSystemService(Vibrator::class.java)?.vibrate(1000) }
  }

  @SuppressLint("SetTextI18n")
  private fun setupUi(state: WorkingOut) {
    txtName.text = state.name()
    txtTime.text = state.remaining.toTimeText()

    @ColorInt val timerColor: Int = resources.getColor(if (state.paused) R.color.purple else R.color.green)
    listOf(btnReset, btnClose).forEach { it.visibility = if (state.paused) VISIBLE else GONE }
    btnStartPause.setImageResource(if (state.paused) R.drawable.ic_play_arrow_24dp else R.drawable.ic_pause_24dp)
    ViewCompat.setBackgroundTintList(btnStartPause, ColorStateList.valueOf(timerColor))

    ringDrawable.apply {
      remainingPct = state.remaining.toMillis() / state.duration().toMillis().toFloat()
      ringColor = timerColor
      invalidateSelf()
    }
  }

  private fun setupButtons() {
    RxView.clicks(btnStartPause)
        .debounce(150, MILLISECONDS)
        .map { stateStream.peekState() }
        .filterAndMap<State, WorkingOut>()
        .autoDisposeWith(this)
        .subscribe { stateStream.setState(it.togglePause(paused = !it.paused) as State) }

    // TODO: set remaining time to current interval time
    RxView.clicks(btnReset)
        .autoDisposeWith(this)
        .subscribe { Toast.makeText(this, "Reset Timer", Toast.LENGTH_SHORT).show() }

    RxView.clicks(btnClose)
        .autoDisposeWith(this)
        .subscribe { Toast.makeText(this, "Longpress to Exit", Toast.LENGTH_SHORT).show() }

    RxView.longClicks(btnClose)
        .autoDisposeWith(this)
        .subscribe {
          finishAffinity()
          System.exit(0)
        }
  }
}
