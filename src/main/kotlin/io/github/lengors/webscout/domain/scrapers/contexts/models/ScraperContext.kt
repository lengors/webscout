package io.github.lengors.webscout.domain.scrapers.contexts.models

import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseErrorCode
import io.github.lengors.webscout.domain.network.http.services.HttpExchanger
import io.github.lengors.webscout.domain.network.http.services.HttpExchangerProvider
import io.github.lengors.webscout.domain.scrapers.exceptions.handlers.ScraperExceptionHandler
import io.github.lengors.webscout.domain.scrapers.exceptions.handlers.ScraperHandlerExceptionHandler
import io.github.lengors.webscout.domain.scrapers.exceptions.handlers.ScraperRequirementExceptionHandler
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinition
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionHandler

@ConsistentCopyVisibility
data class ScraperContext private constructor(
    val definition: ScraperDefinition,
    val httpExchanger: HttpExchanger,
) {
    val computeDefaultGatesExceptionHandler: ScraperExceptionHandler by lazy {
        ScraperExceptionHandler(ScraperResponseErrorCode.COMPUTE_DEFAULT_GATES, definition.name)
    }

    val computeFlatExpressionExceptionHandler: ScraperExceptionHandler by lazy {
        with(null as ScraperDefinitionHandler?) {
            computeFlatExpressionExceptionHandler
        }
    }

    val ScraperDefinitionHandler?.computeFlatExpressionExceptionHandler: ScraperExceptionHandler
        get() = ScraperExceptionHandler(ScraperResponseErrorCode.COMPUTE_FLAT_EXPRESSION, definition.name, this?.name)

    val computeMapsExpressionExceptionHandler: ScraperExceptionHandler by lazy {
        with(null as ScraperDefinitionHandler?) {
            computeMapsExpressionExceptionHandler
        }
    }

    val ScraperDefinitionHandler?.computeMapsExpressionExceptionHandler: ScraperExceptionHandler
        get() = ScraperExceptionHandler(ScraperResponseErrorCode.COMPUTE_MAPS_EXPRESSION, definition.name, this?.name)

    val computeRequestExceptionHandler: ScraperExceptionHandler by lazy {
        with(null as ScraperDefinitionHandler?) {
            computeRequestExceptionHandler
        }
    }

    val ScraperDefinitionHandler?.computeRequestExceptionHandler: ScraperExceptionHandler
        get() = ScraperExceptionHandler(ScraperResponseErrorCode.COMPUTE_REQUEST, definition.name, this?.name)

    val computeResponseExceptionHandler: ScraperExceptionHandler by lazy {
        with(null as ScraperDefinitionHandler?) {
            computeResponseExceptionHandler
        }
    }

    val ScraperDefinitionHandler?.computeResponseExceptionHandler: ScraperExceptionHandler
        get() = ScraperExceptionHandler(ScraperResponseErrorCode.COMPUTE_RESPONSE, definition.name, this?.name)

    val computeReturnExceptionHandler: ScraperExceptionHandler by lazy {
        ScraperExceptionHandler(ScraperResponseErrorCode.COMPUTE_RETURN, definition.name)
    }

    val ScraperDefinitionHandler?.computeReturnExceptionHandler: ScraperExceptionHandler
        get() = ScraperExceptionHandler(ScraperResponseErrorCode.COMPUTE_RETURN, definition.name, this?.name)

    val handlerExceptionHandler: ScraperHandlerExceptionHandler by lazy {
        ScraperHandlerExceptionHandler(definition.name)
    }

    val requirementExceptionHandler: ScraperRequirementExceptionHandler by lazy {
        ScraperRequirementExceptionHandler(definition.name)
    }

    constructor(
        definition: ScraperDefinition,
        httpExchangerProvider: HttpExchangerProvider,
    ) : this(definition, httpExchangerProvider.provide(definition.certificates))
}
