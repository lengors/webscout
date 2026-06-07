package io.github.lengors.webscout.domain.scrapers.exceptions.models

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class ScraperHandlerNotFoundException(
    specificationName: String,
) : ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Could not find handler for specification (name=$specificationName)",
    )
