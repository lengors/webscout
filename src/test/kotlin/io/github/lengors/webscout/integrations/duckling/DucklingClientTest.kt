package io.github.lengors.webscout.integrations.duckling

import io.github.lengors.webscout.integrations.duckling.client.DucklingClient
import io.github.lengors.webscout.integrations.duckling.models.DucklingAmountOfMoneyRequest
import io.github.lengors.webscout.integrations.duckling.models.DucklingAmountOfMoneyResponseValue
import io.github.lengors.webscout.integrations.duckling.models.DucklingDateTimeInstantResponseValue
import io.github.lengors.webscout.integrations.duckling.models.DucklingDateTimeRangeResponseValue
import io.github.lengors.webscout.integrations.duckling.models.DucklingDateTimeRequest
import io.github.lengors.webscout.integrations.duckling.models.DucklingGrain
import io.github.lengors.webscout.testing.duckling.configurations.DucklingTestContainerConfiguration
import io.github.lengors.webscout.testing.postgres.configurations.PostgresTestContainerConfiguration
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

@Import(DucklingTestContainerConfiguration::class, PostgresTestContainerConfiguration::class)
@SpringBootTest
class DucklingClientTest {
    companion object {
        private val locale = Locale.forLanguageTag("en-GB")
        private val zoneId = ZoneId.of("Europe/London")
    }

    @Autowired
    private lateinit var ducklingClient: DucklingClient

    @Test
    fun `should correctly parse amount of money`(): Unit =
        ducklingClient
            .parse(DucklingAmountOfMoneyRequest("10£", locale, zoneId))
            ?.value
            .let {
                Assertions.assertNotNull(it)
                it?.let { Assertions.assertEquals(DucklingAmountOfMoneyResponseValue(10.0, "£"), it) }
            }

    @Test
    fun `should correctly parse date time instant`(): Unit =
        ducklingClient
            .parse(DucklingDateTimeRequest("January 1st, 2025", locale, zoneId))
            ?.value
            .let { actual ->
                Assertions.assertNotNull(actual)
                actual?.let {
                    Assertions.assertEquals(
                        DucklingDateTimeInstantResponseValue(
                            ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZoneId.of("Z")),
                            DucklingGrain.DAY,
                        ),
                        it,
                    )
                }
            }

    @Test
    fun `should correctly parse date range instant`(): Unit =
        ducklingClient
            .parse(DucklingDateTimeRequest("Between January 1st, 2025 and January 2nd, 2025", locale, zoneId))
            ?.value
            .let { actual ->
                Assertions.assertNotNull(actual)
                actual?.let {
                    Assertions.assertEquals(
                        DucklingDateTimeRangeResponseValue(
                            DucklingDateTimeInstantResponseValue(
                                ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZoneId.of("Z")),
                                DucklingGrain.DAY,
                            ),
                            DucklingDateTimeInstantResponseValue(
                                ZonedDateTime.of(2025, 1, 3, 0, 0, 0, 0, ZoneId.of("Z")),
                                DucklingGrain.DAY,
                            ),
                        ),
                        it,
                    )
                }
            }
}
