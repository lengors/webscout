package io.github.lengors.webscout.api.scrapers.controllers

import io.github.lengors.protoscout.domain.scrapers.models.ScraperRequest
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponse
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseErrorCode
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.persistence.services.UniqueKeyPersistenceService
import io.github.lengors.webscout.domain.scrapers.exceptions.handlers.ScraperExceptionHandler
import io.github.lengors.webscout.domain.scrapers.models.ScraperTask
import io.github.lengors.webscout.domain.scrapers.services.ScraperService
import io.github.lengors.webscout.domain.utilities.runCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.mapNotNull
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/v1/scrapers", "/scrapers"])
class ScraperController(
    private val scraperService: ScraperService,
    private val scraperSpecificationPersistenceService: UniqueKeyPersistenceService<ScraperSpecification>,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ScraperController::class.java)
    }

    @PostMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun scrap(
        @RequestBody request: ScraperRequest,
    ): Flow<ScraperResponse> =
        channelFlow {
            with(scraperService) {
                this@channelFlow.scrap(
                    request.profiles
                        .asFlow()
                        .mapNotNull { scraperProfile ->
                            runCatching(
                                logger,
                                ScraperExceptionHandler(
                                    ScraperResponseErrorCode.SPECIFICATION_NOT_FOUND,
                                    scraperProfile.specificationName,
                                ),
                            ) {
                                scraperSpecificationPersistenceService.find(scraperProfile.specificationName)
                            }?.let { ScraperTask(it, request.searchTerm, scraperProfile.inputs.additionalProperties) }
                        },
                )
            }
        }
}
