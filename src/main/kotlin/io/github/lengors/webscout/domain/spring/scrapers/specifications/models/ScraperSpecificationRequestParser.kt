package io.github.lengors.webscout.domain.spring.scrapers.specifications.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequestParser
import io.github.lengors.webscout.domain.jexl.models.JexlReference
import io.github.lengors.webscout.domain.scrapers.contexts.models.ScraperExecutionContext
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

fun ScraperSpecificationRequestParser.asHeaders(): Map<String, String> =
    mediaType
        ?.let { mapOf(HttpHeaders.ACCEPT to "$it") }
        ?: emptyMap()

val ScraperSpecificationRequestParser.mediaType: MediaType?
    get() =
        when (this) {
            ScraperSpecificationRequestParser.AUTO -> null
            ScraperSpecificationRequestParser.HTML -> MediaType.TEXT_HTML
            ScraperSpecificationRequestParser.JSON -> MediaType.APPLICATION_JSON
            ScraperSpecificationRequestParser.TEXT -> MediaType.TEXT_PLAIN
        }

fun ScraperSpecificationRequestParser.parse(
    executionContext: ScraperExecutionContext,
    headers: Map<String, List<String>>? = null,
): JexlReference<Any> =
    when (this) {
        ScraperSpecificationRequestParser.AUTO ->
            when (
                headers
                    ?.get(HttpHeaders.CONTENT_TYPE.lowercase())
                    ?.firstOrNull()
            ) {
                MediaType.TEXT_HTML_VALUE -> ScraperSpecificationRequestParser.HTML
                MediaType.APPLICATION_JSON_VALUE -> ScraperSpecificationRequestParser.JSON
                else -> ScraperSpecificationRequestParser.TEXT
            }.parse(executionContext)

        ScraperSpecificationRequestParser.HTML -> executionContext.html()
        ScraperSpecificationRequestParser.JSON -> executionContext.json()
        ScraperSpecificationRequestParser.TEXT -> executionContext.text()
    }
