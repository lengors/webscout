package io.github.lengors.webscout.domain.scrapers.specifications.converters

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.converters.ObjectMapperReadingConverter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class ScraperSpecificationReadingConverter(
    objectMapper: ObjectMapper,
) : ObjectMapperReadingConverter<ScraperSpecification>(objectMapper, ScraperSpecification::class)
