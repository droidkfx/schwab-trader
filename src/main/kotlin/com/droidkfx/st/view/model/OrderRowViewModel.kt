package com.droidkfx.st.view.model

import com.droidkfx.st.orders.CachedOrder
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault())

data class OrderRowViewModel(
    @field:Column(name = "Symbol", position = 0, editable = false)
    val symbol: String,

    @field:Column(name = "Action", position = 1, editable = false)
    val action: String,

    @field:Column(name = "Type", position = 2, editable = false)
    val type: String,

    @field:Column(name = "Status", position = 3, editable = false)
    val status: String,

    @field:Column(name = "Qty", mapper = BigDecimalReadTableValueMapper::class, position = 4, editable = false)
    val quantity: java.math.BigDecimal,

    @field:Column(name = "Filled", mapper = BigDecimalReadTableValueMapper::class, position = 5, editable = false)
    val filledQuantity: java.math.BigDecimal,

    @field:Column(name = "Remaining", mapper = BigDecimalReadTableValueMapper::class, position = 6, editable = false)
    val remainingQuantity: java.math.BigDecimal,

    @field:Column(name = "Price", mapper = DollarReadTableValueMapper::class, position = 7, editable = false)
    val price: java.math.BigDecimal,

    @field:Column(name = "Entered", position = 8, editable = false)
    val enteredTime: String,

    @field:Column(name = "Closed", position = 9, editable = false)
    val closedTime: String,
)

fun CachedOrder.toRowViewModel(): OrderRowViewModel = OrderRowViewModel(
    symbol = symbol,
    action = instruction?.name ?: "",
    type = orderType?.name ?: "",
    status = status.name,
    quantity = quantity ?: java.math.BigDecimal.ZERO,
    filledQuantity = filledQuantity ?: java.math.BigDecimal.ZERO,
    remainingQuantity = remainingQuantity ?: java.math.BigDecimal.ZERO,
    price = price ?: java.math.BigDecimal.ZERO,
    enteredTime = enteredTime?.let { DATE_FORMATTER.format(it) } ?: "",
    closedTime = closeTime?.let { DATE_FORMATTER.format(it) } ?: "",
)
