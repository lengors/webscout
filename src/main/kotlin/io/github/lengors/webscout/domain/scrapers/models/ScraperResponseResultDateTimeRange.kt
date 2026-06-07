package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTimeRange
import io.github.lengors.webscout.integrations.duckling.models.DucklingDateTimeRangeResponseValue

fun fromDucklingResponseValue(responseValue: DucklingDateTimeRangeResponseValue): ScraperResponseResultDateTimeRange =
    ScraperResponseResultDateTimeRange(
        fromDucklingResponseValue(responseValue.from),
        fromDucklingResponseValue(responseValue.to),
    )
