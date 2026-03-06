package io.github.lengors.webscout.domain.jexl.namespaces

import org.junit.jupiter.api.Test

class JexlTypingNamespaceTests {
    @Test
    fun `should correctly identify boolean`() {
        assert(JexlTypingNamespace.isBoolean(true))
        assert(JexlTypingNamespace.isBoolean(false))
        assert(!JexlTypingNamespace.isBoolean("true"))
        assert(!JexlTypingNamespace.isBoolean(null))
    }

    @Test
    fun `should correctly identify byte`() {
        assert(JexlTypingNamespace.isByte(1.toByte()))
        assert(!JexlTypingNamespace.isByte(1))
    }

    @Test
    fun `should correctly identify char`() {
        assert(JexlTypingNamespace.isChar('a'))
        assert(!JexlTypingNamespace.isChar("a"))
    }

    @Test
    fun `should correctly identify double`() {
        assert(JexlTypingNamespace.isDouble(1.0))
        assert(!JexlTypingNamespace.isDouble(1.0f))
    }

    @Test
    fun `should correctly identify float`() {
        assert(JexlTypingNamespace.isFloat(1.0f))
        assert(!JexlTypingNamespace.isFloat(1.0))
    }

    @Test
    fun `should correctly identify int`() {
        assert(JexlTypingNamespace.isInt(1))
        assert(!JexlTypingNamespace.isInt(1L))
    }

    @Test
    fun `should correctly identify long`() {
        assert(JexlTypingNamespace.isLong(1L))
        assert(!JexlTypingNamespace.isLong(1))
    }

    @Test
    fun `should correctly identify number`() {
        assert(JexlTypingNamespace.isNumber(1))
        assert(JexlTypingNamespace.isNumber(1L))
        assert(JexlTypingNamespace.isNumber(1.0))
        assert(JexlTypingNamespace.isNumber(1.0f))
        assert(JexlTypingNamespace.isNumber(1.toShort()))
        assert(JexlTypingNamespace.isNumber(1.toByte()))
        assert(!JexlTypingNamespace.isNumber("1"))
    }

    @Test
    fun `should correctly identify short`() {
        assert(JexlTypingNamespace.isShort(1.toShort()))
        assert(!JexlTypingNamespace.isShort(1))
    }

    @Test
    fun `should correctly identify string`() {
        assert(JexlTypingNamespace.isString("abc"))
        assert(!JexlTypingNamespace.isString(123))
    }

    @Test
    fun `should correctly convert to boolean`() {
        assert(JexlTypingNamespace.toBoolean("true") == true)
        assert(JexlTypingNamespace.toBoolean("false") == false)
        assert(JexlTypingNamespace.toBoolean("not-a-boolean") == null)
        assert(JexlTypingNamespace.toBoolean(null) == null)
    }

    @Test
    fun `should correctly convert to byte`() {
        assert(JexlTypingNamespace.toByte("1") == 1.toByte())
        assert(JexlTypingNamespace.toByte("not-a-byte") == null)
    }

    @Test
    fun `should correctly convert to double`() {
        assert(JexlTypingNamespace.toDouble("1.0") == 1.0)
        assert(JexlTypingNamespace.toDouble("not-a-double") == null)
    }

    @Test
    fun `should correctly convert to float`() {
        assert(JexlTypingNamespace.toFloat("1.0") == 1.0f)
        assert(JexlTypingNamespace.toFloat("not-a-float") == null)
    }

    @Test
    fun `should correctly convert to int`() {
        assert(JexlTypingNamespace.toInt("1") == 1)
        assert(JexlTypingNamespace.toInt("not-an-int") == null)
    }

    @Test
    fun `should correctly convert to long`() {
        assert(JexlTypingNamespace.toLong("1") == 1L)
        assert(JexlTypingNamespace.toLong("not-a-long") == null)
    }

    @Test
    fun `should correctly convert to short`() {
        assert(JexlTypingNamespace.toShort("1") == 1.toShort())
        assert(JexlTypingNamespace.toShort("not-a-short") == null)
    }
}
