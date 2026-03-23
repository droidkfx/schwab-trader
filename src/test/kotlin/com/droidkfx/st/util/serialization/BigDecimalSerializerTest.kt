package com.droidkfx.st.util.serialization

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BigDecimalSerializerTest {

    private val json = Json

    @Test
    fun `serialize preserves exact decimal precision`() {
        val value = BigDecimal("123.456789")
        val encoded = json.encodeToString(BigDecimalSerializer, value)
        assertEquals("123.456789", encoded)
    }

    @Test
    fun `serialize zero`() {
        val value = BigDecimal.ZERO
        val encoded = json.encodeToString(BigDecimalSerializer, value)
        assertEquals("0", encoded)
    }

    @Test
    fun `serialize negative value`() {
        val value = BigDecimal("-42.50")
        val encoded = json.encodeToString(BigDecimalSerializer, value)
        assertEquals("-42.50", encoded)
    }

    @Test
    fun `serialize large integer value`() {
        val value = BigDecimal("99999999999999")
        val encoded = json.encodeToString(BigDecimalSerializer, value)
        assertEquals("99999999999999", encoded)
    }

    @Test
    fun `deserialize from unquoted number literal`() {
        val decoded = json.decodeFromString(BigDecimalSerializer, "123.45")
        assertEquals(0, BigDecimal("123.45").compareTo(decoded))
    }

    @Test
    fun `deserialize from quoted string`() {
        val decoded = json.decodeFromString(BigDecimalSerializer, "\"123.45\"")
        assertEquals(0, BigDecimal("123.45").compareTo(decoded))
    }

    @Test
    fun `deserialize zero`() {
        val decoded = json.decodeFromString(BigDecimalSerializer, "0")
        assertEquals(0, BigDecimal.ZERO.compareTo(decoded))
    }

    @Test
    fun `round-trip preserves exact value`() {
        val original = BigDecimal("9876543210.0123456789")
        val encoded = json.encodeToString(BigDecimalSerializer, original)
        val decoded = json.decodeFromString(BigDecimalSerializer, encoded)
        assertEquals(0, original.compareTo(decoded))
    }

    @Test
    fun `serialized form is not quoted in JSON`() {
        val value = BigDecimal("3.14")
        val encoded = json.encodeToString(BigDecimalSerializer, value)
        assertTrue(!encoded.startsWith("\""), "BigDecimal should serialize as JSON number, not string: $encoded")
    }
}