package com.droidkfx.st.util.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

typealias KInstant = @Serializable(with = InstantSerializer::class) Instant

object InstantSerializer : KSerializer<Instant> {
    // Fallback formatter for Schwab API responses that use +HHMM offset without colon (e.g., +0000, -0500)
    private val offsetFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.time.Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        val str = decoder.decodeString()
        return try {
            Instant.parse(str)
        } catch (_: DateTimeParseException) {
            offsetFormatter.parse(str, Instant::from) // Schwab API uses +HHMM offset without colon
        }
    }
}