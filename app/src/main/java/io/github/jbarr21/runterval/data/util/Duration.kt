package io.github.jbarr21.runterval.data.util

import org.threeten.bp.Duration

fun Duration.toMinutesPart(): Long = toMinutes() % 60
fun Duration.toSecondsPart(): Long = (Math.ceil((toMillis() % 60000) / 1000.0) % 60).toLong()

fun Duration.toMinutesPartText(): String = toMinutesPart().toString().padStart(2, '0')
fun Duration.toSecondsPartText(): String = toSecondsPart().toString().padStart(2, '0')

fun Duration.toTimeText(): String = "${toMinutesPartText()} ${toSecondsPartText()}"
