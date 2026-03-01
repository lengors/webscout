package io.github.lengors.webscout.domain.scrapers.contexts.models

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTime
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDescriptionlessDetail
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDescriptiveDetail
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDetail
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultGrading
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultNoiseLevel
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultPrice
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultQuantity
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultStock
import io.github.lengors.webscout.domain.jexl.models.JexlContextDateTimeAdapter
import io.github.lengors.webscout.domain.jexl.models.JexlContextFunctionalAdapter
import io.github.lengors.webscout.domain.jexl.models.JexlDateTimeAdapter
import io.github.lengors.webscout.domain.jexl.models.JexlExecutionContext
import io.github.lengors.webscout.domain.jexl.models.JexlFunctionalAdapter
import io.github.lengors.webscout.domain.jexl.models.JexlHttpSessionAdapter
import io.github.lengors.webscout.domain.jexl.models.JexlSessionAdapter
import io.github.lengors.webscout.domain.network.http.services.HttpSession
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionReturnDetailAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionReturnExtractDetailAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionReturnExtractStockAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionReturnFlatDetailAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionReturnFlatStockAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionReturnStockAction
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionUrl
import io.github.lengors.webscout.integrations.duckling.client.DucklingClient
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression
import org.apache.commons.jexl3.ObjectContext
import org.springframework.util.CollectionUtils
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder
import java.time.ZoneId
import java.util.Locale
import kotlin.collections.flatMap

@ConsistentCopyVisibility
data class ScraperExecutionContext private constructor(
    private val jexlEngine: JexlEngine,
    private val toolkitProvider: (ScraperExecutionContext) -> ScraperToolkit,
    val context: ScraperContext,
    val httpSession: HttpSession,
    val searchTerm: String,
    val inputs: Map<String, String> = emptyMap(),
    val visitedHandlers: List<String> = emptyList(),
    val gates: Set<String> = emptySet(),
    val uri: UriComponents? = null,
    val value: Any? = null,
) : JexlExecutionContext {
    private val jexlContext = ObjectContext(jexlEngine, this)

    val datetime: JexlDateTimeAdapter
        get() = JexlContextDateTimeAdapter(timezone)

    val defaultUri: UriComponents by lazy {
        context.definition.defaultUrl.computeUri()
    }

    val functional: JexlFunctionalAdapter = JexlContextFunctionalAdapter(this)

    val locale: Locale by lazy {
        context.definition.locale
            .compute()
            .let {
                when (it) {
                    is String -> Locale.forLanguageTag(it)
                    is Locale -> it
                    else -> throw IllegalArgumentException("Unsupported locale: $it")
                }
            }
    }

    val session: JexlSessionAdapter? by lazy {
        uri?.toUri()?.let { JexlHttpSessionAdapter(httpSession, it) }
    }

    val timezone: ZoneId by lazy {
        context.definition.timezone
            .compute()
            .let {
                when (it) {
                    is String -> ZoneId.of(it)
                    is ZoneId -> it
                    else -> throw IllegalArgumentException("Unsupported timezone: $it")
                }
            }
    }

    val toolkit: ScraperToolkit by lazy {
        toolkitProvider(this)
    }

    constructor(
        jexlEngine: JexlEngine,
        ducklingClient: DucklingClient,
        objectMapper: ObjectMapper,
        context: ScraperContext,
        httpSession: HttpSession,
        searchTerm: String,
        inputs: Map<String, String> = emptyMap(),
    ) : this(
        jexlEngine,
        { ScraperContextToolkit(it, ducklingClient, objectMapper) },
        context,
        httpSession,
        searchTerm,
        inputs,
        emptyList(),
    )

    fun branch(
        visitedHandlerName: String? = null,
        openGates: List<JexlExpression> = emptyList(),
        closeGates: List<JexlExpression> = emptyList(),
        uri: UriComponents? = null,
        value: Any? = null,
    ): ScraperExecutionContext =
        copy(
            visitedHandlers =
                visitedHandlerName
                    ?.let { visitedHandlers + it }
                    ?: visitedHandlers,
            gates =
                gates
                    .plus(
                        openGates
                            .compute(String::class)
                            .filterNotNull()
                            .toSet(),
                    ).minus(
                        closeGates
                            .compute(String::class)
                            .filterNotNull()
                            .toSet(),
                    ),
            uri = uri ?: this.uri,
            value = value,
        )

    fun JexlExpression.computeBrand(): String = toolkit.brand(compute())!!

    suspend fun JexlExpression?.computeDateOrNull(): ScraperResponseResultDateTime? =
        toolkit
            .date(compute())
            ?.invoke()

    fun JexlExpression?.computeDecibelsOrNull(): Int? = toolkit.decibels(compute())

    fun JexlExpression?.computeDescription(): String = toolkit.description(compute())!!

    fun ScraperDefinitionReturnExtractDetailAction.computeDetail(): ScraperResponseResultDetail? =
        name
            .computeTextOrNull()
            ?.let { name ->
                description
                    .computeTextOrNull()
                    ?.let { description ->
                        ScraperResponseResultDescriptiveDetail(
                            name,
                            description,
                            image.computeUriStringOrNull(),
                        )
                    } ?: image
                    .computeUriStringOrNull()
                    ?.let { image ->
                        ScraperResponseResultDescriptionlessDetail(name, image)
                    }
            }

    fun List<ScraperDefinitionReturnDetailAction>.computeDetails(): List<ScraperResponseResultDetail> =
        flatMap { detailAction ->
            when (detailAction) {
                is ScraperDefinitionReturnExtractDetailAction ->
                    detailAction
                        .computeDetail()
                        ?.let(::listOf)
                        ?: emptyList()

                is ScraperDefinitionReturnFlatDetailAction ->
                    detailAction.flattens
                        .flatMap { it.compute(Iterable::class) ?: emptyList() }
                        .flatMap { value ->
                            with(branch(value = value)) {
                                detailAction.extracts.computeDetails()
                            }
                        }
            }
        }

    fun JexlExpression?.computeGradingOrNull(): ScraperResponseResultGrading? = toolkit.grading(compute())

    fun JexlExpression?.computeNoiseLevelOrNull(): ScraperResponseResultNoiseLevel? = toolkit.noiseLevel(compute())

    suspend fun JexlExpression.computePrice(): ScraperResponseResultPrice =
        toolkit
            .price(compute())
            ?.invoke()
            ?.let {
                ScraperResponseResultPrice(
                    it.number.doubleValueExact(),
                    it.currency.currencyCode,
                )
            }!!

    fun JexlExpression.computeQuantity(): ScraperResponseResultQuantity = toolkit.quantity(compute())!!

    suspend fun ScraperDefinitionReturnExtractStockAction.computeStock(): ScraperResponseResultStock =
        ScraperResponseResultStock(
            availability.computeQuantity(),
            storage.computeTextOrNull(),
            deliveringOn.computeDateOrNull(),
        )

    suspend fun List<ScraperDefinitionReturnStockAction>.computeStocks(): List<ScraperResponseResultStock> =
        flatMap { stockAction ->
            when (stockAction) {
                is ScraperDefinitionReturnExtractStockAction -> listOf(stockAction.computeStock())
                is ScraperDefinitionReturnFlatStockAction ->
                    stockAction.flattens
                        .flatMap { it.compute(Iterable::class) ?: emptyList() }
                        .flatMap { value ->
                            with(branch(value = value)) {
                                stockAction.extracts.computeStocks()
                            }
                        }
            }
        }

    fun JexlExpression?.computeTextOrNull(): String? =
        toolkit
            .text(compute())
            ?.takeIf(String::isNotBlank)

    fun ScraperDefinitionUrl.computeUri(defaultUri: UriComponents? = null): UriComponents =
        UriComponentsBuilder
            .newInstance()
            .let { defaultUri?.let(it::uriComponents) ?: it }
            .let { builder ->
                location
                    .computeUriOrNull()
                    ?.let { computedUri ->
                        builder
                            .let { if (computedUri.path != null) it.replacePath(null) else it }
                            .let { if (computedUri.query != null) it.replaceQuery(null) else it }
                            .uriComponents(computedUri)
                    }
                    ?: builder
            }.let {
                scheme
                    .compute(String::class)
                    ?.let(it::scheme)
                    ?: it
            }.let {
                host
                    .compute(String::class)
                    ?.let(it::host)
                    ?: it
            }.let { builder ->
                path
                    .compute(String::class)
                    ?.let { if (it.startsWith("/")) builder.replacePath(it) else builder.path(it) }
                    ?: builder
            }.let { builder ->
                parameters
                    ?.let { nonNullParameters ->
                        builder.replaceQueryParams(
                            CollectionUtils.toMultiValueMap(
                                nonNullParameters
                                    .map { (key, value) ->
                                        val computedKey =
                                            key
                                                .compute()
                                                ?.toString()
                                        val computedValue =
                                            value
                                                .compute()
                                                .mapNotNull { it?.toString() }
                                        computedKey to computedValue
                                    }.toMap(),
                            ),
                        )
                    }
                    ?: builder
            }.build()

    fun JexlExpression?.computeUriOrNull(): UriComponents? = toolkit.uri(compute())

    fun JexlExpression?.computeUriStringOrNull(): String? =
        computeUriOrNull()
            ?.let(toolkit::str)
            ?.takeIf(String::isNotBlank)

    override fun get(name: String?): Any? = jexlContext[name]

    override fun has(name: String?): Boolean = jexlContext.has(name)

    override fun set(
        name: String?,
        value: Any?,
    ) {
        jexlContext[name] = value
    }
}
