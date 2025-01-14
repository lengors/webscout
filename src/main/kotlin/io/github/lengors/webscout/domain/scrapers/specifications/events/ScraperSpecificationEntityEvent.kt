package io.github.lengors.webscout.domain.scrapers.specifications.events

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.persistence.events.EntityEvent

sealed interface ScraperSpecificationEntityEvent :
    ScraperSpecificationPersistenceEvent,
    EntityEvent<ScraperSpecification>
