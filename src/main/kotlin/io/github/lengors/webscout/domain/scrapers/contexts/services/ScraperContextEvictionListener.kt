package io.github.lengors.webscout.domain.scrapers.contexts.services

import io.github.lengors.webscout.domain.events.models.EventListener
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityBatchEvent
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityCreatedEvent
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityDeletedEvent
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityUpdatedEvent
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationPersistenceEvent
import org.springframework.stereotype.Component

@Component
class ScraperContextEvictionListener(
    private val scraperContextManager: ScraperContextManager,
) : EventListener<ScraperSpecificationPersistenceEvent> {
    override fun onEvent(event: ScraperSpecificationPersistenceEvent) =
        when (event) {
            is ScraperSpecificationEntityCreatedEvent -> Unit
            is ScraperSpecificationEntityBatchEvent -> event.entities.forEach(scraperContextManager::discardScraperContext)
            is ScraperSpecificationEntityDeletedEvent, is ScraperSpecificationEntityUpdatedEvent ->
                scraperContextManager.discardScraperContext(
                    event.entity,
                )
        }
}
