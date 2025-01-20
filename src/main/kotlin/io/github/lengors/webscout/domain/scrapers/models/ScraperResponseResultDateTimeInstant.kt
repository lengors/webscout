package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTimeInstant
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTimeInstantGrain
import io.github.lengors.webscout.integrations.duckling.models.DucklingDateTimeInstantResponseValue
import java.util.Date

fun fromDucklingResponseValue(responseValue: DucklingDateTimeInstantResponseValue): ScraperResponseResultDateTimeInstant =
    ScraperResponseResultDateTimeInstant(
        Date.from(responseValue.value.toInstant()),
        ScraperResponseResultDateTimeInstantGrain.valueOf(responseValue.grain.name.uppercase()),
    )
