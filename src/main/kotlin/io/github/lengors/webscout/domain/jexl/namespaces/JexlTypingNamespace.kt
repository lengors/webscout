package io.github.lengors.webscout.domain.jexl.namespaces

import io.github.lengors.webscout.domain.jexl.services.JexlNamespace
import org.springframework.stereotype.Component

@Component("typing")
data object JexlTypingNamespace : JexlNamespace {
    fun isBoolean(value: Any?): Boolean = value is Boolean

    fun isByte(value: Any?): Boolean = value is Byte

    fun isChar(value: Any?): Boolean = value is Char

    fun isDouble(value: Any?): Boolean = value is Double

    fun isFloat(value: Any?): Boolean = value is Float

    fun isInt(value: Any?): Boolean = value is Int

    fun isLong(value: Any?): Boolean = value is Long

    fun isNumber(value: Any?): Boolean =
        isByte(value) || isDouble(value) || isFloat(value) || isInt(value) || isLong(value) || isShort(value)

    fun isShort(value: Any?): Boolean = value is Short

    fun isString(value: Any?): Boolean = value is String

    fun toBoolean(value: String?): Boolean? = value?.toBooleanStrictOrNull()

    fun toByte(value: String?): Byte? = value?.toByteOrNull()

    fun toDouble(value: String?): Double? = value?.toDoubleOrNull()

    fun toFloat(value: String?): Float? = value?.toFloatOrNull()

    fun toInt(value: String?): Int? = value?.toIntOrNull()

    fun toLong(value: String?): Long? = value?.toLongOrNull()

    fun toShort(value: String?): Short? = value?.toShortOrNull()
}
