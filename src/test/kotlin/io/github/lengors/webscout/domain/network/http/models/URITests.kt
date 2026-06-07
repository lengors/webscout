package io.github.lengors.webscout.domain.network.http.models

import org.junit.jupiter.api.Test
import java.net.URI

class URITests {
    @Test
    fun `should correctly identify same origin - same host, same scheme, same port`() {
        val uri1 = URI("http://example.com:8080/api")
        val uri2 = URI("http://example.com:8080/other")
        assert(uri1.isSameOrigin(uri2))
    }

    @Test
    fun `should correctly identify same origin - same host, same scheme, default port (explicit vs implicit)`() {
        val uri1 = URI("http://example.com:80/api")
        val uri2 = URI("http://example.com/other")
        assert(uri1.isSameOrigin(uri2))
    }

    @Test
    fun `should correctly identify same origin - same host, current is https, target is http with default ports`() {
        val uri1 = URI("https://example.com:443/api")
        val uri2 = URI("http://example.com/other")
        assert(uri1.isSameOrigin(uri2))
    }

    @Test
    fun `should correctly identify different origin - same host, current is http, target is https with default ports`() {
        val uri1 = URI("http://example.com:80/api")
        val uri2 = URI("https://example.com/other")
        assert(!uri1.isSameOrigin(uri2))
    }

    @Test
    fun `should correctly identify different origin - different hosts`() {
        val uri1 = URI("http://example.com/api")
        val uri2 = URI("http://other.com/api")
        assert(!uri1.isSameOrigin(uri2))
    }

    @Test
    fun `should correctly identify different origin - different ports`() {
        val uri1 = URI("http://example.com:8080/api")
        val uri2 = URI("http://example.com:8081/api")
        assert(!uri1.isSameOrigin(uri2))
    }

    @Test
    fun `should correctly identify same origin - different schemes (not http vs https)`() {
        val uri1 = URI("ftp://example.com:21/api")
        val uri2 = URI("ssh://example.com:21/api")
        assert(uri1.isSameOrigin(uri2))
    }
}
