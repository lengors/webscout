package io.github.lengors.webscout.domain.hazelcast.events.services

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.topic.ITopic
import io.github.lengors.webscout.domain.events.models.Event
import io.github.lengors.webscout.domain.events.models.EventListener
import io.github.lengors.webscout.domain.events.services.EventPublisher
import io.github.lengors.webscout.domain.hazelcast.events.models.HazelcastEventListener
import kotlinx.coroutines.future.asDeferred
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.support.BeanDefinitionValidationException
import org.springframework.core.ResolvableType
import org.springframework.util.ReflectionUtils

class HazelcastEventPublisher(
    hazelcastInstance: HazelcastInstance,
    eventListeners: Collection<EventListener<*>>,
) : EventPublisher {
    companion object {
        private val logger = LoggerFactory.getLogger(HazelcastEventPublisher::class.java)
    }

    private val hazelcastEventListeners: Collection<HazelcastEventListener> =
        eventListeners.map { eventListener ->
            ReflectionUtils
                .getAllDeclaredMethods(eventListener.javaClass)
                .firstOrNull { it.name == "onEvent" }
                ?.let { ResolvableType.forMethodParameter(it, 0) }
                ?.also {
                    if (it.hasGenerics()) throw BeanDefinitionValidationException("Event type with generics is not supported: ${it.type}")
                }?.let { it to hazelcastInstance.getTopic<Event>(it.type.typeName) }
                ?.let {
                    HazelcastEventListener(
                        it.second.addMessageListener { event ->
                            @Suppress("UNCHECKED_CAST")
                            eventListener as EventListener<Event>
                            eventListener.onEvent(event.messageObject)
                        },
                        it.first,
                        it.second,
                    )
                }
                ?: throw BeanDefinitionValidationException("Missing 'onEvent' method for: ${eventListener.javaClass}")
        }

    init {
        logger.info("Registered ${hazelcastEventListeners.size} event listeners")
    }

    override fun publishEvent(event: Event) = getTopics(event).forEach { it.publish(event) }

    override suspend fun publishEventAsync(event: Event) =
        getTopics(event)
            .map { it.publishAsync(event) }
            .map { it.asDeferred() }
            .forEach { it.await() }

    private fun getTopics(event: Event): Collection<ITopic<in Event>> =
        ResolvableType
            .forInstance(event)
            .let { type ->
                hazelcastEventListeners
                    .filter { it.eventType.isAssignableFrom(type) }
                    .map { it.eventTopic }
            }
}
