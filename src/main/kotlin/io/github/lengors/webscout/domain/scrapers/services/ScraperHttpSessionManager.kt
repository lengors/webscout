package io.github.lengors.webscout.domain.scrapers.services

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.network.http.services.HttpSession

interface ScraperHttpSessionManager {
    fun discardHttpSession(specification: ScraperSpecification)

    fun getHttpSession(
        specification: ScraperSpecification,
        inputs: Map<String, String>,
    ): HttpSession
}
