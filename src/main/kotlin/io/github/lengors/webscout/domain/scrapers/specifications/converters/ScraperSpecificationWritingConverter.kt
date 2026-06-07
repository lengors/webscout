package io.github.lengors.webscout.domain.scrapers.specifications.converters

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.converters.ObjectMapperWritingConverter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class ScraperSpecificationWritingConverter(
    objectMapper: ObjectMapper,
) : ObjectMapperWritingConverter<ScraperSpecification>(objectMapper)
