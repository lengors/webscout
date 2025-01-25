package io.github.lengors.webscout.domain.scrapers.exceptions.handlers

import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseError
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseErrorCode
import io.github.lengors.webscout.domain.scrapers.exceptions.models.ScraperHandlerNotFoundException

@JvmInline
value class ScraperHandlerExceptionHandler(
    private val scraperName: String,
) : (Throwable) -> ScraperResponseError {
    override fun invoke(throwable: Throwable): ScraperResponseError =
        when (throwable) {
            is ScraperHandlerNotFoundException ->
                ScraperResponseError(
                    ScraperResponseErrorCode.HANDLER_NOT_FOUND,
                    scraperName,
                    null,
                    "Could not find an handler for specification '$scraperName'",
                )

            else -> ScraperResponseError(ScraperResponseErrorCode.COMPUTE_HANDLER, scraperName, null, throwable.message)
        }
}
