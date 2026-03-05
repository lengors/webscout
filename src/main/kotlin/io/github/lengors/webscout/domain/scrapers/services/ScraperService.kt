package io.github.lengors.webscout.domain.scrapers.services

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponse
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResult
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultBrand
import io.github.lengors.webscout.domain.network.http.models.HttpRequest
import io.github.lengors.webscout.domain.scrapers.contexts.models.ScraperExecutionContext
import io.github.lengors.webscout.domain.scrapers.contexts.services.ScraperContextManager
import io.github.lengors.webscout.domain.scrapers.exceptions.models.ScraperHandlerNotFoundException
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionComputeAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionFlatAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionHandler
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionMapAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionPayloadType
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionRequestAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionReturnAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperTask
import io.github.lengors.webscout.domain.spring.scrapers.models.asHeaders
import io.github.lengors.webscout.domain.spring.scrapers.specifications.models.asHeaders
import io.github.lengors.webscout.domain.spring.scrapers.specifications.models.parse
import io.github.lengors.webscout.domain.utilities.asMultiValueMap
import io.github.lengors.webscout.domain.utilities.runCatching
import io.github.lengors.webscout.integrations.duckling.client.DucklingClient
import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.jexl3.JexlEngine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Service
class ScraperService(
    private val jexlEngine: JexlEngine,
    private val objectMapper: ObjectMapper,
    private val ducklingClient: DucklingClient,
    private val observationRegistry: ObservationRegistry,
    private val scraperContextManager: ScraperContextManager,
    private val scraperHttpSessionManager: ScraperHttpSessionManager,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ScraperService::class.java)
    }

    fun scrap(vararg scraperTasks: ScraperTask): Flow<ScraperResponse> = scrap(scraperTasks.asFlow())

    fun scrap(scraperTasks: Iterable<ScraperTask>): Flow<ScraperResponse> = scrap(scraperTasks.asFlow())

    fun scrap(scraperTasks: Flow<ScraperTask>): Flow<ScraperResponse> = channelFlow { scrap(scraperTasks) }

    suspend fun SendChannel<ScraperResponse>.scrap(vararg scraperTasks: ScraperTask) = scrap(scraperTasks.asFlow())

    suspend fun SendChannel<ScraperResponse>.scrap(scraperTasks: Iterable<ScraperTask>) = scrap(scraperTasks.asFlow())

    suspend fun SendChannel<ScraperResponse>.scrap(scraperTasks: Flow<ScraperTask>) =
        withContext(observationRegistry.asContextElement()) {
            scraperTasks.collect {
                launch { scrap(it) }
            }
        }

    private suspend fun SendChannel<ScraperResponse>.scrap(task: ScraperTask) {
        val context = scraperContextManager.getScraperContext(task.specification)

        val requiredInputs =
            runCatching(logger, context.requirementExceptionHandler) {
                context.definition.requirements.associate { requirement ->
                    requirement.name to
                        task.inputs
                            .getOrDefault(requirement.name, requirement.default)
                            .let(requirement::validate)
                }
            } ?: return

        val httpSession = scraperHttpSessionManager.getHttpSession(task.specification, requiredInputs)
        val scraperExecutionContext =
            ScraperExecutionContext(
                jexlEngine,
                ducklingClient,
                objectMapper,
                context,
                httpSession,
                task.searchTerm,
                requiredInputs,
            )
        runCatching(logger, context.computeDefaultGatesExceptionHandler) {
            scraperExecutionContext.branch(openGates = context.definition.defaultGates)
        }?.let { scrap(it) }
    }

    private suspend fun SendChannel<ScraperResponse>.scrap(executionContext: ScraperExecutionContext) {
        val context = executionContext.context
        val handler =
            runCatching(logger, context.handlerExceptionHandler) {
                with(executionContext) {
                    context.definition.handlers.firstOrNull { handler ->
                        handler.requiresGates
                            .compute(String::class)
                            .filterNotNull()
                            .let(gates::containsAll) &&
                            handler.matches?.takeUnless { it.compute(Boolean::class) == true } == null
                    }
                } ?: throw ScraperHandlerNotFoundException(context.definition.name)
            } ?: return

        logger.info("Scraper handler: (name={})", handler.name)

        return when (handler.action) {
            is ScraperDefinitionFlatAction -> scrap(executionContext, handler, handler.action)
            is ScraperDefinitionComputeAction -> scrap(executionContext, handler, handler.action)
            is ScraperDefinitionReturnAction -> scrap(executionContext, handler, handler.action)
        }
    }

    private suspend fun SendChannel<ScraperResponse>.scrap(
        executionContext: ScraperExecutionContext,
        handler: ScraperDefinitionHandler,
        action: ScraperDefinitionFlatAction,
    ): Unit =
        coroutineScope {
            with(executionContext) {
                with(context) {
                    runCatching(logger, handler.computeFlatExpressionExceptionHandler) {
                        action.flattens.flatMap {
                            it.compute(Iterable::class) ?: emptyList()
                        }
                    }
                }
            }?.let { actions ->
                actions.forEach {
                    launch {
                        scrap(
                            executionContext.branch(
                                visitedHandlerName = handler.name,
                                openGates = handler.opensGates,
                                closeGates = handler.closesGates,
                                value = it,
                            ),
                        )
                    }
                }
            }
        }

    private suspend fun SendChannel<ScraperResponse>.scrap(
        executionContext: ScraperExecutionContext,
        handler: ScraperDefinitionHandler,
        action: ScraperDefinitionComputeAction,
    ): Unit =
        with(executionContext) {
            val mappedValues =
                with(context) {
                    runCatching(logger, handler.computeMapsExpressionExceptionHandler) {
                        action.maps.compute()
                    } ?: return
                }

            val requestReference =
                when (action) {
                    is ScraperDefinitionMapAction -> null
                    is ScraperDefinitionRequestAction ->
                        with(context) {
                            action.let { requestAction ->
                                val httpRequest =
                                    runCatching(logger, handler.computeRequestExceptionHandler) {
                                        val uriComponents = requestAction.url.computeUri(defaultUri)

                                        val uri = URI.create(uriComponents.toUriString())
                                        val defaultHeaders = definition.defaultHeaders.compute(String::class)
                                        val headers = requestAction.headers.compute(String::class)
                                        val fields =
                                            requestAction.payload?.let { payload ->
                                                payload.fields
                                                    .associate {
                                                        val name = it.name.compute(String::class)
                                                        val value = it.value.compute(Any::class)
                                                        name to value?.toString()
                                                    }.let {
                                                        when (payload.type) {
                                                            ScraperDefinitionPayloadType.DATA -> it.asMultiValueMap()
                                                            ScraperDefinitionPayloadType.JSON -> it
                                                        }
                                                    }
                                            }
                                        val requestHeaders =
                                            defaultHeaders
                                                .plus(headers)
                                                .plus(requestAction.payload?.type.asHeaders())
                                                .plus(requestAction.parser.asHeaders())
                                                .entries
                                                .mapNotNull { (key, value) ->
                                                    value?.let { key to it }
                                                }.associate { it }

                                        HttpRequest(uri, requestAction.method, requestHeaders, fields)
                                    } ?: return

                                runCatching(logger, handler.computeResponseExceptionHandler) {
                                    val response = httpExchanger.exchange(httpSession, httpRequest)
                                    val responseBodyContext = branch(value = response.body)
                                    response.uri to requestAction.parser.parse(responseBodyContext, response.headers)
                                } ?: return
                            }
                        }
                }

            val requestValues = requestReference?.second?.let(::listOf) ?: emptyList()
            requestReference?.first to requestValues + mappedValues
        }.let { (uri, output) ->
            scrap(
                executionContext.branch(
                    visitedHandlerName = handler.name,
                    openGates = handler.opensGates,
                    closeGates = handler.closesGates,
                    uri =
                        uri?.let {
                            UriComponentsBuilder
                                .fromUri(it)
                                .build()
                        },
                    value = if (output.size == 1) output[0] else output,
                ),
            )
        }

    private suspend fun SendChannel<ScraperResponse>.scrap(
        executionContext: ScraperExecutionContext,
        handler: ScraperDefinitionHandler,
        action: ScraperDefinitionReturnAction,
    ): Unit =
        with(executionContext) {
            with(context) {
                runCatching(logger, handler.computeReturnExceptionHandler) {
                    ScraperResponseResult(
                        definition.defaultUrl
                            .computeUri()
                            .toUriString(),
                        definition.name,
                        action.description.computeDescription(),
                        ScraperResponseResultBrand(
                            action.brand.description.computeBrand(),
                            action.brand.image.computeUriStringOrNull(),
                        ),
                        action.price.computePrice(),
                        action.image.computeUriStringOrNull(),
                        action.stocks.computeStocks(),
                        action.grip.computeGradingOrNull(),
                        action.noise.computeNoiseLevelOrNull(),
                        action.decibels.computeDecibelsOrNull(),
                        action.consumption.computeGradingOrNull(),
                        action.details.computeDetails(),
                    )
                } ?: return
            }
        }.let { send(it) }
}
