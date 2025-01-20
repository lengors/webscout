package io.github.lengors.webscout.domain.spring.network.http.configurations

import io.github.lengors.webscout.domain.network.http.services.HttpStateManagerBuilder
import io.github.lengors.webscout.domain.network.http.services.HttpStatefulClientBuilder
import io.github.lengors.webscout.domain.spring.network.http.services.SpringHttpStatefulClientBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration(proxyBeanMethods = false)
class SpringHttpStatefulClientBuilderConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun httpStatefulClientBuilder(
        webClientBuilder: WebClient.Builder,
        httpStateManagerBuilder: HttpStateManagerBuilder,
    ): HttpStatefulClientBuilder = SpringHttpStatefulClientBuilder(webClientBuilder, httpStateManagerBuilder)
}
