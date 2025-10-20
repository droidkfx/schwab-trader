package com.droidkfx.st.position

import kotlinx.serialization.Serializable

@Serializable
data class PositionTarget(val symbol: String, val allocationTarget: Double)
