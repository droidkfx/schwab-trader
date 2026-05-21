package com.droidkfx.st.orders

import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import com.droidkfx.st.util.repository.FileRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class OrderRepository(rootPath: ReadOnlyValueDataBinding<String>) : FileRepository(
    logger {},
    rootPath.readOnlyMapped { "$it/orders" }
) {
    fun loadOrders(accountId: String): List<CachedOrder> {
        logger.trace { "loadOrders for account: $accountId" }
        return load<List<CachedOrder>>(accountId) ?: emptyList()
    }

    fun saveOrders(accountId: String, orders: List<CachedOrder>) {
        logger.trace { "saveOrders for account: $accountId count=${orders.size}" }
        save(accountId, orders)
    }

    fun clear(accountId: String) {
        logger.trace { "clear for account: $accountId" }
        delete(accountId)
    }
}
