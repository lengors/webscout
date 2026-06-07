package io.github.lengors.webscout.domain.scrapers.specifications.exceptions.models

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class ScraperInvalidSpecificationNameException(
    name: String,
    cause: Throwable? = null,
) : ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid specification name '$name'", cause)
