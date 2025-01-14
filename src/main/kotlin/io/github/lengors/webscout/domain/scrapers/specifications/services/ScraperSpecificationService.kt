package io.github.lengors.webscout.domain.scrapers.specifications.services

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.events.services.EventPublisher
import io.github.lengors.webscout.domain.persistence.exceptions.EntityConflictException
import io.github.lengors.webscout.domain.persistence.exceptions.EntityNotFoundException
import io.github.lengors.webscout.domain.persistence.services.UniqueKeyPersistenceService
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityBatchDeletedEvent
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityCreatedEvent
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityDeletedEvent
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityUpdatedEvent
import io.github.lengors.webscout.domain.scrapers.specifications.models.ScraperSpecificationEntity
import io.github.lengors.webscout.domain.scrapers.specifications.repositories.ScraperSpecificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ScraperSpecificationService(
    private val eventPublisher: EventPublisher,
    private val scraperSpecificationRepository: ScraperSpecificationRepository,
) : UniqueKeyPersistenceService<ScraperSpecification> {
    override suspend fun delete(key: String): ScraperSpecification =
        scraperSpecificationRepository
            .findByName(key)
            ?.also { scraperSpecificationRepository.delete(it) }
            ?.data
            ?.also { eventPublisher.publishEventAsync(ScraperSpecificationEntityDeletedEvent(it)) }
            ?: throw EntityNotFoundException(ScraperSpecificationEntity::class, key)

    @Transactional
    override fun deleteAll(): Flow<ScraperSpecification> =
        flow {
            scraperSpecificationRepository
                .findAll()
                .toList()
                .also { scraperSpecificationRepository.deleteAll(it) }
                .map { it.data }
                .also { eventPublisher.publishEventAsync(ScraperSpecificationEntityBatchDeletedEvent(it)) }
                .asFlow()
                .let { emitAll(it) }
        }

    @Transactional(readOnly = true)
    override suspend fun find(key: String): ScraperSpecification =
        scraperSpecificationRepository
            .findByName(key)
            ?.data
            ?: throw EntityNotFoundException(ScraperSpecificationEntity::class, key)

    @Transactional
    override fun findAll(): Flow<ScraperSpecification> =
        scraperSpecificationRepository
            .findAll()
            .map { it.data }

    @Transactional
    override fun findAll(keys: Collection<String>): Flow<ScraperSpecification> =
        scraperSpecificationRepository
            .findAllByNameIn(keys)
            .map { it.data }

    @Transactional
    override suspend fun save(data: ScraperSpecification): ScraperSpecification =
        scraperSpecificationRepository
            .findByName(data.name)
            ?.let { throw EntityConflictException(ScraperSpecificationEntity::class, data.name) }
            ?: scraperSpecificationRepository
                .save(ScraperSpecificationEntity(data))
                .data
                .also { eventPublisher.publishEventAsync(ScraperSpecificationEntityCreatedEvent(it)) }

    @Transactional
    override suspend fun update(data: ScraperSpecification): ScraperSpecification =
        scraperSpecificationRepository
            .findByName(data.name)
            ?.let { scraperSpecificationRepository.save(it.clone(data = data)) }
            ?.data
            ?.also { eventPublisher.publishEventAsync(ScraperSpecificationEntityUpdatedEvent(it)) }
            ?: throw EntityNotFoundException(ScraperSpecificationEntity::class, data.name)
}
