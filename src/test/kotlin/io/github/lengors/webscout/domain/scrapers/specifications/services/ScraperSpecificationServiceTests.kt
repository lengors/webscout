package io.github.lengors.webscout.domain.scrapers.specifications.services

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.testing.postgres.configurations.PostgresTestContainerConfiguration
import io.github.lengors.webscout.testing.utilities.TestingSpecifications
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(PostgresTestContainerConfiguration::class)
@SpringBootTest
class ScraperSpecificationServiceTests {
    @Autowired
    private lateinit var scraperSpecificationService: ScraperSpecificationService

    private val testBarSpecification: ScraperSpecification =
        TestingSpecifications.buildTestSpecification(name = "test-bar")
    private val testFooSpecification: ScraperSpecification =
        TestingSpecifications.buildTestSpecification(name = "test-foo", locale = "'en-GB'")

    @AfterEach
    fun cleanup() =
        runBlocking {
            scraperSpecificationService
                .deleteAll()
                .collect()
        }

    @Test
    fun `should correctly find all specifications based on names`() {
        runBlocking {
            scraperSpecificationService.save(testBarSpecification)
            scraperSpecificationService.save(testFooSpecification)
            scraperSpecificationService
                .findAll(listOf("test-bar"))
                .toList()
                .also {
                    Assertions.assertEquals(listOf(testBarSpecification), it)
                }
        }
    }
}
