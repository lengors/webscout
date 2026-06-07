package io.github.lengors.webscout.domain.scrapers.exceptions.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequirement
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class ScraperInputMissingException(
    val requirement: ScraperSpecificationRequirement,
) : ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Input missing for requirement (name=${requirement.name}, type=${requirement.type})",
    )
