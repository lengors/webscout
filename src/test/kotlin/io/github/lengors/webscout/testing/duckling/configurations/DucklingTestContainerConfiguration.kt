package io.github.lengors.webscout.testing.duckling.configurations

import io.github.lengors.webscout.testing.duckling.containers.DucklingContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean

@TestConfiguration(proxyBeanMethods = false)
class DucklingTestContainerConfiguration {
    @Bean
    @ServiceConnection
    fun ducklingContainer(): DucklingContainer<*> = DucklingContainer("rasa/duckling:latest")
}
