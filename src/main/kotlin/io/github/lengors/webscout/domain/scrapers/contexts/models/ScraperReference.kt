package io.github.lengors.webscout.domain.scrapers.contexts.models

import com.fasterxml.jackson.databind.JsonNode
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTime
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTimeInstant
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTimeInstantGrain
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultGrading
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultNoiseLevel
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultQuantity
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultQuantityModifier
import io.github.lengors.webscout.domain.functional.async.AsyncSupplier
import io.github.lengors.webscout.domain.jexl.models.JexlReference
import io.github.lengors.webscout.domain.jexl.models.mapString
import io.github.lengors.webscout.domain.jexl.models.value
import io.github.lengors.webscout.domain.scrapers.models.fromDucklingResponseValue
import io.github.lengors.webscout.domain.utilities.decibels
import io.github.lengors.webscout.domain.utilities.grading
import io.github.lengors.webscout.domain.utilities.noiseLevel
import io.github.lengors.webscout.domain.utilities.quantity
import io.github.lengors.webscout.domain.utilities.supplementary
import io.github.lengors.webscout.integrations.duckling.models.DucklingAmountOfMoneyDimension
import io.github.lengors.webscout.integrations.duckling.models.DucklingAmountOfMoneyLikeDimension
import io.github.lengors.webscout.integrations.duckling.models.DucklingAmountOfMoneyRequest
import io.github.lengors.webscout.integrations.duckling.models.DucklingAmountOfMoneyResponseValue
import io.github.lengors.webscout.integrations.duckling.models.DucklingAnyAmountOfMoneyLikeDimension
import io.github.lengors.webscout.integrations.duckling.models.DucklingAnyAmountOfMoneyLikeResponseValue
import io.github.lengors.webscout.integrations.duckling.models.DucklingAnyDateTimeLikeDimension
import io.github.lengors.webscout.integrations.duckling.models.DucklingAnyDateTimeLikeResponseValue
import io.github.lengors.webscout.integrations.duckling.models.DucklingDateTimeDimension
import io.github.lengors.webscout.integrations.duckling.models.DucklingDateTimeLikeDimension
import io.github.lengors.webscout.integrations.duckling.models.DucklingDateTimeRequest
import io.github.lengors.webscout.integrations.duckling.models.DucklingDateTimeResponseValue
import io.github.lengors.webscout.integrations.duckling.models.DucklingDurationDimension
import io.github.lengors.webscout.integrations.duckling.models.DucklingDurationResponseValue
import io.github.lengors.webscout.integrations.duckling.models.DucklingNumberDimension
import io.github.lengors.webscout.integrations.duckling.models.DucklingNumberResponseValue
import org.apache.commons.lang3.StringUtils
import org.javamoney.moneta.Money
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.LeafNode
import org.jsoup.nodes.Node
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder
import java.time.ZonedDateTime
import java.util.Currency
import java.util.Date
import javax.money.MonetaryAmount

interface ScraperReference<out T : Any> : JexlReference<T> {
    companion object {
        val AMOUNT_OF_MONEY_LIKE_DIMENSIONS: List<DucklingAmountOfMoneyLikeDimension> =
            linkedSetOf(DucklingAmountOfMoneyDimension, DucklingNumberDimension, DucklingAnyAmountOfMoneyLikeDimension)
                .toList()

        val DATE_TIME_LIKE_DIMENSIONS: List<DucklingDateTimeLikeDimension> =
            linkedSetOf(DucklingDateTimeDimension, DucklingDurationDimension, DucklingAnyDateTimeLikeDimension)
                .toList()
    }

    fun attr(attribute: String?): JexlReference<String> =
        flatMap { value ->
            attribute
                ?.let { attr ->
                    when (value) {
                        is Node -> value.attr(attr)
                        else -> string().valueOrNull?.takeIf { attr == it }
                    }
                }
                ?: when (value) {
                    is String -> value
                    is LeafNode -> value.attr(value.nodeName())
                    is Element -> value.text()
                    is JsonNode -> value.asText()
                    is MonetaryAmount -> "${value.number}${Currency.getInstance(value.currency.currencyCode).symbol}"
                    else -> value.toString()
                }
        }

    fun brand(): JexlReference<String> = mapString { it.replace(Regex.supplementary, StringUtils.EMPTY) }

    fun date(): JexlReference<AsyncSupplier<ScraperResponseResultDateTime?>> =
        flatMap {
            when (it) {
                is AsyncSupplier<*> -> it
                else -> AsyncSupplier { it }
            }.map { input ->
                when (input) {
                    is ScraperResponseResultDateTime -> input
                    null -> null
                    else ->
                        executionContext.ducklingClient
                            .parse(
                                DucklingDateTimeRequest(
                                    executionContext.makeReference(input).string().value,
                                    executionContext.locale,
                                    executionContext.timezone,
                                ),
                            ).minByOrNull { response -> DATE_TIME_LIKE_DIMENSIONS.indexOf(response.dimension) }
                            ?.value
                            ?.let { response ->
                                when (response) {
                                    is DucklingDateTimeResponseValue -> fromDucklingResponseValue(response)
                                    is DucklingDurationResponseValue ->
                                        ScraperResponseResultDateTimeInstant(
                                            Date.from(
                                                ZonedDateTime
                                                    .now()
                                                    .plusSeconds(response.normalized.value.toLong())
                                                    .toInstant(),
                                            ),
                                            ScraperResponseResultDateTimeInstantGrain.valueOf(response.unit.name.uppercase()),
                                        )

                                    is DucklingAnyDateTimeLikeResponseValue -> null
                                }
                            }
                }
            }
        }

    fun decibels(): JexlReference<Int> =
        mapString {
            Regex.decibels
                .find(it)
                ?.groups
                ?.get(1)
                ?.value
                ?.toIntOrNull()
        }

    fun description(): JexlReference<String> = text().map(String::uppercase)

    override val executionContext: ScraperExecutionContext

    fun grading(): JexlReference<ScraperResponseResultGrading> =
        mapString { stringValue ->
            Regex.grading
                .find(stringValue)
                ?.value
                ?.let { grading ->
                    grading
                        .toIntOrNull()
                        ?.let { ScraperResponseResultGrading.entries[it - 1] }
                        ?: runCatching { ScraperResponseResultGrading.valueOf(grading.uppercase()) }
                            .getOrNull()
                }
        }

    fun html(): JexlReference<Node> =
        flatMap {
            when (it) {
                is Node -> it
                else -> mapString(Jsoup::parse).valueOrNull
            }
        }

    fun json(): JexlReference<JsonNode> =
        flatMap {
            when (it) {
                is JsonNode -> it
                else -> mapString(executionContext.objectMapper::readTree).valueOrNull
            }
        }

    fun noiseLevel(): JexlReference<ScraperResponseResultNoiseLevel> =
        mapString { stringValue ->
            Regex.noiseLevel
                .find(stringValue)
                ?.value
                ?.let { noiseLevel ->
                    noiseLevel
                        .toIntOrNull()
                        ?.let { ScraperResponseResultNoiseLevel.entries[it - 1] }
                        ?: runCatching { ScraperResponseResultNoiseLevel.valueOf(noiseLevel.uppercase()) }
                            .getOrNull()
                }
        }

    fun price(): JexlReference<AsyncSupplier<MonetaryAmount?>> =
        flatMap {
            when (it) {
                is AsyncSupplier<*> -> it
                else -> AsyncSupplier { it }
            }.map { input ->
                when (input) {
                    is MonetaryAmount -> input
                    null -> null
                    else ->
                        executionContext.ducklingClient
                            .parse(
                                DucklingAmountOfMoneyRequest(
                                    executionContext.makeReference(input).string().value,
                                    executionContext.locale,
                                    executionContext.timezone,
                                ),
                            ).minByOrNull { response -> AMOUNT_OF_MONEY_LIKE_DIMENSIONS.indexOf(response.dimension) }
                            ?.value
                            ?.let { response ->
                                when (response) {
                                    is DucklingAmountOfMoneyResponseValue -> Money.of(response.value, response.unit)

                                    is DucklingNumberResponseValue ->
                                        Money.of(
                                            response.value,
                                            Currency.getInstance(executionContext.locale).currencyCode,
                                        )

                                    is DucklingAnyAmountOfMoneyLikeResponseValue -> null
                                }
                            }
                }
            }
        }

    fun quantity(): JexlReference<ScraperResponseResultQuantity> =
        flatMap {
            when (it) {
                is ScraperResponseResultQuantity -> it
                else ->
                    mapString { stringValue ->
                        Regex.quantity
                            .find(stringValue)
                            ?.let { quantityMatch ->
                                ScraperResponseResultQuantity(
                                    quantityMatch.groups[2]?.value?.toInt() ?: 0,
                                    when (quantityMatch.groups[1]?.value) {
                                        "+", ">" -> ScraperResponseResultQuantityModifier.AT_LEAST
                                        "-", "<" -> ScraperResponseResultQuantityModifier.AT_MOST
                                        else -> ScraperResponseResultQuantityModifier.EXACT
                                    },
                                )
                            }
                    }.valueOrNull
            }
        }

    fun select(selector: String): JexlReference<Any> =
        executionContext.makeReference(
            selectAll(selector).valueOrNull?.let {
                it.firstOrNull() ?: it
            },
        )

    fun selectAll(selector: String): JexlReference<Iterable<*>> =
        flatMap {
            when (it) {
                is JsonNode -> it.at(selector)
                is Element -> it.selectXpath(selector, Node::class.java)
                is Iterable<*> ->
                    it.flatMap { value ->
                        executionContext
                            .makeReference(value)
                            .selectAll(selector)
                            .valueOrNull
                            ?: emptyList()
                    }

                else -> attr(selector).valueOrNull?.let(::listOf)
            }
        }

    override fun string(): JexlReference<String> = attr(null)

    fun uri(): JexlReference<UriComponents> =
        mapString { stringValue ->
            UriComponentsBuilder
                .fromUriString(stringValue)
                .build()
                .let { selectedUri ->
                    executionContext.uri
                        ?.let {
                            UriComponentsBuilder
                                .newInstance()
                                .uriComponents(it)
                                .replacePath(null)
                                .replaceQuery(null)
                                .let { contextUriComponents ->
                                    if (selectedUri.path?.startsWith("/") == true) {
                                        contextUriComponents
                                    } else {
                                        it.path
                                            ?.let { path ->
                                                contextUriComponents.path(StringUtils.appendIfMissing(path, "/"))
                                            }
                                            ?: contextUriComponents
                                    }
                                }
                        }?.uriComponents(selectedUri)
                        ?.build()
                        ?: selectedUri
                }
        }
}
