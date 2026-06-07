package io.github.lengors.webscout.domain.persistence.events

import java.io.Serializable

interface EntityCreatedEvent<T : Serializable> : EntityEvent<T>
