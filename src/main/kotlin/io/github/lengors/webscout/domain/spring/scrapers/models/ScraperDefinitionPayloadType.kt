package io.github.lengors.webscout.domain.spring.scrapers.models

import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionPayloadType
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

fun ScraperDefinitionPayloadType?.asHeaders(): Map<String, String> =
    this
        ?.let { mapOf(HttpHeaders.CONTENT_TYPE to "$mediaType") }
        ?: emptyMap()

val ScraperDefinitionPayloadType.mediaType: MediaType
    get() =
        when (this) {
            ScraperDefinitionPayloadType.DATA -> MediaType.APPLICATION_FORM_URLENCODED
            ScraperDefinitionPayloadType.JSON -> MediaType.APPLICATION_JSON
        }
