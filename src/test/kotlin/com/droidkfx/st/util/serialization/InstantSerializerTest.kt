package com.droidkfx.st.util.serialization

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InstantSerializerTest {

    private val json = Json

    @Test
    fun `serialize produces a quoted JSON string`() {
        val instant = Instant.parse("2024-01-15T10:30:00Z")
        val encoded = json.encodeToString(InstantSerializer, instant)
        assertTrue(encoded.startsWith("\""), "Expected a JSON string value, got: $encoded")
        assertTrue(encoded.endsWith("\""), "Expected a JSON string value, got: $encoded")
    }

    @Test
    fun `serialize encodes using Instant toString`() {
        val instant = Instant.parse("2024-06-15T14:25:30Z")
        val encoded = json.encodeToString(InstantSerializer, instant)
        assertEquals("\"${instant}\"", encoded)
    }

    @Test
    fun `deserialize parses offset format string`() {
        // The formatter pattern is "yyyy-MM-dd'T'HH:mm:ssZ" which expects +0000 style offsets
        val input = "\"2024-01-15T10:30:00+0000\""
        val decoded = json.decodeFromString(InstantSerializer, input)
        assertEquals(Instant.parse("2024-01-15T10:30:00Z"), decoded)
    }

    @Test
    fun `deserialize parses negative offset`() {
        val input = "\"2024-03-20T08:00:00-0500\""
        val decoded = json.decodeFromString(InstantSerializer, input)
        assertEquals(Instant.parse("2024-03-20T13:00:00Z"), decoded)
    }

    @Test
    fun `deserialize parses positive non-UTC offset`() {
        val input = "\"2024-06-01T12:00:00+0530\""
        val decoded = json.decodeFromString(InstantSerializer, input)
        assertEquals(Instant.parse("2024-06-01T06:30:00Z"), decoded)
    }
}
