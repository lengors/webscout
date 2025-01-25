package io.github.lengors.webscout.domain.events.services

import io.github.lengors.webscout.domain.events.models.Event
import org.springframework.stereotype.Service

@Service
interface EventPublisher {
    fun publishEvent(event: Event)

    suspend fun publishEventAsync(event: Event)
}
