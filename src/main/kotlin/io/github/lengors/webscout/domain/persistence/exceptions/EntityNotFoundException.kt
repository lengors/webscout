package io.github.lengors.webscout.domain.persistence.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.reflect.KClass

class EntityNotFoundException(
    type: KClass<*>,
    query: Any,
) : ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Entity {type=${type.simpleName}} not found for {query=$query}",
    )
