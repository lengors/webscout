package io.github.lengors.webscout.domain.jexl.models

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

interface JexlDateTimeAdapter {
    fun now(): ZonedDateTime

    fun pattern(pattern: String): DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
}
