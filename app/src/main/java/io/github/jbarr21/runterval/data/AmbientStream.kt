package io.github.jbarr21.runterval.data

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable

class AmbientStream {
  private val ambientRelay: BehaviorRelay<AmbientEvent> = BehaviorRelay.create()

  fun ambientObservable(): Observable<AmbientEvent> = ambientRelay.hide()

  fun onAmbientEvent(ambient: AmbientEvent) {
    ambientRelay.accept(ambient)
  }

  fun peekAmbient(): AmbientEvent = ambientRelay.value

  enum class AmbientEvent { ENTER, UPDATE, EXIT }
}