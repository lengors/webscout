package io.github.lengors.webscout.domain.converters

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import kotlin.reflect.KClass

open class ObjectMapperReadingConverter<T : Any>(
    private val objectMapper: ObjectMapper,
    private val type: KClass<T>,
) : Converter<ByteArray, T> {
    override fun convert(source: ByteArray): T? = objectMapper.readValue(source, type.java)
}
