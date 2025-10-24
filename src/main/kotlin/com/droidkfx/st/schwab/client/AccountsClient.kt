package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import kotlinx.serialization.Serializable

class AccountsClient(
    config: SchwabClientConfig,
    client: HttpClient,
    oathToken: ValueDataBinding<String?> = ValueDataBinding(null),
    requestTokenRefresh: ValueDataBinding<Boolean>,
) : BaseClient(config, client, requestTokenRefresh, oathToken, listOf("trader", "v1")) {
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
            val accruedInterest: Double = 0.0,
            val cashBalance: Double = 0.0,
        )

        @Serializable
        data class LinkedAccountPosition(
            val shortQuantity: Double = 0.0,
            val averagePrice: Double = 0.0,
            val currentDayProfitLoss: Double = 0.0,
            val currentDayProfitLossPercentage: Double = 0.0,
            val longQuantity: Double = 0.0,
            val settledLongQuantity: Double = 0.0,
            val settledShortQuantity: Double = 0.0,
            val agedQuantity: Double = 0.0,
            val instrument: AccountPositionInstrument,
            val marketValue: Double = 0.0,
            val maintenanceRequirement: Double = 0.0,
            val averageLongPrice: Double = 0.0,
            val averageShortPrice: Double = 0.0,
            val taxLotAverageLongPrice: Double = 0.0,
            val taxLotAverageShortPrice: Double = 0.0,
            val longOpenProfitLoss: Double = 0.0,
            val shortOpenProfitLoss: Double = 0.0,
            val previousSessionLongQuantity: Double = 0.0,
            val previousSessionShortQuantity: Double = 0.0,
            val currentDayCost: Double = 0.0,
        ) {
            val totalQuantity: Double = longQuantity + shortQuantity
            val totalSettledQuantity: Double = settledLongQuantity + settledShortQuantity


            @Serializable
            data class AccountPositionInstrument(
                val assetType: AccountPositionInstrumentType = AccountPositionInstrumentType.EQUITY,
                val cuisp: String = "",
                val symbol: String = "",
                val description: String = "",
                val instrumentId: Int = 0,
                val netChange: Double = 0.0,
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
