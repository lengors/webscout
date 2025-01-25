package io.github.lengors.webscout.domain.spring.scrapers.specifications.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequestParser
import io.github.lengors.webscout.domain.jexl.models.JexlReference
import io.github.lengors.webscout.domain.scrapers.contexts.models.ScraperExecutionContext
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

fun ScraperSpecificationRequestParser.asHeaders(): Map<String, String> = mapOf(HttpHeaders.ACCEPT to "$mediaType")

val ScraperSpecificationRequestParser.mediaType: MediaType
    get() =
        when (this) {
            ScraperSpecificationRequestParser.HTML -> MediaType.TEXT_HTML
            ScraperSpecificationRequestParser.JSON -> MediaType.APPLICATION_JSON
            ScraperSpecificationRequestParser.TEXT -> MediaType.TEXT_PLAIN
        }

fun ScraperSpecificationRequestParser.parse(executionContext: ScraperExecutionContext): JexlReference<Any> =
    when (this) {
        ScraperSpecificationRequestParser.HTML -> executionContext.html()
        ScraperSpecificationRequestParser.JSON -> executionContext.json()
        ScraperSpecificationRequestParser.TEXT -> executionContext.text()
    }
