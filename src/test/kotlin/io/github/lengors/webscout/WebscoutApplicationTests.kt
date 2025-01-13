package io.github.lengors.webscout

import org.junit.jupiter.api.Test
import org.springframework.boot.fromApplication

class WebscoutApplicationTests {
    @Test
    fun `should correctly boot`() {
        fromApplication<WebscoutApplication>()
            .run()
    }
}
