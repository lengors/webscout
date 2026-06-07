package io.github.lengors.webscout.domain.converters

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter

open class ObjectMapperWritingConverter<T : Any>(
    private val objectMapper: ObjectMapper,
) : Converter<T, ByteArray> {
    override fun convert(source: T): ByteArray? = objectMapper.writeValueAsBytes(source)
}
