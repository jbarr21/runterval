package io.github.jbarr21.runterval.ui.util

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport

abstract class RuntervalActivity : FragmentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val controller = AmbientModeSupport.attach(this)
    val isAmbient = controller.isAmbient
  }
}
