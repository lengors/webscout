package io.github.lengors.webscout.domain.scrapers.contexts.services

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.network.http.services.HttpExchangerProvider
import io.github.lengors.webscout.domain.scrapers.contexts.models.ScraperContext
import io.github.lengors.webscout.domain.scrapers.services.ScraperDefinitionProvider
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
@ConditionalOnMissingBean(value = [ScraperContextManager::class], ignored = [CachingScraperContextManager::class])
class CachingScraperContextManager(
    private val httpExchangerProvider: HttpExchangerProvider,
    private val scraperDefinitionProvider: ScraperDefinitionProvider,
) : ScraperContextManager {
    companion object {
        private val logger = LoggerFactory.getLogger(CachingScraperContextManager::class.java)
    }

    @CacheEvict("ScraperContexts", key = "#specification.name")
    override fun discardScraperContext(specification: ScraperSpecification) {
        logger.info("Discarding scraper context for ScraperSpecification(name={})", specification.name)
    }

    @Cacheable("ScraperContexts", key = "#specification.name")
    override fun getScraperContext(specification: ScraperSpecification): ScraperContext =
        ScraperContext(scraperDefinitionProvider.provide(specification), httpExchangerProvider)
}
