package io.github.lengors.webscout.domain.scrapers.services

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponse
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResult
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultBrand
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDescriptionlessDetail
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDescriptiveDetail
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.events.models.EventListener
import io.github.lengors.webscout.domain.jexl.models.JexlReference
import io.github.lengors.webscout.domain.network.http.models.HttpRequest
import io.github.lengors.webscout.domain.network.http.services.HttpStatefulClientBuilder
import io.github.lengors.webscout.domain.network.ssl.services.SslMaterialLoader
import io.github.lengors.webscout.domain.scrapers.contexts.models.ScraperContext
import io.github.lengors.webscout.domain.scrapers.contexts.models.ScraperExecutionContext
import io.github.lengors.webscout.domain.scrapers.exceptions.models.ScraperHandlerNotFoundException
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinition
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionComputeAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionFlatAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionMapAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionRequestAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionReturnAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperTask
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityBatchDeletedEvent
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityCreatedEvent
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityEvent
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationPersistenceEvent
import io.github.lengors.webscout.domain.spring.scrapers.models.asHeaders
import io.github.lengors.webscout.domain.spring.scrapers.specifications.models.asHeaders
import io.github.lengors.webscout.domain.spring.scrapers.specifications.models.parse
import io.github.lengors.webscout.domain.utilities.VirtualThreadPerTaskExecutor
import io.github.lengors.webscout.domain.utilities.asMultiValueMap
import io.github.lengors.webscout.domain.utilities.mapEachValue
import io.github.lengors.webscout.domain.utilities.runCatching
import io.github.lengors.webscout.integrations.duckling.client.DucklingClient
import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.jexl3.JexlEngine
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Service
class ScraperService(
    private val jexlEngine: JexlEngine,
    private val objectMapper: ObjectMapper,
    private val ducklingClient: DucklingClient,
    private val sslMaterialLoader: SslMaterialLoader,
    private val observationRegistry: ObservationRegistry,
    private val httpStatefulClientBuilder: HttpStatefulClientBuilder,
    @Lazy private val self: ScraperService? = null,
) : EventListener<ScraperSpecificationPersistenceEvent> {
    companion object {
        private val logger = LoggerFactory.getLogger(ScraperService::class.java)
    }

    fun computeScraperDefinition(scraperSpecification: ScraperSpecification): ScraperDefinition =
        ScraperDefinition(scraperSpecification, jexlEngine, sslMaterialLoader)

    @CacheEvict("ScraperContexts", key = "#scraperSpecification.name")
    fun deleteScraperContext(scraperSpecification: ScraperSpecification) {
        logger.info("Discarding scraper for ScraperSpecification(name={})", scraperSpecification.name)
    }

    @Cacheable("ScraperContexts", key = "#scraperSpecification.name")
    fun findScraperContext(scraperSpecification: ScraperSpecification): ScraperContext =
        ScraperContext(computeScraperDefinition(scraperSpecification), httpStatefulClientBuilder)

    override fun onEvent(event: ScraperSpecificationPersistenceEvent) {
        when (event) {
            is ScraperSpecificationEntityCreatedEvent -> Unit
            is ScraperSpecificationEntityEvent -> self?.deleteScraperContext(event.entity) ?: Unit
            is ScraperSpecificationEntityBatchDeletedEvent -> event.entities.forEach { self?.deleteScraperContext(it) }
        }
    }

    fun scrap(vararg scraperTasks: ScraperTask): Flow<ScraperResponse> = scrap(scraperTasks.asFlow())

    fun scrap(scraperTasks: Iterable<ScraperTask>): Flow<ScraperResponse> = scrap(scraperTasks.asFlow())

    fun scrap(scraperTasks: Flow<ScraperTask>): Flow<ScraperResponse> = channelFlow { scrap(scraperTasks) }

    suspend fun SendChannel<ScraperResponse>.scrap(vararg scraperTasks: ScraperTask) = scrap(scraperTasks.asFlow())

    suspend fun SendChannel<ScraperResponse>.scrap(scraperTasks: Iterable<ScraperTask>) = scrap(scraperTasks.asFlow())

    suspend fun SendChannel<ScraperResponse>.scrap(scraperTasks: Flow<ScraperTask>) =
        withContext(observationRegistry.asContextElement() + Dispatchers.VirtualThreadPerTaskExecutor) {
            coroutineScope {
                scraperTasks.collect {
                    launch { scrap(it) }
                }
            }
        }

    private suspend fun SendChannel<ScraperResponse>.scrap(task: ScraperTask) {
        val context = self?.findScraperContext(task.specification) ?: return

        val requiredInputs =
            runCatching(logger, context.requirementExceptionHandler) {
                context.definition.requirements.associate { requirement ->
                    requirement.name to
                        task.inputs
                            .getOrDefault(requirement.name, requirement.default)
                            .let(requirement::validate)
                }
            } ?: return

        val scraperExecutionContext =
            ScraperExecutionContext(
                jexlEngine,
                ducklingClient,
                objectMapper,
                context,
                task.searchTerm,
                requiredInputs,
            )
        runCatching(logger, context.computeDefaultGatesExceptionHandler) {
            scraperExecutionContext.branch(openGates = context.definition.defaultGates)
        }?.let { scrap(it) }
    }

    private suspend fun SendChannel<ScraperResponse>.scrap(executionContext: ScraperExecutionContext): Unit =
        withContext(observationRegistry.asContextElement()) {
            coroutineScope {
                val context = executionContext.context
                val handler =
                    runCatching(logger, context.handlerExceptionHandler) {
                        with(executionContext) {
                            context.definition.handlers.firstOrNull { handler ->
                                handler.matches?.takeUnless { it.compute(Boolean::class).valueOrNull == true } == null &&
                                    handler.requiresGates
                                        .compute(String::class)
                                        .mapNotNull { it.valueOrNull }
                                        .let(gates::containsAll)
                            }
                        } ?: throw ScraperHandlerNotFoundException(context.definition.name)
                    } ?: return@coroutineScope

                logger.info("Scraper handler: (name={})", handler.name)

                when (handler.action) {
                    is ScraperDefinitionFlatAction ->
                        runCatching(logger, context.computeFlatExpressionExceptionHandler) {
                            with(executionContext) {
                                handler.action.flattens
                                    .compute(Iterable::class)
                                    .valueOrNull
                            }
                        }?.let { actions ->
                            actions.forEach {
                                launch {
                                    scrap(
                                        executionContext.branch(
                                            visitedHandlerName = handler.name,
                                            openGates = handler.opensGates,
                                            closeGates = handler.closesGates,
                                            valueOrNull = it,
                                        ),
                                    )
                                }
                            }
                        }

                    is ScraperDefinitionComputeAction ->
                        with(executionContext) {
                            val mappedVaues =
                                runCatching(logger, context.computeMapsExpressionExceptionHandler) {
                                    handler.action.maps
                                        .compute()
                                        .mapNotNull { it.valueOrNull }
                                } ?: return@coroutineScope

                            val requestReference =
                                when (handler.action) {
                                    is ScraperDefinitionMapAction -> null
                                    is ScraperDefinitionRequestAction ->
                                        handler.action.let { requestAction ->
                                            val httpRequest =
                                                runCatching(logger, context.computeRequestExceptionHandler) {
                                                    val defaultUriComponents =
                                                        context.definition.defaultUrl.computeUri()
                                                    val uriComponents =
                                                        requestAction.url.computeUri(defaultUriComponents)

                                                    val uri = URI.create(uriComponents.toUriString())
                                                    val defaultHeaders =
                                                        context.definition.defaultHeaders
                                                            .compute(String::class)
                                                            .mapEachValue(JexlReference<String>::valueOrNull)
                                                    val headers =
                                                        requestAction.headers
                                                            .compute(String::class)
                                                            .mapEachValue(JexlReference<String>::valueOrNull)
                                                    val fields =
                                                        requestAction.payload
                                                            ?.fields
                                                            ?.compute(String::class)
                                                            ?.mapEachValue(JexlReference<String>::valueOrNull)
                                                            ?.asMultiValueMap()

                                                    HttpRequest(
                                                        uri,
                                                        requestAction.method,
                                                        defaultHeaders + headers + requestAction.payload?.type.asHeaders() +
                                                            requestAction.parser.asHeaders(),
                                                        fields,
                                                    )
                                                } ?: return@coroutineScope

                                            runCatching(logger, context.computeResponseExceptionHandler) {
                                                val response = context.httpStatefulClient.exchange(httpRequest)
                                                val responseBodyContext = branch(valueOrNull = response.body)
                                                response.uri to requestAction.parser.parse(responseBodyContext)
                                            } ?: return@coroutineScope
                                        }
                                }

                            val requestValues = requestReference?.second?.valueOrNull?.let(::listOf) ?: emptyList()
                            requestReference?.first to requestValues + mappedVaues
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
                                    valueOrNull = if (output.size == 1) output[0] else output,
                                ),
                            )
                        }

                    is ScraperDefinitionReturnAction ->
                        runCatching(logger, context.computeReturnExceptionHandler) {
                            with(executionContext) {
                                ScraperResponseResult(
                                    context.definition.defaultUrl
                                        .computeUri()
                                        .toUriString(),
                                    context.definition.name,
                                    handler.action.description.computeDescription(),
                                    ScraperResponseResultBrand(
                                        handler.action.brand.description
                                            .computeBrand(),
                                        handler.action.brand.image
                                            .computeUriStringOrNull(),
                                    ),
                                    handler.action.price.computePrice(),
                                    handler.action.image.computeUriStringOrNull(),
                                    handler.action.stocks.computeStocks(),
                                    handler.action.grip.computeGradingOrNull(),
                                    handler.action.noise.computeNoiseLevelOrNull(),
                                    handler.action.decibels.computeDecibelsOrNull(),
                                    handler.action.consumption.computeGradingOrNull(),
                                    handler.action.details.mapNotNull {
                                        it.name
                                            .computeTextOrNull()
                                            ?.let { name ->
                                                it.description
                                                    .computeTextOrNull()
                                                    ?.let { description ->
                                                        ScraperResponseResultDescriptiveDetail(
                                                            name,
                                                            description,
                                                            it.image.computeUriStringOrNull(),
                                                        )
                                                    } ?: it.image
                                                    .computeUriStringOrNull()
                                                    ?.let { image ->
                                                        ScraperResponseResultDescriptionlessDetail(name, image)
                                                    }
                                            }
                                    },
                                )
                            }
                        }?.let { send(it) }
                }
            }
        }
}
