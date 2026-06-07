package io.github.lengors.webscout.domain.scrapers.exceptions.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequirement
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class ScraperInvalidInputException(
    input: String,
    val requirement: ScraperSpecificationRequirement,
) : ResponseStatusException(
        HttpStatus.UNPROCESSABLE_ENTITY,
        "The provided input ($input) does not meet the requirement (name=${requirement.name}, type=${requirement.type})",
    )
