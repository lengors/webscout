package io.github.lengors.webscout.domain.scrapers.contexts.models

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTime
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultGrading
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultNoiseLevel
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultPrice
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultQuantity
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultStock
import io.github.lengors.webscout.domain.jexl.models.JexlExecutionContext
import io.github.lengors.webscout.domain.jexl.models.JexlReference
import io.github.lengors.webscout.domain.jexl.models.value
import io.github.lengors.webscout.domain.jexl.utilities.JexlStringUtilities
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionReturnExtractStockAction
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
import java.math.BigDecimal
import java.time.ZoneId
import java.util.Locale
import kotlin.reflect.safeCast

@ConsistentCopyVisibility
data class ScraperExecutionContext private constructor(
    private val jexlEngine: JexlEngine,
    val ducklingClient: DucklingClient,
    val objectMapper: ObjectMapper,
    val context: ScraperContext,
    val searchTerm: String,
    val inputs: Map<String, String> = emptyMap(),
    val visitedHandlers: List<String> = emptyList(),
    val gates: Set<String> = emptySet(),
    val uri: UriComponents? = null,
    override val valueOrNull: Any? = null,
) : JexlExecutionContext,
    JexlStringUtilities,
    ScraperReference<Any> {
    private val jexlContext = ObjectContext(jexlEngine, this)

    constructor(
        jexlEngine: JexlEngine,
        ducklingClient: DucklingClient,
        objectMapper: ObjectMapper,
        context: ScraperContext,
        searchTerm: String,
        inputs: Map<String, String> = emptyMap(),
    ) : this(jexlEngine, ducklingClient, objectMapper, context, searchTerm, inputs, emptyList())

    fun branch(
        visitedHandlerName: String? = null,
        openGates: List<JexlExpression> = emptyList(),
        closeGates: List<JexlExpression> = emptyList(),
        uri: UriComponents? = null,
        valueOrNull: Any? = null,
    ): ScraperExecutionContext =
        copy(
            visitedHandlers =
                visitedHandlerName
                    ?.let { visitedHandlers + it }
                    ?: visitedHandlers,
            gates =
                gates +
                    openGates
                        .compute(String::class)
                        .mapNotNull { it.valueOrNull }
                        .toSet() -
                    closeGates
                        .compute(String::class)
                        .mapNotNull { it.valueOrNull }
                        .toSet(),
            uri = uri ?: this.uri,
            valueOrNull = valueOrNull,
        )

    fun JexlExpression.computeBrand(): String =
        this
            .compute()
            .let(ScraperReference::class::safeCast)
            ?.brand()
            .value

    fun JexlExpression?.computeDateOrNull(): ScraperResponseResultDateTime? =
        this
            .compute()
            .let(ScraperReference::class::safeCast)
            ?.date()
            ?.valueOrNull

    fun JexlExpression?.computeDecibelsOrNull(): Int? =
        this
            .compute()
            .let(ScraperReference::class::safeCast)
            ?.decibels()
            ?.valueOrNull

    fun JexlExpression?.computeDescription(): String =
        this
            .compute()
            .let(ScraperReference::class::safeCast)
            ?.description()
            .value

    fun JexlExpression?.computeGradingOrNull(): ScraperResponseResultGrading? =
        this
            .compute()
            .let(ScraperReference::class::safeCast)
            ?.grading()
            ?.valueOrNull

    fun JexlExpression?.computeNoiseLevelOrNull(): ScraperResponseResultNoiseLevel? =
        this
            .compute()
            .let(ScraperReference::class::safeCast)
            ?.noiseLevel()
            ?.valueOrNull

    fun JexlExpression.computePrice(): ScraperResponseResultPrice =
        this
            .compute()
            .let(ScraperReference::class::safeCast)
            ?.price()
            .value
            .let {
                ScraperResponseResultPrice(
                    it.number
                        .numberValueExact(BigDecimal::class.java)
                        .toPlainString(),
                    it.currency.currencyCode,
                )
            }

    fun JexlExpression.computeQuantity(): ScraperResponseResultQuantity =
        this
            .compute()
            .let(ScraperReference::class::safeCast)
            ?.quantity()
            .value

    fun ScraperDefinitionReturnExtractStockAction.computeStock(): List<ScraperResponseResultStock> =
        listOf(
            ScraperResponseResultStock(
                availability.computeQuantity(),
                storage.computeTextOrNull(),
                deliveryDateTime.computeDateOrNull(),
            ),
        )

    fun List<ScraperDefinitionReturnStockAction>.computeStocks(): List<ScraperResponseResultStock> =
        flatMap {
            when (it) {
                is ScraperDefinitionReturnExtractStockAction -> it.computeStock()
                is ScraperDefinitionReturnFlatStockAction ->
                    it.flattens
                        .compute(Iterable::class)
                        .valueOrNull
                        ?.flatMap { value ->
                            with(branch(valueOrNull = value)) {
                                it.extracts.computeStocks()
                            }
                        }
                        ?: emptyList()
            }
        }

    fun JexlExpression?.computeTextOrNull(): String? =
        this
            .compute()
            .text()
            .valueOrNull

    fun ScraperDefinitionUrl.computeUri(defaultUri: UriComponents? = null): UriComponents =
        UriComponentsBuilder
            .newInstance()
            .let { defaultUri?.let(it::uriComponents) ?: it }
            .let {
                location
                    .computeUriOrNull()
                    ?.let(it::uriComponents)
                    ?: it
            }.let {
                scheme
                    .compute(String::class)
                    .valueOrNull
                    ?.let(it::scheme)
                    ?: it
            }.let {
                host
                    .compute(String::class)
                    .valueOrNull
                    ?.let(it::host)
                    ?: it
            }.let {
                path
                    .compute(String::class)
                    .valueOrNull
                    ?.let(it::path)
                    ?: it
            }.let { builder ->
                parameters
                    ?.let { nonNullParameters ->
                        builder.replaceQueryParams(
                            CollectionUtils.toMultiValueMap(
                                nonNullParameters
                                    .map {
                                        it.key.compute(String::class).value to
                                            it.value
                                                .compute(String::class)
                                                .map(JexlReference<String>::valueOrNull)
                                    }.toMap(),
                            ),
                        )
                    }
                    ?: builder
            }.build()

    fun JexlExpression?.computeUri(): JexlReference<UriComponents>? =
        this
            .compute()
            .let(ScraperReference::class::safeCast)
            ?.uri()

    fun JexlExpression?.computeUriOrNull(): UriComponents? = computeUri()?.valueOrNull

    fun JexlExpression?.computeUriStringOrNull(): String? =
        this
            .computeUri()
            ?.string()
            ?.valueOrNull

    override val executionContext: ScraperExecutionContext
        get() = this

    override fun get(name: String?): Any? = jexlContext[name]

    override fun has(name: String?): Boolean = jexlContext.has(name)

    val locale: Locale by lazy {
        context.definition.locale
            .compute()
            .value
            .let {
                when (it) {
                    is String -> Locale.forLanguageTag(it)
                    is Locale -> it
                    else -> throw IllegalArgumentException("Unsupported locale: $it")
                }
            }
    }

    override fun <T : Any> makeReference(valueOrNull: T?): ScraperReference<T> = ScraperObject(this, valueOrNull)

    override fun set(
        name: String?,
        value: Any?,
    ) {
        jexlContext[name] = value
    }

    val timezone: ZoneId by lazy {
        context.definition.timezone
            .compute()
            .value
            .let {
                when (it) {
                    is String -> ZoneId.of(it)
                    is ZoneId -> it
                    else -> throw IllegalArgumentException("Unsupported timezone: $it")
                }
            }
    }
}
