package io.github.jbarr21.runterval.data

import io.reactivex.Observable

inline fun <reified T, reified R> Observable<T>.filterAndMap(): Observable<R> {
  return compose { it.filter { it is R }.map { it as R } }
}
