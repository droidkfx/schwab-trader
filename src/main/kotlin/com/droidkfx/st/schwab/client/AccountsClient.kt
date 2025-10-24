package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.KBigDecimal
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import kotlinx.serialization.Serializable
import java.math.BigDecimal

class AccountsClient(
    config: SchwabClientConfig,
    client: HttpClient,
    oathToken: ValueDataBinding<String?> = ValueDataBinding(null),
    requestTokenRefresh: ValueDataBinding<Boolean>,
    oauthTokenStatus: ReadOnlyValueDataBinding<OauthStatus>,
) : BaseClient(config, client, requestTokenRefresh, oathToken, oauthTokenStatus, listOf("trader", "v1")) {
    override val logger: KLogger = logger {}

    fun listAccountNumbers(): ApiResponse<List<AccountNumberResponse>> {
        logger.trace { "listAccountNumbers" }
        return getAt("accounts", "accountNumbers")
    }

    fun getLinkedAccounts(includePositions: Boolean = false): ApiResponse<List<LinkedAccountsResponse>> {
        logger.trace { "getLinkedAccounts" }
        return getAt("accounts") {
            url {
                if (includePositions) {
                    parameters["fields"] = "positions"
                }
            }
        }
    }

    fun getLinkedAccount(accountId: String, includePositions: Boolean = false): ApiResponse<LinkedAccountsResponse> {
        logger.trace { "getLinkedAccount $accountId" }
        return getAt("accounts", accountId) {
            url {
                if (includePositions) {
                    parameters["fields"] = "positions"
                }
            }
        }
    }
}

@Serializable
data class LinkedAccountsResponse(
    val securitiesAccount: LinkedSecuritiesAccount
) {
    @Serializable
    data class LinkedSecuritiesAccount(
        val type: LinkedSecuritiesAccountType = LinkedSecuritiesAccountType.CASH,
        val accountNumber: String,
        val roundTrips: Int = 0,
        val isDayTrader: Boolean = false,
        val isClosingOnlyRestricted: Boolean = false,
        val pfcbFlag: Boolean = false,
        val positions: List<LinkedAccountPosition> = emptyList(),
        val initialBalances: AccountBalance? = null,
        val currentBalances: AccountBalance? = null,
    ) {
        enum class LinkedSecuritiesAccountType {
            CASH, MARGIN
        }

        @Serializable
        data class AccountBalance(
            val accruedInterest: KBigDecimal = KBigDecimal.ZERO,
            val cashBalance: KBigDecimal = KBigDecimal.ZERO,
        )

        @Serializable
        data class LinkedAccountPosition(
            val shortQuantity: KBigDecimal = KBigDecimal.ZERO,
            val averagePrice: KBigDecimal = KBigDecimal.ZERO,
            val currentDayProfitLoss: KBigDecimal = KBigDecimal.ZERO,
            val currentDayProfitLossPercentage: KBigDecimal = KBigDecimal.ZERO,
            val longQuantity: KBigDecimal = KBigDecimal.ZERO,
            val settledLongQuantity: KBigDecimal = KBigDecimal.ZERO,
            val settledShortQuantity: KBigDecimal = KBigDecimal.ZERO,
            val agedQuantity: KBigDecimal = KBigDecimal.ZERO,
            val instrument: AccountPositionInstrument,
            val marketValue: KBigDecimal = KBigDecimal.ZERO,
            val maintenanceRequirement: KBigDecimal = KBigDecimal.ZERO,
            val averageLongPrice: KBigDecimal = KBigDecimal.ZERO,
            val averageShortPrice: KBigDecimal = KBigDecimal.ZERO,
            val taxLotAverageLongPrice: KBigDecimal = KBigDecimal.ZERO,
            val taxLotAverageShortPrice: KBigDecimal = KBigDecimal.ZERO,
            val longOpenProfitLoss: KBigDecimal = KBigDecimal.ZERO,
            val shortOpenProfitLoss: KBigDecimal = KBigDecimal.ZERO,
            val previousSessionLongQuantity: KBigDecimal = KBigDecimal.ZERO,
            val previousSessionShortQuantity: KBigDecimal = KBigDecimal.ZERO,
            val currentDayCost: KBigDecimal = KBigDecimal.ZERO,
        ) {
            val totalQuantity: BigDecimal
                get() = longQuantity + shortQuantity
            val totalSettledQuantity: BigDecimal
                get() = settledLongQuantity + settledShortQuantity


            @Serializable
            data class AccountPositionInstrument(
                val assetType: AccountPositionInstrumentType = AccountPositionInstrumentType.EQUITY,
                val cuisp: String = "",
                val symbol: String = "",
                val description: String = "",
                val instrumentId: Int = 0,
                val netChange: KBigDecimal = KBigDecimal.ZERO,
            ) {
                enum class AccountPositionInstrumentType {
                    EQUITY, OPTION, INDEX, MUTUAL_FUND, CASH_EQUIVALENT, FIXED_INCOME, CURRENCY, COLLECTIVE_INVESTMENT
                }
            }
        }
    }
}

@Serializable
data class AccountNumberResponse(
    val accountNumber: String,
    val hashValue: String,
)
