package io.github.jbarr21.runterval.data;

import android.os.Bundle
import androidx.wear.ambient.AmbientModeSupport
import io.github.jbarr21.runterval.data.AmbientStream.AmbientEvent.ENTER
import io.github.jbarr21.runterval.data.AmbientStream.AmbientEvent.EXIT
import io.github.jbarr21.runterval.data.AmbientStream.AmbientEvent.UPDATE

class RxAmbientCallback(private val ambientStream: AmbientStream) : AmbientModeSupport.AmbientCallback() {
  override fun onEnterAmbient(ambientDetails: Bundle?) = ambientStream.onAmbientEvent(ENTER)
  override fun onUpdateAmbient() = ambientStream.onAmbientEvent(UPDATE)
  override fun onExitAmbient() = ambientStream.onAmbientEvent(EXIT)
}
