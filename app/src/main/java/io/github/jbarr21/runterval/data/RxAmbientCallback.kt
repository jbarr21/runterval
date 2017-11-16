package io.github.jbarr21.runterval.data;

import android.os.Bundle
import android.support.wear.ambient.AmbientMode
import io.github.jbarr21.runterval.data.AmbientStream.AmbientEvent.ENTER
import io.github.jbarr21.runterval.data.AmbientStream.AmbientEvent.EXIT
import io.github.jbarr21.runterval.data.AmbientStream.AmbientEvent.UPDATE

class RxAmbientCallback(private val ambientStream: AmbientStream) : AmbientMode.AmbientCallback() {
  override fun onEnterAmbient(ambientDetails: Bundle?) = ambientStream.onAmbientEvent(ENTER)
  override fun onUpdateAmbient() = ambientStream.onAmbientEvent(UPDATE)
  override fun onExitAmbient() = ambientStream.onAmbientEvent(EXIT)
}
