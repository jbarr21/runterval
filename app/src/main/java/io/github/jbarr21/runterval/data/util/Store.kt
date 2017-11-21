package io.github.jbarr21.runterval.data.util

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import redux.api.Store

fun <T> Store<T>.observable(): Observable<T> {
  val relay = BehaviorRelay.createDefault<T>(state)
  val subscription = subscribe { relay.accept(state) }
  return relay.hide()
      .doOnDispose { subscription.unsubscribe() }
}
