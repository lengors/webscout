package io.github.lengors.webscout.integrations.duckling.configurations

import io.github.lengors.webscout.integrations.duckling.properties.DucklingClientConnectionDetails
import io.github.lengors.webscout.integrations.duckling.properties.DucklingClientConnectionDetailsProperties
import io.github.lengors.webscout.integrations.duckling.properties.DucklingClientProperties
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

@AutoConfiguration
class DucklingClientConnectionDetailsConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun ducklingClientConnectionDetails(ducklingClientProperties: DucklingClientProperties): DucklingClientConnectionDetails =
        DucklingClientConnectionDetailsProperties(ducklingClientProperties)
}
