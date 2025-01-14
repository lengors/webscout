package io.github.lengors.webscout.domain.scrapers.specifications.events

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.persistence.events.EntityBatchDeletedEvent

data class ScraperSpecificationEntityBatchDeletedEvent(
    override val entities: Collection<ScraperSpecification>,
) : ScraperSpecificationEntityBatchEvent,
    EntityBatchDeletedEvent<ScraperSpecification>
