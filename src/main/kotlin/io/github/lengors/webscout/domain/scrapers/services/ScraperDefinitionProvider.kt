package io.github.lengors.webscout.domain.scrapers.services

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.network.ssl.services.SslMaterialLoader
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinition
import org.apache.commons.jexl3.JexlEngine
import org.springframework.stereotype.Component

@Component
class ScraperDefinitionProvider(
    private val jexlEngine: JexlEngine,
    private val sslMaterialLoader: SslMaterialLoader,
) {
    fun provide(specification: ScraperSpecification): ScraperDefinition = ScraperDefinition(specification, jexlEngine, sslMaterialLoader)
}
