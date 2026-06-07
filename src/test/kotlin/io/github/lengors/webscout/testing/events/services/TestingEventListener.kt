package io.github.lengors.webscout.testing.events.services

import io.github.lengors.webscout.domain.events.models.EventListener
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationPersistenceEvent

class TestingEventListener : EventListener<ScraperSpecificationPersistenceEvent> {
    private val events: MutableList<ScraperSpecificationPersistenceEvent> = mutableListOf()

    val count: Int
        get() = events.size

    fun flushEvents(): List<ScraperSpecificationPersistenceEvent> {
        val output = events.toList()
        events.clear()
        return output
    }

    override fun onEvent(event: ScraperSpecificationPersistenceEvent) {
        events.add(event)
    }
}
