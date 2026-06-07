package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequirement
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequirementType
import io.github.lengors.webscout.domain.scrapers.exceptions.models.ScraperInputMissingException
import io.github.lengors.webscout.domain.scrapers.exceptions.models.ScraperInvalidInputException
import io.github.lengors.webscout.domain.validators.EmailValidator

@JvmInline
value class ScraperDefinitionRequirement(
    private val specification: ScraperSpecificationRequirement,
) {
    val default: String?
        get() = specification.default

    val name: String
        get() = specification.name

    fun validate(input: String?): String {
        input ?: throw ScraperInputMissingException(specification)
        val validation =
            when (specification.type) {
                ScraperSpecificationRequirementType.EMAIL -> EmailValidator.isValid(input)
                ScraperSpecificationRequirementType.PASSWORD, ScraperSpecificationRequirementType.TEXT -> null
            }
        return input
            .takeIf { validation != false }
            ?: throw ScraperInvalidInputException(input, specification)
    }
}
