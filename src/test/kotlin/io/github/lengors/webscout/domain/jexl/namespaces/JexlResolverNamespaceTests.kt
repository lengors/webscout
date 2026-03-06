package io.github.lengors.webscout.domain.jexl.namespaces

import org.junit.jupiter.api.Test
import java.net.URI

class JexlResolverNamespaceTests {
    @Test
    fun `should correctly get name without extension`() {
        assert(JexlResolverNamespace.name("path/to/file.txt") == "file")
        assert(JexlResolverNamespace.name("file.txt") == "file")
        assert(JexlResolverNamespace.name(null) == null)
    }

    @Test
    fun `should correctly resolve URI with URI`() {
        val base = URI("https://example.com/api/")
        val path = URI("v1/users")
        val resolved = JexlResolverNamespace.resolve(base, path)
        assert(resolved == URI("https://example.com/api/v1/users"))
    }

    @Test
    fun `should correctly resolve URI with string`() {
        val base = URI("https://example.com/api/")
        val path = "v1/users"
        val resolved = JexlResolverNamespace.resolve(base, path)
        assert(resolved == URI("https://example.com/api/v1/users"))
    }
}
