package io.github.lengors.webscout.domain.hazelcast.events.configurations

import com.hazelcast.core.HazelcastInstance
import io.github.lengors.webscout.domain.events.models.EventListener
import io.github.lengors.webscout.domain.events.services.EventPublisher
import io.github.lengors.webscout.domain.hazelcast.events.services.HazelcastEventPublisher
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class HazelcastEventPublisherConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun eventPublisher(
        hazelcastInstance: HazelcastInstance,
        eventListeners: Collection<EventListener<*>>,
    ): EventPublisher = HazelcastEventPublisher(hazelcastInstance, eventListeners)
}
