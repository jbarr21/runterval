package io.github.jbarr21.runterval.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import me.tatarka.redux.SimpleStore
import me.tatarka.redux.Store
import me.tatarka.redux.rx2.FlowableAdapter

fun <T : Any> Store<T>.asFlow(): Flow<T> = FlowableAdapter.flowable(this as SimpleStore<T>).asFlow()
