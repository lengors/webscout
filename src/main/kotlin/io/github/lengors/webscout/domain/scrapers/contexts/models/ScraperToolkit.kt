package io.github.lengors.webscout.domain.scrapers.contexts.models

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.MissingNode
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTime
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultGrading
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultNoiseLevel
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultQuantity
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultQuantityModifier
import io.github.lengors.webscout.domain.functional.async.AsyncSupplier
import io.github.lengors.webscout.domain.jexl.namespaces.JexlRegexNamespace
import io.github.lengors.webscout.domain.text.models.decibels
import io.github.lengors.webscout.domain.text.models.grading
import io.github.lengors.webscout.domain.text.models.noiseLevel
import io.github.lengors.webscout.domain.text.models.quantity
import io.github.lengors.webscout.domain.text.models.supplementary
import io.github.lengors.webscout.domain.text.models.whitespace
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.parser.Parser
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder
import java.util.Currency
import javax.money.MonetaryAmount
import kotlin.collections.firstOrNull

interface ScraperToolkit {
    fun attr(
        node: Node,
        attribute: String,
    ): String = node.attr(attribute)

    fun brand(value: Any?): String? = str(value)?.replace(Regex.supplementary, StringUtils.EMPTY)

    fun date(value: Any?): AsyncSupplier<ScraperResponseResultDateTime?>? = str(value, this::date)

    fun date(value: String): AsyncSupplier<ScraperResponseResultDateTime?>

    fun decibels(value: Any?): Int? =
        str(value) {
            Regex.decibels
                .find(it)
                ?.groups
                ?.get(1)
                ?.value
                ?.toIntOrNull()
        }

    fun description(value: Any?): String? = text(value)?.uppercase()

    fun grading(value: Any?): ScraperResponseResultGrading? =
        str(value) { str ->
            Regex.grading
                .find(str)
                ?.value
                ?.let { grading ->
                    grading
                        .toIntOrNull()
                        ?.let { ScraperResponseResultGrading.entries[it - 1] }
                        ?: runCatching { ScraperResponseResultGrading.valueOf(grading.uppercase()) }
                            .getOrNull()
                }
        }

    fun html(value: Any?): Node? =
        str(value) {
            val xmlDocument = Jsoup.parse(it, Parser.xmlParser())
            val tableTags = setOf("tr", "td", "th", "thead", "tbody", "tfoot")
            val isTableFragment =
                xmlDocument
                    .children()
                    .map(Element::tagName)
                    .map(String::lowercase)
                    .all(tableTags::contains)

            val content = if (isTableFragment) "<table>$it</table>" else it
            Jsoup.parse(content)
        }

    fun json(value: Any?): JsonNode? = str(value, this::json)

    fun json(value: String): JsonNode?

    fun noiseLevel(value: Any?): ScraperResponseResultNoiseLevel? =
        str(value) { str ->
            Regex.noiseLevel
                .find(str)
                ?.groups
                ?.let { it["gradingletter"]?.value ?: it["gradingnumber"]?.value }
                ?.let { noiseLevel ->
                    noiseLevel
                        .toIntOrNull()
                        ?.let { ScraperResponseResultNoiseLevel.entries[it - 1] }
                        ?: runCatching { ScraperResponseResultNoiseLevel.valueOf(noiseLevel.uppercase()) }
                            .getOrNull()
                }
        }

    fun price(value: Any?): AsyncSupplier<MonetaryAmount?>? = str(value, this::price)

    fun price(value: String): AsyncSupplier<MonetaryAmount?>

    fun quantity(value: Any?): ScraperResponseResultQuantity? =
        str(value) { str ->
            Regex.quantity
                .find(str)
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
        }

    fun regexMatch(
        value: Any?,
        regex: String,
    ): String? = regexMatch(value, regex, null)

    fun regexMatch(
        value: Any?,
        regex: String,
        group: Int?,
    ): String? = JexlRegexNamespace.match(str(value), regex, group)

    fun select(
        node: JsonNode?,
        selector: String,
    ): JsonNode? =
        selectAll(node, selector)
            .let { it.firstOrNull() ?: it }
            .takeUnless { it.isMissingNode }

    fun select(
        node: Element?,
        selector: String,
    ): Node? = selectAll(node, selector).firstOrNull()

    fun select(
        node: Any?,
        selector: String,
    ): Any? =
        selectAll(node, selector).let { selected ->
            when (selected) {
                is JsonNode -> (selected.firstOrNull() ?: selected).takeUnless { it.isMissingNode }
                else -> selected.firstOrNull()
            }
        }

    fun selectAll(
        node: JsonNode?,
        selector: String,
    ): JsonNode =
        node
            ?.at(selector)
            ?: MissingNode.getInstance()

    fun selectAll(
        node: Element?,
        selector: String,
    ): List<Node> =
        node
            ?.selectXpath(selector, Node::class.java)
            ?: emptyList()

    fun selectAll(
        node: Any?,
        selector: String,
    ): Iterable<*> =
        when (node) {
            is JsonNode -> selectAll(node, selector)
            is Element -> selectAll(node, selector)
            else -> emptyList<Any>()
        }

    fun str(value: Any?): String? =
        when (value) {
            is String -> value
            is Element -> value.text()
            is Node -> value.attr(value.nodeName())
            is JsonNode -> value.asText()
            is MonetaryAmount -> "${value.number}${Currency.getInstance(value.currency.currencyCode).symbol}"
            else -> value?.toString()
        }

    fun text(value: Any?): String? =
        str(value)
            ?.trim()
            ?.replace(Regex.whitespace, StringUtils.SPACE)

    fun uri(value: Any?): UriComponents? =
        str(value) { str ->
            UriComponentsBuilder
                .fromUriString(str)
                .build()
        }?.let(this::uri)

    fun uri(value: UriComponents): UriComponents
}

inline fun <reified T : Any> ScraperToolkit.str(
    value: Any?,
    transform: (String) -> T?,
): T? =
    when (value) {
        is T -> value
        else -> str(value)?.let(transform)
    }
