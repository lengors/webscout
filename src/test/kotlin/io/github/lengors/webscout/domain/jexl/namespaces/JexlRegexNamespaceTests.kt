package io.github.lengors.webscout.domain.jexl.namespaces

import org.junit.jupiter.api.Test

class JexlRegexNamespaceTests {
    @Test
    fun `should correctly match regex`() {
        val result = JexlRegexNamespace.match("abc 123 def", "\\d+")
        assert(result == "123")
    }

    @Test
    fun `should correctly match regex with group`() {
        val result = JexlRegexNamespace.match("abc 123 def", "(\\d+)", 1)
        assert(result == "123")
    }

    @Test
    fun `should return null if no match`() {
        val result = JexlRegexNamespace.match("abc def", "\\d+")
        assert(result == null)
    }

    @Test
    fun `should handle null value`() {
        val result = JexlRegexNamespace.match(null, "\\d+")
        assert(result == null)
    }

    @Test
    fun `should handle case insensitivity and multiline`() {
        val src =
            """
            ABC
            123
            def
            """.trimIndent()
        assert(JexlRegexNamespace.match(src, "abc") == "ABC")
        assert(JexlRegexNamespace.match(src, "^123$") == "123")
    }
}
