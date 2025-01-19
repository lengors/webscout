package io.github.lengors.webscout.domain.persistence.exceptions.models

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.reflect.KClass

class EntityConflictException(
    type: KClass<*>,
    query: Any,
) : ResponseStatusException(
        HttpStatus.CONFLICT,
        "Entity {type=${type.simpleName}} already exists for {query=$query}",
    )
