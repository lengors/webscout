package io.github.lengors.webscout.domain.spring.r2dbc.configurations

import io.github.lengors.webscout.domain.converters.ConverterDescriptor
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration

@Configuration(proxyBeanMethods = false)
class R2dbcConfiguration(
    private val r2dbcProperties: R2dbcProperties,
    private val converterDescriptors: List<ConverterDescriptor<*, *>>,
) : AbstractR2dbcConfiguration() {
    override fun connectionFactory(): ConnectionFactory = ConnectionFactories.get(r2dbcProperties.url)

    override fun getCustomConverters(): List<Any> =
        converterDescriptors.flatMap {
            listOf(it.readingConverterSupplier(), it.writingConverterSupplier())
        }
}
