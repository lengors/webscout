package io.github.lengors.webscout.domain.persistence.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

@Service
interface UniqueKeyPersistenceService<T : Serializable> : PersistenceService<T> {
    @Transactional
    suspend fun delete(key: String): T

    @Transactional(readOnly = true)
    suspend fun find(key: String): T

    @Transactional
    suspend fun update(data: T): T
}
