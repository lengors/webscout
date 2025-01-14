package io.github.lengors.webscout.domain.persistence.events

import java.io.Serializable

interface EntityBatchEvent<T : Serializable> : PersistenceEvent {
    val entities: Collection<T>
}
