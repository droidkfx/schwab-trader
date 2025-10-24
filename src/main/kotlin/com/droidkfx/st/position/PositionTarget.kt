package com.droidkfx.st.position

import com.droidkfx.st.util.KBigDecimal
import kotlinx.serialization.Serializable

@Serializable
data class PositionTarget(val symbol: String, val allocationTarget: KBigDecimal)
