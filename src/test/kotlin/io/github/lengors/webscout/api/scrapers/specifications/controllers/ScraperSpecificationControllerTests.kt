package io.github.lengors.webscout.api.scrapers.specifications.controllers

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.events.models.Event
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityBatchDeletedEvent
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityCreatedEvent
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityDeletedEvent
import io.github.lengors.webscout.domain.scrapers.specifications.events.ScraperSpecificationEntityUpdatedEvent
import io.github.lengors.webscout.domain.scrapers.specifications.services.ScraperSpecificationService
import io.github.lengors.webscout.testing.events.configurations.TestingEventListenerConfiguration
import io.github.lengors.webscout.testing.events.services.TestingEventListener
import io.github.lengors.webscout.testing.postgres.configurations.PostgresTestContainerConfiguration
import io.github.lengors.webscout.testing.utilities.TestingSpecifications
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.awaitility.Awaitility
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters
import java.time.Duration

@Import(PostgresTestContainerConfiguration::class, TestingEventListenerConfiguration::class)
@SpringBootTest
@AutoConfigureWebTestClient
class ScraperSpecificationControllerTests {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var testingEventListener: TestingEventListener

    @Autowired
    private lateinit var scraperSpecificationService: ScraperSpecificationService

    private val testSpecification: ScraperSpecification = TestingSpecifications.buildTestSpecification()
    private val testInvalidSpecification: ScraperSpecification =
        TestingSpecifications.buildTestSpecification(locale = "inputs'.'locales")
    private val testPatchSpecification: ScraperSpecification =
        TestingSpecifications.buildTestSpecification(locale = "'en-GB'")

    @AfterEach
    fun cleanup() {
        runBlocking {
            scraperSpecificationService
                .deleteAll()
                .collect()
        }
        Awaitility
            .await()
            .until { testingEventListener.count > 0 }
        testingEventListener.flushEvents()
    }

    @Test
    fun `should correctly put, find and delete specification`() {
        webTestClient
            .put(testSpecification)
            .assert(testSpecification)

        webTestClient.assertOne()

        webTestClient
            .delete(testSpecification.name)
            .assert(testSpecification)

        webTestClient.assertNone()
    }

    @Test
    fun `should correctly patch and delete all specifications`() {
        webTestClient
            .put(testSpecification)
            .assert(testSpecification)

        webTestClient
            .patch(testPatchSpecification)
            .assert(testPatchSpecification)

        webTestClient
            .delete()
            .submit()
            .hasSize(1)
            .contains(testPatchSpecification)
            .doesNotContain(testSpecification)
            .consumeWith<WebTestClient.ListBodySpec<ScraperSpecification>> {
                it.assertEvents(::ScraperSpecificationEntityBatchDeletedEvent)
            }

        webTestClient.assertNone()
    }

    @Test
    fun `should correctly find specification`() {
        webTestClient
            .put(testSpecification)
            .assert(testSpecification)

        webTestClient
            .get(testSpecification.name)
            .assert(testSpecification)
    }

    @Test
    fun `should fail to patch invalid specification`() {
        webTestClient
            .put(testSpecification)
            .assert(testSpecification)

        webTestClient
            .patch(testInvalidSpecification)
            .consumeWith { Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, it.status) }

        webTestClient.assertOne()
    }

    @Test
    fun `should fail to put invalid specification`() {
        webTestClient
            .put(testInvalidSpecification)
            .consumeWith { Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, it.status) }

        webTestClient.assertNone()
    }

    @Test
    fun `should fail to patch non-existing specification`() {
        webTestClient
            .patch(testPatchSpecification)
            .consumeWith { Assertions.assertEquals(HttpStatus.NOT_FOUND, it.status) }

        webTestClient.assertNone()
    }

    @Test
    fun `should fail to delete non-existing specification`() {
        webTestClient
            .delete(testSpecification.name)
            .consumeWith { Assertions.assertEquals(HttpStatus.NOT_FOUND, it.status) }

        webTestClient.assertNone()
    }

    @Test
    fun `should fail to find non-existing specification`() {
        webTestClient
            .get(testSpecification.name)
            .consumeWith { Assertions.assertEquals(HttpStatus.NOT_FOUND, it.status) }
    }

    @Test
    fun `should fail to put existing specification`() {
        webTestClient
            .put(testSpecification)
            .assert(testSpecification)

        webTestClient
            .put(testPatchSpecification)
            .consumeWith { Assertions.assertEquals(HttpStatus.CONFLICT, it.status) }

        webTestClient.assertOne()
    }

    private fun WebTestClient.BodySpec<ScraperSpecification, *>.assert(scraperSpecification: ScraperSpecification) {
        consumeWith {
            Assertions.assertTrue(it.status.is2xxSuccessful)
            Assertions.assertNotNull(it.responseBody)
            Assertions.assertEquals(scraperSpecification, it.responseBody)
        }
    }

    private fun <T> EntityExchangeResult<T>.assertEvents(expectedEventProducer: (T) -> Event) {
        responseBody
            ?.let { listOf(expectedEventProducer(it)) }
            ?.also {
                Awaitility
                    .await()
                    .atMost(Duration.ofSeconds(20))
                    .until { testingEventListener.count >= it.size }
                Assertions.assertEquals(it, testingEventListener.flushEvents())
            }
    }

    private fun WebTestClient.assertOne() {
        this
            .get()
            .submit()
            .hasSize(1)
            .contains(testSpecification)
    }

    private fun WebTestClient.assertNone() {
        this
            .get()
            .submit()
            .hasSize(0)
    }

    private fun WebTestClient.delete(name: String): WebTestClient.BodySpec<ScraperSpecification, *> =
        this
            .delete()
            .submit(name)
            .consumeWith {
                if (it.status.is2xxSuccessful) {
                    it.assertEvents(::ScraperSpecificationEntityDeletedEvent)
                }
            }

    private fun WebTestClient.get(name: String): WebTestClient.BodySpec<ScraperSpecification, *> =
        this
            .get()
            .submit(name)

    private fun WebTestClient.patch(scraperSpecification: ScraperSpecification): WebTestClient.BodySpec<ScraperSpecification, *> =
        this
            .patch()
            .submit(scraperSpecification)
            .consumeWith {
                if (it.status.is2xxSuccessful) {
                    it.assertEvents(::ScraperSpecificationEntityUpdatedEvent)
                }
            }

    private fun WebTestClient.put(scraperSpecification: ScraperSpecification): WebTestClient.BodySpec<ScraperSpecification, *> =
        this
            .put()
            .submit(scraperSpecification)
            .consumeWith {
                if (it.status.is2xxSuccessful) {
                    it.assertEvents(::ScraperSpecificationEntityCreatedEvent)
                }
            }

    private fun WebTestClient.RequestBodyUriSpec.submit(
        scraperSpecification: ScraperSpecification,
    ): WebTestClient.BodySpec<ScraperSpecification, *> =
        this
            .uri("/scrapers/specifications")
            .body(BodyInserters.fromValue(scraperSpecification))
            .exchange()
            .expectBody<ScraperSpecification>()

    private fun WebTestClient.RequestHeadersUriSpec<*>.submit(name: String): WebTestClient.BodySpec<ScraperSpecification, *> =
        this
            .uri("/scrapers/specifications/$name")
            .exchange()
            .expectBody<ScraperSpecification>()

    private fun WebTestClient.RequestHeadersUriSpec<*>.submit(): WebTestClient.ListBodySpec<ScraperSpecification> =
        this
            .uri("/scrapers/specifications")
            .exchange()
            .expectBodyList<ScraperSpecification>()
}
