package io.github.lengors.webscout.domain.scrapers.specifications.exceptions.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class ScraperInvalidSpecificationException(
    specification: ScraperSpecification,
    cause: Throwable,
) : ResponseStatusException(
        HttpStatus.UNPROCESSABLE_ENTITY,
        "Unable to process specification with (name=${specification.name})",
        cause,
    )
