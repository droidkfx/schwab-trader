@file:Suppress("unused")

package com.droidkfx.st.schwab.client

import KInstant
import com.droidkfx.st.util.serialization.KBigDecimal
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonObject

@Serializable
data class AccountNumberHash(
    val accountNumber: String,
    val hashValue: String
)

enum class Session {
    NORMAL, AM, PM, SEAMLESS
}

enum class Duration {
    DAY, GOOD_TILL_CANCEL, FILL_OR_KILL, IMMEDIATE_OR_CANCEL, END_OF_WEEK, END_OF_MONTH, NEXT_END_OF_MONTH, UNKNOWN
}

enum class OrderType {
    MARKET, LIMIT, STOP, STOP_LIMIT, TRAILING_STOP, CABINET, NON_MARKETABLE, MARKET_ON_CLOSE, EXERCISE, TRAILING_STOP_LIMIT, NET_DEBIT, NET_CREDIT, NET_ZERO, LIMIT_ON_CLOSE, UNKNOWN
}

// Note that UNKNOWN is not a valid request type
typealias OrderTypeRequest = OrderType

enum class ComplexOrderStrategyType {
    NONE, COVERED, VERTICAL, BACK_RATIO, CALENDAR, DIAGONAL, STRADDLE, STRANGLE, COLLAR_SYNTHETIC, BUTTERFLY, CONDOR, IRON_CONDOR, VERTICAL_ROLL, COLLAR_WITH_STOCK, DOUBLE_DIAGONAL, UNBALANCED_BUTTERFLY, UNBALANCED_CONDOR, UNBALANCED_IRON_CONDOR, UNBALANCED_VERTICAL_ROLL, MUTUAL_FUND_SWAP, CUSTOM
}

enum class RequestedDestination {
    INET, ECN_ARCA, CBOE, AMEX, PHLX, ISE, BOX, NYSE, NASDAQ, BATS, C2, AUTO
}

typealias StopPriceLinkBasis = PriceLinkBasis

typealias StopPriceLinkType = PriceLinkType

enum class StopType {
    STANDARD, BID, ASK, LAST, MARK
}

enum class PriceLinkBasis {
    MANUAL, BASE, TRIGGER, LAST, BID, ASK, ASK_BID, MARK, AVERAGE
}

enum class PriceLinkType {
    VALUE, PERCENT, TICK
}

enum class TaxLotMethod {
    FIFO, LIFO, HIGH_COST, LOW_COST, AVERAGE_COST, SPECIFIC_LOT, LOSS_HARVESTER
}

enum class SpecialInstruction {
    ALL_OR_NONE, DO_NOT_REDUCE, ALL_OR_NONE_DO_NOT_REDUCE
}

enum class OrderStrategyType {
    SINGLE, CANCEL, RECALL, PAIR, FLATTEN, TWO_DAY_SWAP, BLAST_ALL, OCO, TRIGGER
}

enum class Status {
    AWAITING_PARENT_ORDER, AWAITING_CONDITION, AWAITING_STOP_CONDITION, AWAITING_MANUAL_REVIEW, ACCEPTED, AWAITING_UR_OUT, PENDING_ACTIVATION, QUEUED, WORKING, REJECTED, PENDING_CANCEL, CANCELED, PENDING_REPLACE, REPLACED, FILLED, EXPIRED, NEW, AWAITING_RELEASE_TIME, PENDING_ACKNOWLEDGEMENT, PENDING_RECALL, UNKNOWN
}

enum class AmountIndicator {
    DOLLARS, SHARES, ALL_SHARES, PERCENTAGE, UNKNOWN
}

enum class SettlementInstruction {
    REGULAR, CASH, NEXT_DAY, UNKNOWN
}

@Serializable
class OrderStrategy(
    val accountNumber: String? = null,
    val advancedOrderType: AdvancedOrderType? = null,
    val closeTime: KInstant? = null,
    val enteredTime: KInstant? = null,
    val orderBalance: OrderBalance? = null,
    val orderStrategyType: OrderStrategyType? = null,
    val orderVersion: Int? = null,
    val session: Session? = null,
    val status: ApiOrderStatus? = null,
    val allOrNone: Boolean? = null,
    val discretionary: Boolean? = null,
    val duration: Duration? = null,
    val filledQuantity: KBigDecimal? = null,
    val orderType: OrderType? = null,
    val orderValue: KBigDecimal? = null,
    val price: KBigDecimal? = null,
    val quantity: KBigDecimal? = null,
    val remainingQuantity: KBigDecimal? = null,
    val sellNonMarginableFirst: Boolean? = null,
    val settlementInstruction: SettlementInstruction? = null,
    val strategy: ComplexOrderStrategyType? = null,
    val amountIndicator: AmountIndicator? = null,
    val orderLegs: List<OrderLeg>? = null,
) {
    enum class AdvancedOrderType {
        NONE, OTO, OCO, OTOCO, OT2OCO, OT3OCO, BLAST_ALL, OTA, PAIR
    }
}

@Serializable
data class OrderLeg(
    val askPrice: KBigDecimal? = null,
    val bidPrice: KBigDecimal? = null,
    val lastPrice: KBigDecimal? = null,
    val markPrice: KBigDecimal? = null,
    val projectedCommission: KBigDecimal? = null,
    val quantity: KBigDecimal? = null,
    val finalSymbol: String? = null,
    val legId: Long? = null,
    val assetType: AssetType? = null,
    val instruction: Instruction? = null,
)

@Serializable
data class OrderBalance(
    val orderValue: KBigDecimal? = null,
    val projectedAvailableFund: KBigDecimal? = null,
    val projectedBuyingPower: KBigDecimal? = null,
    val projectedCommission: KBigDecimal? = null,
)

@Serializable
data class OrderValidationResult(
    val alerts: List<OrderValidationDetail>? = null,
    val accepts: List<OrderValidationDetail>? = null,
    val rejects: List<OrderValidationDetail>? = null,
    val reviews: List<OrderValidationDetail>? = null,
    val warns: List<OrderValidationDetail>? = null,
)

@Serializable
data class OrderValidationDetail(
    val validationRuleName: String? = null,
    val message: String? = null,
    val activityMessage: String? = null,
    val originalSeverity: ApiRuleAction? = null,
    val overrideName: String? = null,
    val overrideSeverity: ApiRuleAction? = null,
)

enum class ApiRuleAction {
    ACCEPT, ALERT, REJECT, REVIEW, UNKNOWN
}

@Serializable
data class CommissionAndFee(
    val commission: Commission? = null,
    val fee: Fees? = null,
    val trueCommission: Commission? = null,
)

@Serializable
data class Commission(
    val commissionLegs: List<CommissionLeg>? = null
)

@Serializable
data class CommissionLeg(
    val commissionValues: List<CommissionValue>? = null
)

typealias CommissionValue = FeeValue

@Serializable
data class Fees(
    val feeLegs: List<FeeLeg>? = null
)

@Serializable
data class FeeLeg(
    val feeValues: List<FeeValue>? = null
)

@Serializable
data class FeeValue(
    val value: KBigDecimal? = null,
    val type: FeeType? = null,
)

enum class FeeType {
    COMMISSION, SEC_FEE, STR_FEE, R_FEE, CDSC_FEE, OPT_REG_FEE, ADDITIONAL_FEE, MISCELLANEOUS_FEE, FTT, FUTURES_CLEARING_FEE, FUTURES_DESK_OFFICE_FEE, FUTURES_EXCHANGE_FEE, FUTURES_GLOBEX_FEE, FUTURES_NFA_FEE, FUTURES_PIT_BROKERAGE_FEE, FUTURES_TRANSACTION_FEE, LOW_PROCEEDS_COMMISSION, BASE_CHARGE, GENERAL_CHARGE, GST_FEE, TAF_FEE, INDEX_OPTION_FEE, TEFRA_TAX, STATE_TAX, UNKNOWN
}

@Serializable
data class Account(
    val securitiesAccount: SecuritiesAccount? = null,
)

@Serializable
data class Order(
    val session: Session? = null,
    val duration: Duration? = null,
    val orderType: OrderType? = null,
    val cancelTime: KInstant? = null,
    val complexOrderStrategyType: ComplexOrderStrategyType? = null,
    val quantity: KBigDecimal? = null,
    val filledQuantity: KBigDecimal? = null,
    val remainingQuantity: KBigDecimal? = null,
    val requestedDestination: RequestedDestination? = null,
    val destinationLinkName: String? = null,
    val releaseTime: KInstant? = null,
    val stopPrice: KBigDecimal? = null,
    val stopPriceLinkBasis: StopPriceLinkBasis? = null,
    val stopPriceLinkType: StopPriceLinkType? = null,
    val stopPriceOffset: KBigDecimal? = null,
    val stopType: StopType? = null,
    val priceLinkBasis: PriceLinkBasis? = null,
    val priceLinkType: PriceLinkType? = null,
    val price: KBigDecimal? = null,
    val taxLotMethod: TaxLotMethod? = null,
    val orderLegCollection: List<OrderLegCollection>? = null,
    val activationPrice: KBigDecimal? = null,
    val specialInstruction: SpecialInstruction? = null,
    val orderStrategyType: OrderStrategyType? = null,
    val orderId: Int? = null,
    val cancelable: Boolean? = null,
    val editable: Boolean? = null,
    val status: Status? = null,
    val enteredTime: KInstant? = null,
    val closeTime: KInstant? = null,
    val tag: String? = null,
    val accountNumber: Int? = null,
    val orderActivityCollection: List<OrderActivity>? = null,
    val replacingOrderCollection: List<JsonObject>? = null,
    val childOrderStrategies: List<JsonObject>? = null,
    val statusDescription: String? = null,
)

@Serializable
data class OrderRequest(
    val session: Session? = null,
    val duration: Duration? = null,
    val orderType: OrderTypeRequest? = null,
    val cancelTime: KInstant? = null,
    val complexOrderStrategyType: ComplexOrderStrategyType? = null,
    val quantity: KBigDecimal? = null,
    val filledQuantity: KBigDecimal? = null,
    val remainingQuantity: KBigDecimal? = null,
    val destinationLinkName: String? = null,
    val releaseTime: KInstant? = null,
    val stopPrice: KBigDecimal? = null,
    val stopPriceLinkBasis: StopPriceLinkBasis? = null,
    val stopPriceLinkType: StopPriceLinkType? = null,
    val stopPriceOffset: KBigDecimal? = null,
    val stopType: StopType? = null,
    val priceLinkBasis: PriceLinkBasis? = null,
    val priceLinkType: PriceLinkType? = null,
    val price: KBigDecimal? = null,
    val taxLotMethod: TaxLotMethod? = null,
    val orderLegCollection: List<OrderLegCollection>? = null,
    val activationPrice: KBigDecimal? = null,
    val specialInstruction: SpecialInstruction? = null,
    val orderStrategyType: OrderStrategyType? = null,
    val orderId: Int? = null,
    val cancelable: Boolean? = null,
    val editable: Boolean? = null,
    val status: Status? = null,
    val enteredTime: KInstant? = null,
    val closeTime: KInstant? = null,
    val accountNumber: KInstant? = null,
    val orderActivityCollection: List<OrderActivity>? = null,
    val replacingOrderCollection: List<JsonObject>? = null,
    val childOrderStrategies: List<JsonObject>? = null,
    val statusDescription: String? = null,
)

@Serializable
data class PreviewOrder(
    val orderId: Int? = null,
    val orderStrategy: OrderStrategy? = null,
    val orderValidationResult: OrderValidationResult? = null,
    val commissionAndFee: CommissionAndFee? = null,
)

@Serializable
data class OrderActivity(
    // TODO why is this not documented as such?
    val activityType: Activity? = null,
    val executionType: String? = null,
    val quantity: KBigDecimal? = null,
    val orderRemainingQuantity: KBigDecimal? = null,
    val executionLegs: List<ExecutionLeg>? = null,
) {
    enum class Activity {
        EXECUTION, ORDER_ACTION
    }
}

@Serializable
data class ExecutionLeg(
    val legId: Int? = null,
    val price: KBigDecimal? = null,
    val quantity: KBigDecimal? = null,
    val mismarkedQuantity: KBigDecimal? = null,
    val instrumentId: Int? = null,
    val time: KInstant? = null,
)

@Serializable
data class Position(
    val shortQuantity: KBigDecimal? = null,
    val averagePrice: KBigDecimal? = null,
    val currentDayProfitLoss: KBigDecimal? = null,
    val currentDayProfitLossPercentage: KBigDecimal? = null,
    val longQuantity: KBigDecimal? = null,
    val settledLongQuantity: KBigDecimal? = null,
    val settledShortQuantity: KBigDecimal? = null,
    val agedQuantity: KBigDecimal? = null,
    val instrument: TransactionInstrument? = null,
    val marketValue: KBigDecimal? = null,
    val maintenanceRequirement: KBigDecimal? = null,
    val averageLongPrice: KBigDecimal? = null,
    val averageShortPrice: KBigDecimal? = null,
    val taxLotAverageLongPrice: KBigDecimal? = null,
    val taxLotAverageShortPrice: KBigDecimal? = null,
    val longOpenProfitLoss: KBigDecimal? = null,
    val shortOpenProfitLoss: KBigDecimal? = null,
    val previousSessionLongQuantity: KBigDecimal? = null,
    val previousSessionShortQuantity: KBigDecimal? = null,
    val currentDayCost: KBigDecimal? = null,
) {
    val totalQuantity: KBigDecimal
        get() {
            return (longQuantity ?: KBigDecimal.ZERO) + (shortQuantity ?: KBigDecimal.ZERO)
        }

    val marketPrice: KBigDecimal
        get() {
            return (marketValue ?: KBigDecimal.ZERO) / (totalQuantity)
        }
}

@Serializable
data class ServiceError(
    val message: String? = null,
    val errors: List<String>? = null
)

@Serializable
data class OrderLegCollection(
    // TODO why is this not documented as such?
    val orderLegType: OrderLegType? = null,
    val legId: Int? = null,
    val instrument: TransactionInstrument? = null,
    val instruction: Instruction? = null,
    // TODO why is this not documented as such?
    val positionEffect: PositionEffect? = null,
    val quantity: KBigDecimal? = null,
    // TODO why is this not documented as such?
    val quantityType: QuantityType? = null,
    // TODO why is this not documented as such?
    val divCapGains: DivCapGains? = null,
    val toSymbol: String? = null,
) {
    enum class OrderLegType {
        EQUITY, OPTION, INDEX, MUTUAL_FUND, CASH_EQUIVALENT, FIXED_INCOME, CURRENCY, COLLECTIVE_INVESTMENT
    }

    enum class QuantityType {
        ALL_SHARES, DOLLARS, SHARES
    }

    enum class DivCapGains {
        REINVEST, PAYOUT
    }
}

enum class PositionEffect {
    OPENING, CLOSING, AUTOMATIC, UNKNOWN
}

typealias SecuritiesAccount = SecuritiesAccountBase

@Serializable
sealed class SecuritiesAccountBase {
    //    abstract val type: Type?
    abstract val accountNumber: String?
    abstract val roundTrips: Int?
    abstract val isDayTrader: Boolean?
    abstract val isClosingOnlyRestricted: Boolean?
    abstract val pfcbFlag: Boolean?
    abstract val positions: List<Position>?

    @Serializable
    enum class Type {
        CASH, MARGIN
    }
}

@Serializable
@SerialName("MARGIN")
class MarginAccount(
//    override val type: Type = Type.MARGIN,
    override val accountNumber: String? = null,
    override val roundTrips: Int? = null,
    override val isDayTrader: Boolean? = null,
    override val isClosingOnlyRestricted: Boolean? = null,
    override val pfcbFlag: Boolean? = null,
    override val positions: List<Position>? = null,
    val initialBalances: MarginInitialBalance? = null,
    val currentBalances: MarginBalance? = null,
    val projectedBalances: MarginBalance? = null,
) : SecuritiesAccountBase()

@Serializable
data class MarginInitialBalance(
    val accruedInterest: KBigDecimal? = null,
    val availableFundsNonMarginableTrade: KBigDecimal? = null,
    val bondValue: KBigDecimal? = null,
    val buyingPower: KBigDecimal? = null,
    val cashBalance: KBigDecimal? = null,
    val cashAvailableForTrading: KBigDecimal? = null,
    val cashReceipts: KBigDecimal? = null,
    val dayTradingBuyingPower: KBigDecimal? = null,
    val dayTradingBuyingPowerCall: KBigDecimal? = null,
    val dayTradingEquityCall: KBigDecimal? = null,
    val equity: KBigDecimal? = null,
    val equityPercentage: KBigDecimal? = null,
    val liquidationValue: KBigDecimal? = null,
    val longMarginValue: KBigDecimal? = null,
    val longOptionMarketValue: KBigDecimal? = null,
    val longStockValue: KBigDecimal? = null,
    val maintenanceCall: KBigDecimal? = null,
    val maintenanceRequirement: KBigDecimal? = null,
    val margin: KBigDecimal? = null,
    val marginEquity: KBigDecimal? = null,
    val moneyMarketFund: KBigDecimal? = null,
    val mutualFundValue: KBigDecimal? = null,
    val regTCall: KBigDecimal? = null,
    val shortMarginValue: KBigDecimal? = null,
    val shortOptionMarketValue: KBigDecimal? = null,
    val shortStockValue: KBigDecimal? = null,
    val totalCash: KBigDecimal? = null,
    val isInCall: Boolean? = null,
    val unsettledCash: KBigDecimal? = null,
    val pendingDeposits: KBigDecimal? = null,
    val marginBalance: KBigDecimal? = null,
    val shortBalance: KBigDecimal? = null,
    val accountValue: KBigDecimal? = null,
)

@Serializable
data class MarginBalance(
    val availableFunds: KBigDecimal? = null,
    val availableFundsNonMarginableTrade: KBigDecimal? = null,
    val buyingPower: KBigDecimal? = null,
    val buyingPowerNonMarginableTrade: KBigDecimal? = null,
    val dayTradingBuyingPower: KBigDecimal? = null,
    val dayTradingBuyingPowerCall: KBigDecimal? = null,
    val equity: KBigDecimal? = null,
    val equityPercentage: KBigDecimal? = null,
    val longMarginValue: KBigDecimal? = null,
    val maintenanceCall: KBigDecimal? = null,
    val maintenanceRequirement: KBigDecimal? = null,
    val marginBalance: KBigDecimal? = null,
    val regTCall: KBigDecimal? = null,
    val shortBalance: KBigDecimal? = null,
    val shortMarginValue: KBigDecimal? = null,
    val sma: KBigDecimal? = null,
    val isInCall: Boolean? = null,
    val stockBuyingPower: KBigDecimal? = null,
    val optionBuyingPower: KBigDecimal? = null,
)

@Serializable
@SerialName("CASH")
class CashAccount(
//    override val type: Type = Type.CASH,
    override val accountNumber: String? = null,
    override val roundTrips: Int? = null,
    override val isDayTrader: Boolean? = null,
    override val isClosingOnlyRestricted: Boolean? = null,
    override val pfcbFlag: Boolean? = null,
    override val positions: List<Position>? = null,
    val initialBalances: CashInitialBalance? = null,
    val currentBalances: CashBalance? = null,
    val projectedBalances: CashBalance? = null
) : SecuritiesAccountBase()

@Serializable
data class CashInitialBalance(
    val accruedInterest: KBigDecimal? = null,
    val cashAvailableForTrading: KBigDecimal? = null,
    val cashAvailableForWithdrawal: KBigDecimal? = null,
    val cashBalance: KBigDecimal? = null,
    val bondValue: KBigDecimal? = null,
    val cashReceipts: KBigDecimal? = null,
    val liquidationValue: KBigDecimal? = null,
    val longOptionMarketValue: KBigDecimal? = null,
    val longStockValue: KBigDecimal? = null,
    val moneyMarketFund: KBigDecimal? = null,
    val mutualFundValue: KBigDecimal? = null,
    val shortOptionMarketValue: KBigDecimal? = null,
    val shortStockValue: KBigDecimal? = null,
    val isInCall: KBigDecimal? = null,
    val unsettledCash: KBigDecimal? = null,
    val cashDebitCallValue: KBigDecimal? = null,
    val pendingDeposits: KBigDecimal? = null,
    val accountValue: KBigDecimal? = null,
)

@Serializable
data class CashBalance(
    val cashAvailableForTrading: KBigDecimal? = null,
    val cashAvailableForWithdrawal: KBigDecimal? = null,
    val cashCall: KBigDecimal? = null,
    val longNonMarginableMarketValue: KBigDecimal? = null,
    val totalCash: KBigDecimal? = null,
    val cashDebitCallValue: KBigDecimal? = null,
    val unsettledCash: KBigDecimal? = null,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("assetType")
sealed class TransactionBaseInstrument {
    //    abstract val assetType: AssetType?
    abstract val cusip: String?
    abstract val symbol: String?
    abstract val description: String?
    abstract val instrumentId: Int?
    abstract val netChange: KBigDecimal?
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("assetType")
sealed class AccountsBaseInstrument() {
    //    abstract val assetType: AssetType?
    abstract val cusip: String?
    abstract val symbol: String?
    abstract val description: String?
    abstract val instrumentId: Int?
    abstract val netChange: KBigDecimal?
}

//typealias AccountsInstrument = TransactionBaseInstrument
typealias TransactionInstrument = TransactionBaseInstrument

@Serializable
@SerialName("CASH_EQUIVALENT")
class TransactionCashEquivalent(
//    @Transient
//    override val assetType: AssetType = AssetType.CASH_EQUIVALENT,
    override val cusip: String? = null,
    override val symbol: String? = null,
    override val description: String? = null,
    override val instrumentId: Int? = null,
    override val netChange: KBigDecimal? = null,
    val type: Type? = null,
) : TransactionBaseInstrument() {
    enum class Type {
        SWEEP_VEHICLE, SAVINGS, MONEY_MARKET_FUND, UNKNOWN
    }
}

@Serializable
@SerialName("COLLECTIVE_INVESTMENT")
class CollectiveInvestment(
//    @Transient
//    override val assetType: AssetType = AssetType.COLLECTIVE_INVESTMENT,
    override val cusip: String? = null,
    override val symbol: String? = null,
    override val description: String? = null,
    override val instrumentId: Int? = null,
    override val netChange: KBigDecimal? = null,
    val type: Type? = null,
) : TransactionBaseInstrument() {
    enum class Type {
        UNIT_INVESTMENT_TRUST, EXCHANGE_TRADED_FUND, CLOSED_END_FUND, INDEX, UNITS
    }
}

enum class Instruction {
    BUY, SELL, BUY_TO_COVER, SELL_SHORT, BUY_TO_OPEN, BUY_TO_CLOSE, SELL_TO_OPEN, SELL_TO_CLOSE, EXCHANGE, SELL_SHORT_EXEMPT
}

@Serializable
enum class AssetType {
    EQUITY, MUTUAL_FUND, OPTION, FUTURE, FOREX, INDEX, CASH_EQUIVALENT, FIXED_INCOME, PRODUCT, CURRENCY, COLLECTIVE_INVESTMENT
}

@Serializable
@SerialName("CURRENCY")
class Currency(
//    @Transient
//    override val assetType: AssetType = AssetType.CURRENCY,
    override val cusip: String? = null,
    override val symbol: String? = null,
    override val description: String? = null,
    override val instrumentId: Int? = null,
    override val netChange: KBigDecimal? = null,
) : TransactionBaseInstrument()

@Serializable
@SerialName("EQUITY")
class TransactionEquity(
//    @Transient
//    override val assetType: AssetType = AssetType.EQUITY,
    override val cusip: String? = null,
    override val symbol: String? = null,
    override val description: String? = null,
    override val instrumentId: Int? = null,
    override val netChange: KBigDecimal? = null,
    val type: Type? = null,
) : TransactionBaseInstrument() {
    enum class Type {
        COMMON_STOCK, PREFERRED_STOCK, DEPOSITORY_RECEIPT, PREFERRED_DEPOSITORY_RECEIPT, RESTRICTED_STOCK, COMPONENT_UNIT, RIGHT, WARRANT, CONVERTIBLE_PREFERRED_STOCK, CONVERTIBLE_STOCK, LIMITED_PARTNERSHIP, WHEN_ISSUED, UNKNOWN
    }
}

@Serializable
@SerialName("FIXED_INCOME")
class TransactionFixedIncome(
//    @Transient
//    override val assetType: AssetType = AssetType.FIXED_INCOME,
    override val cusip: String? = null,
    override val symbol: String? = null,
    override val description: String? = null,
    override val instrumentId: Int? = null,
    override val netChange: KBigDecimal? = null,
    val type: Type? = null,
    val maturityDate: String? = null,
    val factor: KBigDecimal? = null,
    val multiplier: KBigDecimal? = null,
    val variableRate: KBigDecimal? = null,
) : TransactionBaseInstrument() {
    enum class Type {
        BOND_UNIT, CERTIFICATE_OF_DEPOSIT, CONVERTIBLE_BOND, COLLATERALIZED_MORTGAGE_OBLIGATION, CORPORATE_BOND, GOVERNMENT_MORTGAGE, GNMA_BONDS, MUNICIPAL_ASSESSMENT_DISTRICT, MUNICIPAL_BOND, OTHER_GOVERNMENT, SHORT_TERM_PAPER, US_TREASURY_BOND, US_TREASURY_BILL, US_TREASURY_NOTE, US_TREASURY_ZERO_COUPON, AGENCY_BOND, WHEN_AS_AND_IF_ISSUED_BOND, ASSET_BACKED_SECURITY, UNKNOWN
    }
}

@Serializable
@SerialName("FOREX")
class Forex(
//    @Transient
//    override val assetType: AssetType = AssetType.FOREX,
    override val cusip: String? = null,
    override val symbol: String? = null,
    override val description: String? = null,
    override val instrumentId: Int? = null,
    override val netChange: KBigDecimal? = null,
    val type: Type? = null,
    val baseCurrency: Currency? = null,
    val counterCurrency: Currency? = null,
) : TransactionBaseInstrument() {
    enum class Type {
        STANDARD, NBBO, UNKNOWN
    }
}

// TODO this is complicated
//@Serializable
//class Future(
//    override val assetType: AssetType = AssetType.FUTURE,
//    override val cusip: String? = null,
//    override val symbol: String? = null,
//    override val description: String? = null,
//    override val instrumentId: Int? = null,
//    override val netChange: KBigDecimal? = null,
//) : TransactionBaseInstrument()
//
//@Serializable
//class Index(
//    override val assetType: AssetType = AssetType.INDEX,
//    override val cusip: String? = null,
//    override val symbol: String? = null,
//    override val description: String? = null,
//    override val instrumentId: Int? = null,
//    override val netChange: KBigDecimal? = null,
//) : TransactionBaseInstrument()

@Serializable
@SerialName("MUTUAL_FUND")
class TransactionMutualFund(
//    @Transient
//    override val assetType: AssetType = AssetType.MUTUAL_FUND,
    override val cusip: String? = null,
    override val symbol: String? = null,
    override val description: String? = null,
    override val instrumentId: Int? = null,
    override val netChange: KBigDecimal? = null,
    val fundFamilyName: String? = null,
    val fundFamilySymbol: String? = null,
    val fundGroup: String? = null,
    val type: Type? = null,
    val exchangeCutoffTime: KInstant? = null,
    val purchaseCutoffTime: KInstant? = null,
    val redemptionCutoffTime: KInstant? = null,
) : TransactionBaseInstrument() {
    enum class Type {
        NOT_APPLICABLE, OPEN_END_NON_TAXABLE, OPEN_END_TAXABLE, NO_LOAD_NON_TAXABLE, NO_LOAD_TAXABLE, UNKNOWN
    }
}

// TODO this is complicated
//@Serializable
//@SerialName("OPTION")
//class TransactionOption(
//    override val assetType: AssetType = AssetType.OPTION,
//    override val cusip: String? = null,
//    override val symbol: String? = null,
//    override val description: String? = null,
//    override val instrumentId: Int? = null,
//    override val netChange: KBigDecimal? = null,
//) : TransactionBaseInstrument()

@Serializable
@SerialName("PRODUCT")
class Product(
//    @Transient
//    override val assetType: AssetType = AssetType.PRODUCT,
    override val cusip: String? = null,
    override val symbol: String? = null,
    override val description: String? = null,
    override val instrumentId: Int? = null,
    override val netChange: KBigDecimal? = null,
    val type: Type? = null,
) : TransactionBaseInstrument() {
    enum class Type {
        TBD, UNKNOWN
    }
}

//@Serializable
//@SerialName("CASH_EQUIVALENT")
//class AccountCashEquivalent(
////    @Transient
////    override val assetType: AssetType = AssetType.CASH_EQUIVALENT,
//    override val cusip: String? = null,
//    override val symbol: String? = null,
//    override val description: String? = null,
//    override val instrumentId: Int? = null,
//    override val netChange: KBigDecimal? = null,
//) : AccountsBaseInstrument()
//
//@Serializable
//@SerialName("EQUITY")
//class AccountEquity(
////    @Transient
////    override val assetType: AssetType = AssetType.EQUITY,
//    override val cusip: String? = null,
//    override val symbol: String? = null,
//    override val description: String? = null,
//    override val instrumentId: Int? = null,
//    override val netChange: KBigDecimal? = null,
//) : AccountsBaseInstrument()
//
//@Serializable
//@SerialName("FIXED_INCOME")
//class AccountFixedIncome(
////    @Transient
////    override val assetType: AssetType = AssetType.FIXED_INCOME,
//    override val cusip: String? = null,
//    override val symbol: String? = null,
//    override val description: String? = null,
//    override val instrumentId: Int? = null,
//    override val netChange: KBigDecimal? = null,
//) : AccountsBaseInstrument()
//
//@Serializable
//@SerialName("MUTUAL_FUND")
//class AccountMutualFund(
////    @Transient
////    override val assetType: AssetType = AssetType.MUTUAL_FUND,
//    override val cusip: String? = null,
//    override val symbol: String? = null,
//    override val description: String? = null,
//    override val instrumentId: Int? = null,
//    override val netChange: KBigDecimal? = null,
//) : AccountsBaseInstrument()

//@Serializable
//class AccountOption(
//    override val assetType: AssetType = AssetType.OPTION,
//    override val cusip: String? = null,
//    override val symbol: String? = null,
//    override val description: String? = null,
//    override val instrumentId: Int? = null,
//    override val netChange: KBigDecimal? = null,
//) : AccountsBaseInstrument()

@Serializable
data class AccountAPIOptionDeliverable(
    val symbol: String? = null,
    val deliverableUnits: KBigDecimal? = null,
    val apiCurrencyType: ApiCurrencyType? = null,
    val assetType: AssetType? = null
) {
    enum class ApiCurrencyType {
        USD, CAD, EUR, JPY
    }
}

@Serializable
data class TransactionAPIOptionDeliverable(
    val rootSymbol: String? = null,
    val strikePercent: Int? = null,
    val deliverableNumber: Int? = null,
    val deliverableUnits: KBigDecimal? = null,
    val deliverable: TransactionInstrument? = null,
    val assetType: AssetType? = null,
)

enum class ApiOrderStatus {
    AWAITING_PARENT_ORDER, AWAITING_CONDITION, AWAITING_STOP_CONDITION, AWAITING_MANUAL_REVIEW, ACCEPTED, AWAITING_UR_OUT, PENDING_ACTIVATION, QUEUED, WORKING, REJECTED, PENDING_CANCEL, CANCELED, PENDING_REPLACE, REPLACED, FILLED, EXPIRED, NEW, AWAITING_RELEASE_TIME, PENDING_ACKNOWLEDGEMENT, PENDING_RECALL, UNKNOWN
}

enum class TransactionType {
    TRADE, RECEIVE_AND_DELIVER, DIVIDEND_OR_INTEREST, ACH_RECEIPT, ACH_DISBURSEMENT, CASH_RECEIPT, CASH_DISBURSEMENT, ELECTRONIC_FUND, WIRE_OUT, WIRE_IN, JOURNAL, MEMORANDUM, MARGIN_CALL, MONEY_MARKET, SMA_ADJUSTMENT
}

@Serializable
data class Transaction(
    val activityId: Int? = null,
    val time: KInstant? = null,
    val user: UserDetails? = null,
    val description: String? = null,
    val accountNumber: String? = null,
    val type: TransactionType? = null,
    val status: TransactionStatus? = null,
    val subAccount: SubAccountType? = null,
    val tradeDate: KInstant? = null,
    val settlementDate: KInstant? = null,
    val positionId: Int? = null,
    val orderId: Int? = null,
    val netAmount: KBigDecimal? = null,
    val activityType: ActivityType? = null,
    val transferItems: List<TransferItem>? = null,
) {
    enum class ActivityType {
        ACTIVITY_CORRECTION, EXECUTION, ORDER_ACTION, TRANSFER, UNKNOWN
    }

    enum class SubAccountType {
        CASH, MARGIN, SHORT, DIV, INCOME, UNKNOWN
    }

    enum class TransactionStatus {
        VALID, INVALID, PENDING, UNKNOWN
    }
}

@Serializable
data class UserDetails(
    val cdDomainId: String? = null,
    val login: String? = null,
    val type: UserType? = null,
    val userId: String? = null,
    val systemUserName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val brokerRepCode: String? = null,
) {
    enum class UserType {
        ADVISOR_USER, BROKER_USER, CLIENT_USER, SYSTEM_USER, UNKNOWN
    }
}

@Serializable
data class TransferItem(
    val instrument: TransactionInstrument? = null,
    val amount: KBigDecimal? = null,
    val cost: KBigDecimal? = null,
    val price: KBigDecimal? = null,
    val feeType: FeeType? = null,
    val positionEffect: PositionEffect? = null,
)

@Serializable
data class UserPreference(
    val accounts: List<UserPreferenceAccount>? = null,
    val streamerInfo: List<StreamerInfo>? = null,
    val offers: List<Offer>? = null,

    )

@Serializable
data class UserPreferenceAccount(
    val accountNumber: String? = null,
    val primaryAccount: Boolean? = null,
    val type: String? = null,
    val nickName: String? = null,
    val accountColor: String? = null,
    val displayAcctId: String? = null,
    val autoPositionEffect: Boolean? = null,

    )

@Serializable
data class StreamerInfo(
    val streamerSocketUrl: String? = null,
    val schwabClientCustomerId: String? = null,
    val schwabClientCorrelId: String? = null,
    val schwabClientChannel: String? = null,
    val schwabClientFunctionId: String? = null,
)

@Serializable
data class Offer(
    val level2Permissions: Boolean? = null,
    val mktDataPermission: String? = null,

    )
