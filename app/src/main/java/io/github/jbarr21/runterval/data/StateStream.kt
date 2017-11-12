package io.github.jbarr21.runterval.data

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable

class StateStream {
  private val stateRelay: BehaviorRelay<State> = BehaviorRelay.create()

  fun stateObservable(): Observable<State> = stateRelay.hide()

  fun setState(state: State) {
    stateRelay.accept(state)
  }

  fun peekState(): State = stateRelay.value
}
