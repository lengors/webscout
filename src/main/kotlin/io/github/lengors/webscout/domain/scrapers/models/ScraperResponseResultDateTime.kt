package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTime
import io.github.lengors.webscout.integrations.duckling.models.DucklingDateTimeInstantResponseValue
import io.github.lengors.webscout.integrations.duckling.models.DucklingDateTimeRangeResponseValue
import io.github.lengors.webscout.integrations.duckling.models.DucklingDateTimeResponseValue

fun fromDucklingResponseValue(responseValue: DucklingDateTimeResponseValue): ScraperResponseResultDateTime =
    when (responseValue) {
        is DucklingDateTimeInstantResponseValue -> fromDucklingResponseValue(responseValue)
        is DucklingDateTimeRangeResponseValue -> fromDucklingResponseValue(responseValue)
    }
