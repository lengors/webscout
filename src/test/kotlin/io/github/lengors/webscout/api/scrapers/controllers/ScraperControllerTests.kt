package io.github.lengors.webscout.api.scrapers.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.lengors.protoscout.domain.scrapers.models.ScraperInputs
import io.github.lengors.protoscout.domain.scrapers.models.ScraperProfile
import io.github.lengors.protoscout.domain.scrapers.models.ScraperRequest
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponse
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseError
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseErrorCode
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResult
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultBrand
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTimeInstant
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDateTimeInstantGrain
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDescriptionlessDetail
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultDescriptiveDetail
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultGrading
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultNoiseLevel
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultPrice
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultQuantity
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultQuantityModifier
import io.github.lengors.protoscout.domain.scrapers.models.ScraperResponseResultStock
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequestParser
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequirementType
import io.github.lengors.webscout.domain.scrapers.models.ScraperDefinitionPayloadType
import io.github.lengors.webscout.domain.scrapers.specifications.services.ScraperSpecificationService
import io.github.lengors.webscout.testing.duckling.configurations.DucklingTestContainerConfiguration
import io.github.lengors.webscout.testing.postgres.configurations.PostgresTestContainerConfiguration
import io.github.lengors.webscout.testing.utilities.TestingSpecifications
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters
import java.time.ZonedDateTime
import java.util.Date

@Import(DucklingTestContainerConfiguration::class, PostgresTestContainerConfiguration::class)
@SpringBootTest
@AutoConfigureWebTestClient
class ScraperControllerTests {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var scraperSpecificationService: ScraperSpecificationService

    private val mockWebServer = MockWebServer()

    @AfterEach
    fun cleanup(): Unit =
        runBlocking {
            scraperSpecificationService
                .deleteAll()
                .collect()
        }

    @Test
    fun `should correctly scrap json endpoint using specification`() {
        testSuccessfulScrapping(ScraperSpecificationRequestParser.JSON) {
            setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "rows" to
                                listOf(
                                    mapOf(
                                        "description" to "test-description-0",
                                        "brand" to "test-brand-0",
                                        "brandImage" to "/other",
                                        "price" to "10€",
                                        "image" to "/otherImage",
                                        "stocks" to
                                            listOf(
                                                mapOf(
                                                    "availability" to 1,
                                                    "storage" to "Foo",
                                                    "delivery" to "January 3rd, 2025",
                                                ),
                                            ),
                                        "grip" to "E",
                                        "noise" to "C",
                                        "decibels" to "70dB",
                                        "consumption" to "E",
                                        "extra" to "test-extra-0",
                                        "extraImages" to
                                            listOf(
                                                "other",
                                                "otherImage",
                                            ),
                                    ),
                                    mapOf(
                                        "description" to "test-description-1",
                                        "brand" to "test-brand-1",
                                        "brandImage" to "http://localhost:9999/foo",
                                        "price" to "15€",
                                        "image" to "http://localhost:9999/fooImage",
                                        "stocks" to
                                            listOf(
                                                mapOf(
                                                    "availability" to "+10",
                                                    "storage" to "Test",
                                                    "delivery" to "January 10th, 2025",
                                                ),
                                                mapOf(
                                                    "availability" to ">11",
                                                    "storage" to "Other",
                                                    "delivery" to "January 20th, 2025",
                                                ),
                                            ),
                                        "grip" to "A",
                                        "noise" to "A",
                                        "decibels" to "50dB",
                                        "consumption" to "A",
                                        "extra" to "test-extra-1",
                                        "extraImages" to
                                            listOf(
                                                "foo",
                                                "fooImage",
                                            ),
                                    ),
                                ),
                        ),
                    ),
                )
        }
    }

    @Test
    fun `should correctly scrap html endpoint using specification`() {
        testSuccessfulScrapping(ScraperSpecificationRequestParser.HTML) {
            setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE)
                .setBody(
                    """
                    <table>
                        <tbody>
                            <tr>
                                <td class="description">test-description-0</td>
                                <td class="brand">test-brand-0</td>
                                <td class="brandImage">
                                    <a href="/other" />
                                </td>
                                <td class="price">10€</td>
                                <td class="image">
                                    <a href="/otherImage" />
                                </td>
                                <td class="stocks">
                                    <div>
                                        <p class="availability">1</p>
                                        <p class="storage">Foo</p>
                                        <p class="delivery">January 3rd, 2025</p>
                                    </div>
                                </td>
                                <td class="grip">E</td>
                                <td class="noise">C</td>
                                <td class="decibels">70dB</td>
                                <td class="consumption">E</td>
                                <td class="extra">test-extra-0</td>
                                <td class="extraImages">
                                    <a href="other" />
                                    <a href="otherImage" />
                                </td>
                            </tr>
                            <tr>
                                <td class="description">test-description-1</td>
                                <td class="brand">test-brand-1</td>
                                <td class="brandImage">
                                    <a href="http://localhost:9999/foo" />
                                </td>
                                <td class="price">15€</td>
                                <td class="image">
                                    <a href="http://localhost:9999/fooImage" />
                                </td>
                                <td class="stocks">
                                    <div>
                                        <p class="availability">+10</p>
                                        <p class="storage">Test</p>
                                        <p class="delivery">January 10th, 2025</p>
                                    </div>
                                    <div>
                                        <p class="availability">> 11</p>
                                        <p class="storage">Other</p>
                                        <p class="delivery">January 20th, 2025</p>
                                    </div>
                                </td>
                                <td class="grip">A</td>
                                <td class="noise">A</td>
                                <td class="decibels">50dB</td>
                                <td class="consumption">A</td>
                                <td class="extra">test-extra-1</td>
                                 <td class="extraImages">
                                    <a href="foo" />
                                    <a href="fooImage" />
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    """.trimIndent(),
                )
        }
    }

    @Test
    fun `should fail to scrap due to missing specification`() {
        webTestClient
            .post()
            .uri("/scrapers")
            .body(
                BodyInserters.fromValue(
                    ScraperRequest(
                        "test",
                        listOf(ScraperProfile("other", buildScraperInputs())),
                    ),
                ),
            ).exchange()
            .expectBodyList<ScraperResponse>()
            .hasSize(1)
            .contains(
                *arrayOf(
                    ScraperResponseError(
                        ScraperResponseErrorCode.SPECIFICATION_NOT_FOUND,
                        "other",
                        null,
                        "404 NOT_FOUND \"Entity {type=ScraperSpecificationEntity} not found for {query=other}\"",
                    ),
                ),
            )
    }

    @Test
    fun `should fail to scrap due to handler not found`() {
        val (_, specification) =
            setupScrapping(ScraperSpecificationRequestParser.JSON, true) {
                setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody("{}")
            }

        webTestClient
            .post()
            .uri("/scrapers")
            .body(
                BodyInserters.fromValue(
                    ScraperRequest(
                        "test",
                        listOf(ScraperProfile(specification.name, buildScraperInputs())),
                    ),
                ),
            ).exchange()
            .expectBodyList<ScraperResponse>()
            .hasSize(1)
            .contains(
                *arrayOf(
                    ScraperResponseError(
                        ScraperResponseErrorCode.HANDLER_NOT_FOUND,
                        specification.name,
                        null,
                        "Could not find an handler for specification '${specification.name}'",
                    ),
                ),
            )
    }

    @Test
    fun `should fail to scrap due to missing input`() {
        val (_, specification) =
            setupScrapping(ScraperSpecificationRequestParser.JSON, true) {
                setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody("{}")
            }

        webTestClient
            .post()
            .uri("/scrapers")
            .body(
                BodyInserters.fromValue(
                    ScraperRequest(
                        "test",
                        listOf(ScraperProfile(specification.name, buildScraperInputs(TestInputType.MISSING))),
                    ),
                ),
            ).exchange()
            .expectBodyList<ScraperResponse>()
            .hasSize(1)
            .contains(
                *arrayOf(
                    ScraperResponseError(
                        ScraperResponseErrorCode.INPUT_MISSING,
                        specification.name,
                        null,
                        "Input for requirement 'test' of type '${ScraperSpecificationRequirementType.EMAIL}' missing",
                    ),
                ),
            )
    }

    @Test
    fun `should fail to scrap due to invalid input`() {
        val (_, specification) =
            setupScrapping(ScraperSpecificationRequestParser.JSON, true) {
                setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody("{}")
            }

        webTestClient
            .post()
            .uri("/scrapers")
            .body(
                BodyInserters.fromValue(
                    ScraperRequest(
                        "test",
                        listOf(ScraperProfile(specification.name, buildScraperInputs(TestInputType.INVALID))),
                    ),
                ),
            ).exchange()
            .expectBodyList<ScraperResponse>()
            .hasSize(1)
            .contains(
                *arrayOf(
                    ScraperResponseError(
                        ScraperResponseErrorCode.INVALID_INPUT,
                        specification.name,
                        null,
                        "Invalid input for requirement 'test' of type '${ScraperSpecificationRequirementType.EMAIL}'",
                    ),
                ),
            )
    }

    private fun buildScraperInputs(testInputType: TestInputType = TestInputType.VALID): ScraperInputs =
        ScraperInputs().apply {
            when (testInputType) {
                TestInputType.VALID -> setAdditionalProperty("test", "mail@test.com")
                TestInputType.INVALID -> setAdditionalProperty("test", "test")
                TestInputType.MISSING -> Unit
            }
        }

    private fun setupScrapping(
        requestParser: ScraperSpecificationRequestParser,
        brokenGateLogic: Boolean = false,
        configureMockWebServerResponse: MockResponse.() -> MockResponse,
    ): Pair<String, ScraperSpecification> {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .let { configureMockWebServerResponse(it) },
        )

        val url: HttpUrl = mockWebServer.url("/test")
        val host = "${url.host}:${url.port}"

        val specification: ScraperSpecification =
            TestingSpecifications.buildTestSpecification(
                host = host,
                requestParser = requestParser,
                brokenGateLogic = brokenGateLogic,
                payloadType =
                    when (requestParser) {
                        ScraperSpecificationRequestParser.HTML -> ScraperDefinitionPayloadType.DATA
                        ScraperSpecificationRequestParser.JSON -> ScraperDefinitionPayloadType.JSON
                        ScraperSpecificationRequestParser.TEXT -> null
                    },
            )
        runBlocking {
            runCatching { scraperSpecificationService.save(specification) }
        }

        return host to specification
    }

    private fun testSuccessfulScrapping(
        requestParser: ScraperSpecificationRequestParser,
        configureMockWebServerResponse: MockResponse.() -> MockResponse,
    ) {
        val (host, specification) =
            setupScrapping(
                requestParser,
                configureMockWebServerResponse = configureMockWebServerResponse,
            )
        val baseUrl = "http://$host"

        val expectedResults =
            arrayOf(
                ScraperResponseResult(
                    baseUrl,
                    specification.name,
                    "TEST-DESCRIPTION-0",
                    ScraperResponseResultBrand("test-brand-0", "$baseUrl/other"),
                    ScraperResponseResultPrice("10", "EUR"),
                    "$baseUrl/otherImage",
                    listOf(
                        ScraperResponseResultStock(
                            ScraperResponseResultQuantity(1, ScraperResponseResultQuantityModifier.EXACT),
                            "Foo",
                            ScraperResponseResultDateTimeInstant(
                                Date.from(ZonedDateTime.parse("2025-01-03T00:00:00.000Z").toInstant()),
                                ScraperResponseResultDateTimeInstantGrain.DAY,
                            ),
                        ),
                    ),
                    ScraperResponseResultGrading.E,
                    ScraperResponseResultNoiseLevel.C,
                    70,
                    ScraperResponseResultGrading.E,
                    listOf(
                        ScraperResponseResultDescriptiveDetail(
                            "descriptive",
                            "test-extra-0",
                            "$baseUrl/test/other",
                        ),
                        ScraperResponseResultDescriptionlessDetail(
                            "descriptionless",
                            "$baseUrl/test/otherImage",
                        ),
                    ),
                ),
                ScraperResponseResult(
                    baseUrl,
                    specification.name,
                    "TEST-DESCRIPTION-1",
                    ScraperResponseResultBrand("test-brand-1", "http://localhost:9999/foo"),
                    ScraperResponseResultPrice("15", "EUR"),
                    "http://localhost:9999/fooImage",
                    listOf(
                        ScraperResponseResultStock(
                            ScraperResponseResultQuantity(10, ScraperResponseResultQuantityModifier.AT_LEAST),
                            "Test",
                            ScraperResponseResultDateTimeInstant(
                                Date.from(ZonedDateTime.parse("2025-01-10T00:00:00.000Z").toInstant()),
                                ScraperResponseResultDateTimeInstantGrain.DAY,
                            ),
                        ),
                        ScraperResponseResultStock(
                            ScraperResponseResultQuantity(11, ScraperResponseResultQuantityModifier.AT_LEAST),
                            "Other",
                            ScraperResponseResultDateTimeInstant(
                                Date.from(ZonedDateTime.parse("2025-01-20T00:00:00.000Z").toInstant()),
                                ScraperResponseResultDateTimeInstantGrain.DAY,
                            ),
                        ),
                    ),
                    ScraperResponseResultGrading.A,
                    ScraperResponseResultNoiseLevel.A,
                    50,
                    ScraperResponseResultGrading.A,
                    listOf(
                        ScraperResponseResultDescriptiveDetail(
                            "descriptive",
                            "test-extra-1",
                            "$baseUrl/test/foo",
                        ),
                        ScraperResponseResultDescriptionlessDetail(
                            "descriptionless",
                            "$baseUrl/test/fooImage",
                        ),
                    ),
                ),
            )

        webTestClient
            .post()
            .uri("/scrapers")
            .body(
                BodyInserters.fromValue(
                    ScraperRequest(
                        "test",
                        listOf(ScraperProfile(specification.name, buildScraperInputs())),
                    ),
                ),
            ).exchange()
            .expectBodyList<ScraperResponse>()
            .hasSize(expectedResults.size)
            .contains(*expectedResults)

        Assertions.assertEquals("/test?term=test", mockWebServer.takeRequest().path)
    }

    private enum class TestInputType {
        VALID,
        INVALID,
        MISSING,
    }
}
