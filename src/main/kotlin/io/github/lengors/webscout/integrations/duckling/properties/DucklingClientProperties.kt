package io.github.lengors.webscout.integrations.duckling.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "duckling.client")
data class DucklingClientProperties(
    val url: String = "http://localhost:8000",
)
