package io.github.lengors.webscout.api.scrapers.specifications.controllers

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.persistence.services.UniqueKeyPersistenceService
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/v1/scrapers/specifications", "/scrapers/specifications"])
class ScraperSpecificationController(
    private val persistenceService: UniqueKeyPersistenceService<ScraperSpecification>,
) {
    @DeleteMapping("/{name}")
    suspend fun delete(
        @PathVariable name: String,
    ): ScraperSpecification = persistenceService.delete(name)

    @DeleteMapping
    fun deleteAll(
        @RequestParam(name = "name", required = false) names: Collection<String>? = null,
    ): Flow<ScraperSpecification> =
        names
            ?.let { persistenceService.deleteAll(it) }
            ?: persistenceService.deleteAll()

    @GetMapping("/{name}")
    suspend fun find(
        @PathVariable name: String,
    ): ScraperSpecification = persistenceService.find(name)

    @GetMapping
    fun findAll(
        @RequestParam(name = "name", required = false) names: Collection<String>? = null,
    ): Flow<ScraperSpecification> =
        names
            ?.let { persistenceService.findAll(it) }
            ?: persistenceService.findAll()

    @PutMapping
    suspend fun save(
        @RequestBody specification: ScraperSpecification,
    ): ScraperSpecification = persistenceService.save(specification)

    @PatchMapping
    suspend fun update(
        @RequestBody specification: ScraperSpecification,
    ): ScraperSpecification = persistenceService.update(specification)
}
