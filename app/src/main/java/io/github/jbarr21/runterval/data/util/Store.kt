package io.github.jbarr21.runterval.data.util

import io.reactivex.Observable
import me.tatarka.redux.SimpleStore
import me.tatarka.redux.Store
import me.tatarka.redux.rx2.FlowableAdapter

fun <T> Store<T>.observable(): Observable<T> = FlowableAdapter.flowable(this as SimpleStore<T>).toObservable()
