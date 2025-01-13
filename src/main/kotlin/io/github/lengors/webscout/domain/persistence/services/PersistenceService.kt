package io.github.lengors.webscout.domain.persistence.services

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

@Service
interface PersistenceService<T : Serializable> {
    @Transactional
    fun deleteAll(): Flow<T>

    @Transactional(readOnly = true)
    fun findAll(): Flow<T>

    @Transactional(readOnly = true)
    fun findAll(keys: Collection<String>): Flow<T>

    @Transactional
    suspend fun save(data: T): T
}
