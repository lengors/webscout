package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification

data class ScraperTask(
    val specification: ScraperSpecification,
    val searchTerm: String,
    val inputs: Map<String, String>,
)
