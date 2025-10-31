package com.droidkfx.st.schwab.client

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ModelsSerializationTest {

    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = false; explicitNulls = false }

    private fun normalize(s: String): JsonElement = Json.parseToJsonElement(s)

    private inline fun <reified T> assertRoundTrip(value: T) {
        val s1 = json.encodeToString(value)
        val d1 = json.decodeFromString<T>(s1)
        val s2 = json.encodeToString(d1)
        assertEquals(normalize(s1), normalize(s2))
    }

    @Test
    fun accountNumberHash_roundTrip() = assertRoundTrip(sampleAccountNumberHash())

    @Test
    fun orderLeg_roundTrip() = assertRoundTrip(sampleOrderLeg())
    @Test
    fun orderBalance_roundTrip() = assertRoundTrip(sampleOrderBalance())
    @Test
    fun orderStrategy_roundTrip() = assertRoundTrip(sampleOrderStrategy())

    @Test
    fun orderValidationDetail_roundTrip() = assertRoundTrip(sampleOrderValidationDetail())
    @Test
    fun orderValidationResult_roundTrip() = assertRoundTrip(sampleOrderValidationResult())

    @Test
    fun commission_fee_related_roundTrip() {
        assertRoundTrip(sampleCommissionAndFee())
        assertRoundTrip(sampleCommission())
        assertRoundTrip(sampleCommissionLeg())
        assertRoundTrip(sampleCommissionValue())
        assertRoundTrip(sampleFees())
        assertRoundTrip(sampleFeeLeg())
        assertRoundTrip(sampleFeeValue())
    }

    @Test
    fun order_roundTrip() = assertRoundTrip(sampleOrder())
    @Test
    fun orderRequest_roundTrip() = assertRoundTrip(sampleOrderRequest())
    @Test
    fun previewOrder_roundTrip() = assertRoundTrip(samplePreviewOrder())
    @Test
    fun orderActivity_roundTrip() = assertRoundTrip(sampleOrderActivity())
    @Test
    fun executionLeg_roundTrip() = assertRoundTrip(sampleExecutionLeg())

    @Test
    fun position_roundTrip() = assertRoundTrip(samplePosition())
    @Test
    fun serviceError_roundTrip() = assertRoundTrip(sampleServiceError())

    @Test
    fun orderLegCollection_roundTrip() = assertRoundTrip(sampleOrderLegCollection())

    // Securities account sealed hierarchy
    @Test
    fun marginAccount_roundTrip() = assertRoundTrip(sampleMarginAccount())
    @Test
    fun cashAccount_roundTrip() = assertRoundTrip(sampleCashAccount())
    @Test
    fun account_roundTrip() = assertRoundTrip(sampleAccount())

    // Transaction instruments sealed hierarchy
    @Test
    fun currency_roundTrip() = assertRoundTrip(sampleCurrency())
    @Test
    fun transactionEquity_roundTrip() = assertRoundTrip(sampleTransactionEquity())
    @Test
    fun transactionFixedIncome_roundTrip() = assertRoundTrip(sampleTransactionFixedIncome())
    @Test
    fun forex_roundTrip() = assertRoundTrip(sampleForex())
    @Test
    fun transactionMutualFund_roundTrip() = assertRoundTrip(sampleTransactionMutualFund())
    @Test
    fun transactionOption_roundTrip() = assertRoundTrip(sampleTransactionOption())
    @Test
    fun product_roundTrip() = assertRoundTrip(sampleProduct())
    @Test
    fun transactionCashEquivalent_roundTrip() = assertRoundTrip(sampleTransactionCashEquivalent())
    @Test
    fun collectiveInvestment_roundTrip() = assertRoundTrip(sampleCollectiveInvestment())

    // Other models
    @Test
    fun accountApiOptionDeliverable_roundTrip() = assertRoundTrip(sampleAccountAPIOptionDeliverable())
    @Test
    fun transactionApiOptionDeliverable_roundTrip() = assertRoundTrip(sampleTransactionAPIOptionDeliverable())

    @Test
    fun userDetails_roundTrip() = assertRoundTrip(sampleUserDetails())
    @Test
    fun transferItem_roundTrip() = assertRoundTrip(sampleTransferItem())
    @Test
    fun transaction_roundTrip() = assertRoundTrip(sampleTransaction())

    @Test
    fun userPreference_related_roundTrip() {
        assertRoundTrip(sampleUserPreferenceAccount())
        assertRoundTrip(sampleStreamerInfo())
        assertRoundTrip(sampleOffer())
        assertRoundTrip(sampleUserPreference())
    }

    @Test
    fun collections_roundTrip() {
        assertRoundTrip(sampleAccountNumberHashList())
    }
}

// --- Added empty-object round-trip verification tests ---
// For every model whose fields are optional (nullable with defaults), ensure that
// an "empty" JSON object {} can be decoded, then encoded and decoded again
// without altering the normalized JSON structure.
private val emptyJson = Json { encodeDefaults = true; ignoreUnknownKeys = false; explicitNulls = false }
private fun normalizeElement(s: String): JsonElement = Json.parseToJsonElement(s)

private inline fun <reified T> assertEmptyRoundTrip() {
    val empty = emptyJson.decodeFromString<T>("{}")
    val s1 = emptyJson.encodeToString(empty)
    val d1 = emptyJson.decodeFromString<T>(s1)
    val s2 = emptyJson.encodeToString(d1)
    assertEquals(normalizeElement(s1), normalizeElement(s2))
}

class EmptyModelsSerializationTest {

    // Order-related models
    @Test
    fun empty_orderLeg_roundTrip() = assertEmptyRoundTrip<OrderLeg>()
    @Test
    fun empty_orderBalance_roundTrip() = assertEmptyRoundTrip<OrderBalance>()
    @Test
    fun empty_orderStrategy_roundTrip() = assertEmptyRoundTrip<OrderStrategy>()

    @Test
    fun empty_orderValidationDetail_roundTrip() = assertEmptyRoundTrip<OrderValidationDetail>()
    @Test
    fun empty_orderValidationResult_roundTrip() = assertEmptyRoundTrip<OrderValidationResult>()

    @Test
    fun empty_order_roundTrip() = assertEmptyRoundTrip<Order>()
    @Test
    fun empty_orderRequest_roundTrip() = assertEmptyRoundTrip<OrderRequest>()
    @Test
    fun empty_previewOrder_roundTrip() = assertEmptyRoundTrip<PreviewOrder>()
    @Test
    fun empty_orderActivity_roundTrip() = assertEmptyRoundTrip<OrderActivity>()
    @Test
    fun empty_executionLeg_roundTrip() = assertEmptyRoundTrip<ExecutionLeg>()

    // Commission/Fee tree
    @Test
    fun empty_commissionAndFee_roundTrip() = assertEmptyRoundTrip<CommissionAndFee>()
    @Test
    fun empty_commission_roundTrip() = assertEmptyRoundTrip<Commission>()
    @Test
    fun empty_commissionLeg_roundTrip() = assertEmptyRoundTrip<CommissionLeg>()
    @Test
    fun empty_fees_roundTrip() = assertEmptyRoundTrip<Fees>()
    @Test
    fun empty_feeLeg_roundTrip() = assertEmptyRoundTrip<FeeLeg>()
    @Test
    fun empty_feeValue_roundTrip() = assertEmptyRoundTrip<FeeValue>()

    // Positions/accounts
    @Test
    fun empty_position_roundTrip() = assertEmptyRoundTrip<Position>()
    @Test
    fun empty_serviceError_roundTrip() = assertEmptyRoundTrip<ServiceError>()
    @Test
    fun empty_orderLegCollection_roundTrip() = assertEmptyRoundTrip<OrderLegCollection>()

    // Securities account sealed hierarchy
    @Test
    fun empty_marginAccount_roundTrip() = assertEmptyRoundTrip<MarginAccount>()
    @Test
    fun empty_cashAccount_roundTrip() = assertEmptyRoundTrip<CashAccount>()
    @Test
    fun empty_account_roundTrip() = assertEmptyRoundTrip<Account>()

    // Transaction instruments sealed hierarchy
    @Test
    fun empty_currency_roundTrip() = assertEmptyRoundTrip<Currency>()
    @Test
    fun empty_transactionEquity_roundTrip() = assertEmptyRoundTrip<TransactionEquity>()
    @Test
    fun empty_transactionFixedIncome_roundTrip() = assertEmptyRoundTrip<TransactionFixedIncome>()
    @Test
    fun empty_forex_roundTrip() = assertEmptyRoundTrip<Forex>()
    @Test
    fun empty_transactionMutualFund_roundTrip() = assertEmptyRoundTrip<TransactionMutualFund>()
    @Test
    fun empty_transactionOption_roundTrip() = assertEmptyRoundTrip<TransactionOption>()
    @Test
    fun empty_product_roundTrip() = assertEmptyRoundTrip<Product>()
    @Test
    fun empty_transactionCashEquivalent_roundTrip() = assertEmptyRoundTrip<TransactionCashEquivalent>()
    @Test
    fun empty_collectiveInvestment_roundTrip() = assertEmptyRoundTrip<CollectiveInvestment>()

    // Option deliverables
    @Test
    fun empty_accountApiOptionDeliverable_roundTrip() = assertEmptyRoundTrip<AccountAPIOptionDeliverable>()
    @Test
    fun empty_transactionApiOptionDeliverable_roundTrip() = assertEmptyRoundTrip<TransactionAPIOptionDeliverable>()

    // User preference/domain models
    @Test
    fun empty_userDetails_roundTrip() = assertEmptyRoundTrip<UserDetails>()
    @Test
    fun empty_transferItem_roundTrip() = assertEmptyRoundTrip<TransferItem>()
    @Test
    fun empty_transaction_roundTrip() = assertEmptyRoundTrip<Transaction>()

    @Test
    fun empty_userPreferenceAccount_roundTrip() = assertEmptyRoundTrip<UserPreferenceAccount>()
    @Test
    fun empty_streamerInfo_roundTrip() = assertEmptyRoundTrip<StreamerInfo>()
    @Test
    fun empty_offer_roundTrip() = assertEmptyRoundTrip<Offer>()
    @Test
    fun empty_userPreference_roundTrip() = assertEmptyRoundTrip<UserPreference>()
}
