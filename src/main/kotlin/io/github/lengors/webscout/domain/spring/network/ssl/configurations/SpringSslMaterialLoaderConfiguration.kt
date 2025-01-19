package io.github.lengors.webscout.domain.spring.network.ssl.configurations

import io.github.lengors.webscout.domain.network.ssl.services.SslMaterialLoader
import io.github.lengors.webscout.domain.spring.network.ssl.services.SpringSslMaterialLoader
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class SpringSslMaterialLoaderConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun sslMaterialLoader(): SslMaterialLoader = SpringSslMaterialLoader()
}
