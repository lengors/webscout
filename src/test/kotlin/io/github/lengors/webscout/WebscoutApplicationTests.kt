package io.github.lengors.webscout

import io.github.lengors.webscout.testing.postgres.configurations.PostgresTestContainerConfiguration
import org.junit.jupiter.api.Test
import org.springframework.boot.fromApplication

class WebscoutApplicationTests {
    @Test
    fun `should correctly boot`() {
        fromApplication<WebscoutApplication>()
            .with(PostgresTestContainerConfiguration::class.java)
            .run()
    }
}
