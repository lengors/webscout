package io.github.lengors.webscout.domain.hazelcast.events.services

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.topic.ITopic
import io.github.lengors.webscout.domain.events.models.Event
import io.github.lengors.webscout.domain.events.models.EventListener
import io.github.lengors.webscout.domain.events.services.EventPublisher
import io.github.lengors.webscout.domain.hazelcast.events.models.HazelcastEventTopic
import kotlinx.coroutines.future.await
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.support.BeanDefinitionValidationException
import org.springframework.core.ResolvableType
import org.springframework.util.ReflectionUtils
import java.util.UUID

class HazelcastEventPublisher(
    hazelcastInstance: HazelcastInstance,
    eventListeners: Collection<EventListener<*>>,
) : EventPublisher {
    companion object {
        private val logger = LoggerFactory.getLogger(HazelcastEventPublisher::class.java)
    }

    private val hazelcastEventListeners: Map<HazelcastEventTopic, List<UUID>> =
        eventListeners
            .groupBy { eventListener ->
                ReflectionUtils
                    .getAllDeclaredMethods(eventListener.javaClass)
                    .filter { it.name == "onEvent" }
                    .map { ResolvableType.forMethodParameter(it, 0) }
                    .sortedWith { left, right ->
                        val leftAssignableFromRight = left.isAssignableFrom(right)
                        val rightAssignableFromLeft = right.isAssignableFrom(left)
                        when {
                            leftAssignableFromRight && rightAssignableFromLeft -> 0
                            leftAssignableFromRight -> 1
                            rightAssignableFromLeft -> -1
                            else -> 0
                        }
                    }.lastOrNull()
                    ?.also {
                        if (it.hasGenerics()) {
                            throw BeanDefinitionValidationException(
                                "Event type with generics is not supported: ${it.type}",
                            )
                        }
                    }?.let { HazelcastEventTopic(it.type, hazelcastInstance) }
                    ?: throw BeanDefinitionValidationException("Missing 'onEvent' method for: ${eventListener.javaClass}")
            }.mapValues {
                it.value.map { eventListener ->
                    it.key.topic.addMessageListener { event ->
                        @Suppress("UNCHECKED_CAST")
                        eventListener as EventListener<Event>
                        eventListener.onEvent(event.messageObject)
                    }
                }
            }

    init {
        logger.info("Registered ${hazelcastEventListeners.size} topics")
    }

    override fun publishEvent(event: Event) = getTopics(event).forEach { it.publish(event) }

    override suspend fun publishEventAsync(event: Event) =
        getTopics(event)
            .map { it.publishAsync(event) }
            .forEach { it.await() }

    private fun getTopics(event: Event): Collection<ITopic<Event>> =
        ResolvableType
            .forInstance(event)
            .let { type ->
                hazelcastEventListeners
                    .filterKeys { it.resolvableType.isAssignableFrom(type) }
                    .map { it.key.topic }
            }
}
