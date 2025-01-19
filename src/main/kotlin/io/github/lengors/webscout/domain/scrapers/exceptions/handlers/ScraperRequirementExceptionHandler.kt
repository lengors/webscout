package io.github.lengors.webscout.domain.scrapers.exceptions.handlers

import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseError
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseErrorCode
import io.github.lengors.webscout.domain.scrapers.exceptions.models.ScraperInputMissingException
import io.github.lengors.webscout.domain.scrapers.exceptions.models.ScraperInvalidInputException

@JvmInline
value class ScraperRequirementExceptionHandler(
    private val scraperName: String,
) : (Throwable) -> ScraperResponseError {
    override fun invoke(throwable: Throwable): ScraperResponseError =
        when (throwable) {
            is ScraperInputMissingException ->
                ScraperResponseError(
                    ScraperResponseErrorCode.INPUT_MISSING,
                    scraperName,
                    null,
                    "Input for requirement '${throwable.requirement.name}' of type '${throwable.requirement.type}' missing",
                )

            is ScraperInvalidInputException ->
                ScraperResponseError(
                    ScraperResponseErrorCode.INVALID_INPUT,
                    scraperName,
                    null,
                    "Invalid input for requirement '${throwable.requirement.name}' of type '${throwable.requirement.type}'",
                )

            else ->
                ScraperResponseError(
                    ScraperResponseErrorCode.COMPUTE_INPUT,
                    scraperName,
                    null,
                    throwable.message,
                )
        }
}
