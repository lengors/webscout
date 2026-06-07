package io.github.lengors.webscout.domain.java.network.http.configurations

import io.github.lengors.webscout.domain.java.network.http.services.JavaHttpSessionProvider
import io.github.lengors.webscout.domain.network.http.services.HttpSessionProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class JavaHttpSessionProviderConfiguration {
    @Bean
    @ConditionalOnMissingBean(value = [HttpSessionProvider::class], ignored = [JavaHttpSessionProvider::class])
    fun httpSessionProvider(): HttpSessionProvider = JavaHttpSessionProvider()
}
