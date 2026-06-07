package io.github.lengors.webscout.domain.jexl.models

import java.time.ZoneId
import java.time.ZonedDateTime

@JvmInline
value class JexlContextDateTimeAdapter(
    private val timezone: ZoneId,
) : JexlDateTimeAdapter {
    override fun now(): ZonedDateTime = ZonedDateTime.now(timezone)
}
