package io.github.lengors.webscout.domain.persistence.events

import java.io.Serializable

interface EntityUpdatedEvent<T : Serializable> : EntityEvent<T>
