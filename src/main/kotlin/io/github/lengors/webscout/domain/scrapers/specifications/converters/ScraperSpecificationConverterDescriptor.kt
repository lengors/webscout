package io.github.lengors.webscout.domain.scrapers.specifications.converters

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.converters.ConverterDescriptor
import io.github.lengors.webscout.domain.converters.ConverterSupplier
import org.springframework.stereotype.Service

@Service
class ScraperSpecificationConverterDescriptor(
    private val objectMapper: ObjectMapper,
) : ConverterDescriptor<ScraperSpecification, ByteArray> {
    override val readingConverterSupplier: ConverterSupplier<ByteArray, ScraperSpecification>
        get() = { ScraperSpecificationReadingConverter(objectMapper) }

    override val writingConverterSupplier: ConverterSupplier<ScraperSpecification, ByteArray>
        get() = { ScraperSpecificationWritingConverter(objectMapper) }
}
