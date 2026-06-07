package io.github.lengors.webscout.domain.persistence.events

import java.io.Serializable

interface EntityEvent<T : Serializable> : PersistenceEvent {
    val entity: T
}
