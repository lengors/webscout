package io.github.lengors.webscout.domain.scrapers.contexts.models

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTime
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTimeInstant
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTimeInstantGrain
import io.github.lengors.webscout.domain.functional.async.AsyncSupplier
import io.github.lengors.webscout.domain.scrapers.models.fromDucklingResponseValue
import io.github.lengors.webscout.integrations.duckling.client.DucklingClient
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
import org.jsoup.nodes.Element
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder
import java.time.ZonedDateTime
import java.util.Currency
import java.util.Date
import javax.money.MonetaryAmount

data class ScraperContextToolkit(
    private val executionContext: ScraperExecutionContext,
    private val ducklingClient: DucklingClient,
    private val objectMapper: ObjectMapper,
) : ScraperToolkit {
    companion object {
        val AMOUNT_OF_MONEY_LIKE_DIMENSIONS: List<DucklingAmountOfMoneyLikeDimension> =
            linkedSetOf(DucklingAmountOfMoneyDimension, DucklingNumberDimension, DucklingAnyAmountOfMoneyLikeDimension)
                .toList()

        val DATE_TIME_LIKE_DIMENSIONS: List<DucklingDateTimeLikeDimension> =
            linkedSetOf(DucklingDateTimeDimension, DucklingDurationDimension, DucklingAnyDateTimeLikeDimension)
                .toList()
    }

    override fun date(value: String): AsyncSupplier<ScraperResponseResultDateTime?> =
        AsyncSupplier {
            ducklingClient
                .parse(DucklingDateTimeRequest(value, executionContext.locale, executionContext.timezone))
                .minByOrNull { DATE_TIME_LIKE_DIMENSIONS.indexOf(it.dimension) }
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

    override fun json(value: String): JsonNode? = objectMapper.readTree(value)

    override fun price(value: String): AsyncSupplier<MonetaryAmount?> =
        AsyncSupplier {
            ducklingClient
                .parse(DucklingAmountOfMoneyRequest(value, executionContext.locale, executionContext.timezone))
                .minByOrNull { AMOUNT_OF_MONEY_LIKE_DIMENSIONS.indexOf(it.dimension) }
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

    fun select(selector: String): Any? =
        when (executionContext.value) {
            is JsonNode -> select(executionContext.value, selector)
            is Element -> select(executionContext.value, selector)
            else -> null
        }

    fun selectAll(selector: String): Iterable<*> =
        when (executionContext.value) {
            is JsonNode -> selectAll(executionContext.value, selector)
            is Element -> selectAll(executionContext.value, selector)
            else -> emptyList<Any>()
        }

    override fun uri(value: UriComponents): UriComponents =
        executionContext.uri
            ?.let {
                UriComponentsBuilder
                    .newInstance()
                    .uriComponents(it)
                    .replacePath(null)
                    .replaceQuery(null)
                    .let { contextUriComponents ->
                        if (value.path?.startsWith("/") == true) {
                            contextUriComponents
                        } else {
                            it.path
                                ?.let { path ->
                                    contextUriComponents.path(StringUtils.appendIfMissing(path, "/"))
                                }
                                ?: contextUriComponents
                        }
                    }
            }?.uriComponents(value)
            ?.build()
            ?: value
}
