package io.github.lengors.webscout.domain.scrapers.contexts.services

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.scrapers.contexts.models.ScraperContext

interface ScraperContextManager {
    fun discardScraperContext(specification: ScraperSpecification)

    fun getScraperContext(specification: ScraperSpecification): ScraperContext
}
