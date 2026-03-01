package io.github.lengors.webscout.integrations.duckling.configurations

import io.github.lengors.webscout.integrations.duckling.properties.DucklingClientConnectionDetails
import io.github.lengors.webscout.integrations.duckling.properties.DucklingClientConnectionDetailsProperties
import io.github.lengors.webscout.integrations.duckling.properties.DucklingClientProperties
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

@AutoConfiguration
class DucklingClientConnectionDetailsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(
        value = [DucklingClientConnectionDetails::class],
        ignored = [DucklingClientConnectionDetailsProperties::class],
    )
    fun ducklingClientConnectionDetails(ducklingClientProperties: DucklingClientProperties): DucklingClientConnectionDetails =
        DucklingClientConnectionDetailsProperties(ducklingClientProperties)
}
