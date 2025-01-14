package io.github.lengors.webscout.domain.persistence.events

import java.io.Serializable

interface EntityDeletedEvent<T : Serializable> : EntityEvent<T>
