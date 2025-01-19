package io.github.lengors.webscout.domain.scrapers.exceptions.handlers

import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseError
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseErrorCode

data class ScraperExceptionHandler(
    private val scraperResponseErrorCode: ScraperResponseErrorCode,
    private val scraperName: String,
    private val scraperHandlerName: String? = null,
) : (Throwable) -> ScraperResponseError {
    override fun invoke(throwable: Throwable): ScraperResponseError =
        ScraperResponseError(scraperResponseErrorCode, scraperName, scraperHandlerName, throwable.message)
}
