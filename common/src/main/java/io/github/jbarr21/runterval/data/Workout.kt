package io.github.jbarr21.runterval.data

import org.threeten.bp.Duration
import org.threeten.bp.Duration.ZERO
import org.threeten.bp.Duration.ofSeconds

data class Workout(
    val name: String,
    val warmup: Duration = ofSeconds(0),
    val cooldown: Duration = ofSeconds(0),
    val interval: Interval // TODO: add support for list
  ) {

  companion object {
    val UNSELECTED = Workout("Unselected", ZERO, ZERO, Interval(ZERO, ZERO, 0))
  }
}

data class Interval(
    val work: Duration,
    val rest: Duration,
    val sets: Int,
    val name: String? = null
    , val skipLastRest: Boolean = true // TODO: make this configurable
)
