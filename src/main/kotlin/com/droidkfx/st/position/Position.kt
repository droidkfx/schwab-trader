package com.droidkfx.st.position

import kotlinx.serialization.Serializable

@Serializable
data class Position(
    val symbol: String,
    val quantity: Double,
)