package com.droidkfx.st.orders

import com.droidkfx.st.schwab.client.Instruction
import com.droidkfx.st.schwab.client.Order
import com.droidkfx.st.schwab.client.OrderType
import com.droidkfx.st.schwab.client.Status
import com.droidkfx.st.util.serialization.KBigDecimal
import com.droidkfx.st.util.serialization.KInstant
import kotlinx.serialization.Serializable

@Serializable
data class CachedOrder(
    val orderId: Long,
    val accountId: String,
    val status: Status,
    val symbol: String,
    val instruction: Instruction? = null,
    val orderType: OrderType? = null,
    val quantity: KBigDecimal? = null,
    val filledQuantity: KBigDecimal? = null,
    val remainingQuantity: KBigDecimal? = null,
    val price: KBigDecimal? = null,
    val enteredTime: KInstant? = null,
    val closeTime: KInstant? = null,
    val strategyId: String? = null, // reserved — lot attribution pipeline populates this
)

fun Order.toCachedOrder(accountId: String): CachedOrder? {
    val id = this.orderId ?: return null
    return CachedOrder(
        orderId = id,
        accountId = accountId,
        status = this.status ?: Status.UNKNOWN,
        symbol = this.orderLegCollection?.firstOrNull()?.instrument?.symbol ?: "",
        instruction = this.orderLegCollection?.firstOrNull()?.instruction,
        orderType = this.orderType,
        quantity = this.quantity,
        filledQuantity = this.filledQuantity,
        remainingQuantity = this.remainingQuantity,
        price = this.price,
        enteredTime = this.enteredTime,
        closeTime = this.closeTime,
    )
}

fun Status.isOpen(): Boolean = when (this) {
    Status.CANCELED, Status.FILLED, Status.EXPIRED, Status.REJECTED, Status.REPLACED -> false
    else -> true
}
