package io.github.lengors.webscout.domain.persistence.events

import java.io.Serializable

interface EntityBatchDeletedEvent<T : Serializable> : EntityBatchEvent<T>
