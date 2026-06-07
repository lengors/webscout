package io.github.lengors.webscout.domain.spring.network.http.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.http-exchanger")
data class SpringHttpExchangerProperties(
    val maxRedirectsAllowed: Int = 100,
)
