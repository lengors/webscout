package io.github.lengors.webscout.domain.scrapers.specifications.repositories

import io.github.lengors.webscout.domain.scrapers.specifications.models.ScraperSpecificationEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ScraperSpecificationRepository : CoroutineCrudRepository<ScraperSpecificationEntity, Long> {
    suspend fun findByName(name: String): ScraperSpecificationEntity?

    fun findAllByNameIn(names: Collection<String>): Flow<ScraperSpecificationEntity>
}
