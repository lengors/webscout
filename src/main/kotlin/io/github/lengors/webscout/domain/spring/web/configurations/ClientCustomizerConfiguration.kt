package io.github.lengors.webscout.domain.spring.web.configurations

import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import reactor.netty.http.client.HttpClient

@Configuration(proxyBeanMethods = false)
class ClientCustomizerConfiguration {
    @Bean
    fun webClientCustomizer() =
        WebClientCustomizer {
            it.clientConnector(ReactorClientHttpConnector(HttpClient.newConnection()))
        }
}
