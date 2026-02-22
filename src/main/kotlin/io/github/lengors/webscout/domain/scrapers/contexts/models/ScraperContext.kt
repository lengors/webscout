package io.github.lengors.webscout.domain.scrapers.contexts.models

import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseErrorCode
import io.github.lengors.webscout.domain.network.http.services.HttpExchanger
import io.github.lengors.webscout.domain.network.http.services.HttpExchangerProvider
import io.github.lengors.webscout.domain.network.http.services.HttpSession
import io.github.lengors.webscout.domain.network.http.services.HttpSessionProvider
import io.github.lengors.webscout.domain.scrapers.exceptions.handlers.ScraperExceptionHandler
import io.github.lengors.webscout.domain.scrapers.exceptions.handlers.ScraperHandlerExceptionHandler
import io.github.lengors.webscout.domain.scrapers.exceptions.handlers.ScraperRequirementExceptionHandler
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinition

data class ScraperContext(
    val definition: ScraperDefinition,
    private val httpSessionProvider: HttpSessionProvider,
    private val httpExchangerProvider: HttpExchangerProvider
) {
    val computeDefaultGatesExceptionHandler: ScraperExceptionHandler by lazy {
        ScraperExceptionHandler(ScraperResponseErrorCode.COMPUTE_DEFAULT_GATES, definition.name)
    }

    val computeFlatExpressionExceptionHandler: ScraperExceptionHandler by lazy {
        ScraperExceptionHandler(ScraperResponseErrorCode.COMPUTE_FLAT_EXPRESSION, definition.name)
    }

    val computeMapsExpressionExceptionHandler: ScraperExceptionHandler by lazy {
        ScraperExceptionHandler(ScraperResponseErrorCode.COMPUTE_MAPS_EXPRESSION, definition.name)
    }

    val computeRequestExceptionHandler: ScraperExceptionHandler by lazy {
        ScraperExceptionHandler(ScraperResponseErrorCode.COMPUTE_REQUEST, definition.name)
    }

    val computeResponseExceptionHandler: ScraperExceptionHandler by lazy {
        ScraperExceptionHandler(ScraperResponseErrorCode.COMPUTE_RESPONSE, definition.name)
    }

    val computeReturnExceptionHandler: ScraperExceptionHandler by lazy {
        ScraperExceptionHandler(ScraperResponseErrorCode.COMPUTE_RETURN, definition.name)
    }

    val handlerExceptionHandler: ScraperHandlerExceptionHandler by lazy {
        ScraperHandlerExceptionHandler(definition.name)
    }

    val httpExchanger: HttpExchanger by lazy {
        httpExchangerProvider.provide(definition.certificates)
    }

    val httpSession: HttpSession by lazy {
        httpSessionProvider.provide()
    }

    val requirementExceptionHandler: ScraperRequirementExceptionHandler by lazy {
        ScraperRequirementExceptionHandler(definition.name)
    }
}
