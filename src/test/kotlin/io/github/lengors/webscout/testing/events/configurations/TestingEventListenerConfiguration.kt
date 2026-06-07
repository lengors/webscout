package io.github.lengors.webscout.testing.events.configurations

import io.github.lengors.webscout.testing.events.services.TestingEventListener
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration(proxyBeanMethods = false)
class TestingEventListenerConfiguration {
    @Bean
    fun testingEventListener(): TestingEventListener = TestingEventListener()
}
