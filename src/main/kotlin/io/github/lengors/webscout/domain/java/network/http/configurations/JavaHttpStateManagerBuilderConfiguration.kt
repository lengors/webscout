package io.github.lengors.webscout.domain.java.network.http.configurations

import io.github.lengors.webscout.domain.java.network.http.services.JavaHttpStateManagerBuilder
import io.github.lengors.webscout.domain.network.http.services.HttpStateManagerBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class JavaHttpStateManagerBuilderConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun httpStateManagerBuilder(): HttpStateManagerBuilder = JavaHttpStateManagerBuilder()
}
