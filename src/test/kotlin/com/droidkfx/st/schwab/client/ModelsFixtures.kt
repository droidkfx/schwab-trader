package com.droidkfx.st.schwab.client

import com.droidkfx.st.util.serialization.KBigDecimal

// Minimal, nullable-friendly fixtures for Schwab client models.
// Intentionally keep KInstant fields null to avoid formatter differences.

private fun bd(n: String): KBigDecimal = n.toBigDecimal()

fun sampleAccountNumberHash() = AccountNumberHash(
    accountNumber = "123456789",
    hashValue = "abcdef123456"
)

fun sampleOrderLeg() = OrderLeg(
    askPrice = bd("1.23"),
    bidPrice = bd("1.20"),
    lastPrice = bd("1.22"),
    markPrice = bd("1.215"),
    projectedCommission = bd("0.01"),
    quantity = bd("10"),
    finalSymbol = "XYZ",
    legId = 1,
    assetType = AssetType.EQUITY,
    instruction = Instruction.BUY
)

fun sampleOrderBalance() = OrderBalance(
    orderValue = bd("12.34"),
    projectedAvailableFund = bd("1000"),
    projectedBuyingPower = bd("2000"),
    projectedCommission = bd("0.01")
)

fun sampleOrderStrategy() = OrderStrategy(
    accountNumber = "123456789",
    advancedOrderType = OrderStrategy.AdvancedOrderType.NONE,
    // times left null on purpose
    orderBalance = sampleOrderBalance(),
    orderStrategyType = OrderStrategyType.SINGLE,
    orderVersion = 1,
    session = Session.NORMAL,
    status = ApiOrderStatus.ACCEPTED,
    allOrNone = false,
    discretionary = false,
    duration = Duration.DAY,
    filledQuantity = bd("0"),
    orderType = OrderType.MARKET,
    orderValue = bd("12.34"),
    price = bd("12.34"),
    quantity = bd("10"),
    remainingQuantity = bd("10"),
    sellNonMarginableFirst = false,
    settlementInstruction = SettlementInstruction.REGULAR,
    strategy = ComplexOrderStrategyType.NONE,
    amountIndicator = AmountIndicator.DOLLARS,
    orderLegs = listOf(sampleOrderLeg())
)

fun sampleOrderValidationDetail() = OrderValidationDetail(
    validationRuleName = "RULE_1",
    message = "ok",
    activityMessage = "info",
    originalSeverity = ApiRuleAction.ACCEPT,
    overrideName = null,
    overrideSeverity = null
)

fun sampleOrderValidationResult() = OrderValidationResult(
    alerts = listOf(sampleOrderValidationDetail()),
    accepts = listOf(sampleOrderValidationDetail()),
    rejects = listOf(sampleOrderValidationDetail()),
    reviews = listOf(sampleOrderValidationDetail()),
    warns = listOf(sampleOrderValidationDetail()),
)

fun sampleCommissionValue() = FeeValue(value = bd("0.01"), type = FeeType.COMMISSION)
fun sampleCommissionLeg() = CommissionLeg(commissionValues = listOf(sampleCommissionValue()))
fun sampleCommission() = Commission(commissionLegs = listOf(sampleCommissionLeg()))

fun sampleFeeValue() = FeeValue(value = bd("0.01"), type = FeeType.SEC_FEE)
fun sampleFeeLeg() = FeeLeg(feeValues = listOf(sampleFeeValue()))
fun sampleFees() = Fees(feeLegs = listOf(sampleFeeLeg()))

fun sampleCommissionAndFee() = CommissionAndFee(
    commission = sampleCommission(),
    fee = sampleFees(),
    trueCommission = sampleCommission()
)

fun sampleExecutionLeg() = ExecutionLeg(
    legId = 1,
    price = bd("12.34"),
    quantity = bd("10"),
    mismarkedQuantity = bd("0"),
    instrumentId = 111
)

fun sampleOrderActivity() = OrderActivity(
    activityType = OrderActivity.Activity.EXECUTION,
    executionType = "TRADE",
    quantity = bd("10"),
    orderRemainingQuantity = bd("0"),
    executionLegs = listOf(sampleExecutionLeg())
)

fun sampleOrder() = Order(
    session = Session.NORMAL,
    duration = Duration.DAY,
    orderType = OrderType.MARKET,
    // cancelTime, releaseTime, enteredTime, closeTime null
    complexOrderStrategyType = ComplexOrderStrategyType.NONE,
    quantity = bd("10"),
    filledQuantity = bd("0"),
    remainingQuantity = bd("10"),
    requestedDestination = RequestedDestination.AUTO,
    destinationLinkName = "AUTO",
    stopPrice = null,
    stopPriceLinkBasis = StopPriceLinkBasis.MARK,
    stopPriceLinkType = StopPriceLinkType.VALUE,
    stopPriceOffset = null,
    stopType = StopType.STANDARD,
    priceLinkBasis = PriceLinkBasis.LAST,
    priceLinkType = PriceLinkType.VALUE,
    price = bd("12.34"),
    taxLotMethod = TaxLotMethod.FIFO,
    orderLegCollection = listOf(sampleOrderLegCollection()),
    activationPrice = null,
    specialInstruction = SpecialInstruction.DO_NOT_REDUCE,
    orderStrategyType = OrderStrategyType.SINGLE,
    orderId = 123,
    cancelable = true,
    editable = true,
    status = Status.ACCEPTED,
    // times null
    tag = "tag",
    accountNumber = 12345,
    orderActivityCollection = listOf(sampleOrderActivity()),
    replacingOrderCollection = emptyList(),
    childOrderStrategies = emptyList(),
    statusDescription = "accepted"
)

fun sampleOrderRequest() = OrderRequest(
    session = Session.NORMAL,
    duration = Duration.DAY,
    orderType = OrderTypeRequest.MARKET,
    // times null
    complexOrderStrategyType = ComplexOrderStrategyType.NONE,
    quantity = bd("10"),
    filledQuantity = bd("0"),
    remainingQuantity = bd("10"),
    destinationLinkName = "AUTO",
    stopPrice = null,
    stopPriceLinkBasis = StopPriceLinkBasis.MARK,
    stopPriceLinkType = StopPriceLinkType.VALUE,
    stopPriceOffset = null,
    stopType = StopType.STANDARD,
    priceLinkBasis = PriceLinkBasis.LAST,
    priceLinkType = PriceLinkType.VALUE,
    price = bd("12.34"),
    taxLotMethod = TaxLotMethod.FIFO,
    orderLegCollection = listOf(sampleOrderLegCollection()),
    activationPrice = null,
    specialInstruction = SpecialInstruction.DO_NOT_REDUCE,
    orderStrategyType = OrderStrategyType.SINGLE,
    orderId = 123,
    cancelable = true,
    editable = true,
    status = Status.ACCEPTED,
    // times null
    accountNumber = null,
    orderActivityCollection = listOf(sampleOrderActivity()),
    replacingOrderCollection = emptyList(),
    childOrderStrategies = emptyList(),
    statusDescription = "accepted"
)

fun samplePreviewOrder() = PreviewOrder(
    orderId = 99,
    orderStrategy = sampleOrderStrategy(),
    orderValidationResult = sampleOrderValidationResult(),
    commissionAndFee = sampleCommissionAndFee()
)

fun sampleTransactionEquity() = TransactionEquity(
    cusip = "123456",
    symbol = "XYZ",
    description = "Equity",
    instrumentId = 1,
    netChange = bd("0.1"),
    type = TransactionEquity.Type.COMMON_STOCK
)

fun samplePosition() = Position(
    shortQuantity = bd("0"),
    averagePrice = bd("12.34"),
    currentDayProfitLoss = bd("0"),
    currentDayProfitLossPercentage = bd("0"),
    longQuantity = bd("10"),
    settledLongQuantity = bd("10"),
    settledShortQuantity = bd("0"),
    agedQuantity = bd("0"),
    instrument = sampleTransactionEquity(),
    marketValue = bd("123.4"),
    maintenanceRequirement = bd("50"),
    averageLongPrice = bd("12.34"),
    averageShortPrice = null,
    taxLotAverageLongPrice = bd("12.34"),
    taxLotAverageShortPrice = null,
    longOpenProfitLoss = bd("0"),
    shortOpenProfitLoss = null,
    previousSessionLongQuantity = bd("10"),
    previousSessionShortQuantity = bd("0"),
    currentDayCost = bd("0")
)

fun sampleMarginInitialBalance() = MarginInitialBalance(
    accruedInterest = bd("0"),
    availableFundsNonMarginableTrade = bd("0"),
    bondValue = bd("0"),
    buyingPower = bd("0"),
    cashBalance = bd("0"),
    cashAvailableForTrading = bd("0"),
    cashReceipts = bd("0"),
    dayTradingBuyingPower = bd("0"),
    dayTradingBuyingPowerCall = bd("0"),
    dayTradingEquityCall = bd("0"),
    equity = bd("0"),
    equityPercentage = bd("0"),
    liquidationValue = bd("0"),
    longMarginValue = bd("0"),
    longOptionMarketValue = bd("0"),
    longStockValue = bd("0"),
    maintenanceCall = bd("0"),
    maintenanceRequirement = bd("0"),
    margin = bd("0"),
    marginEquity = bd("0"),
    moneyMarketFund = bd("0"),
    mutualFundValue = bd("0"),
    regTCall = bd("0"),
    shortMarginValue = bd("0"),
    shortOptionMarketValue = bd("0"),
    shortStockValue = bd("0"),
    totalCash = bd("0"),
    isInCall = false,
    unsettledCash = bd("0"),
    pendingDeposits = bd("0"),
    marginBalance = bd("0"),
    shortBalance = bd("0"),
    accountValue = bd("0")
)

fun sampleMarginBalance() = MarginBalance(
    availableFunds = bd("0"),
    availableFundsNonMarginableTrade = bd("0"),
    buyingPower = bd("0"),
    buyingPowerNonMarginableTrade = bd("0"),
    dayTradingBuyingPower = bd("0"),
    dayTradingBuyingPowerCall = bd("0"),
    equity = bd("0"),
    equityPercentage = bd("0"),
    longMarginValue = bd("0"),
    maintenanceCall = bd("0"),
    maintenanceRequirement = bd("0"),
    marginBalance = bd("0"),
    regTCall = bd("0"),
    shortBalance = bd("0"),
    shortMarginValue = bd("0"),
    sma = bd("0"),
    isInCall = false,
    stockBuyingPower = bd("0"),
    optionBuyingPower = bd("0")
)

fun sampleCashInitialBalance() = CashInitialBalance(
    accruedInterest = bd("0"),
    cashAvailableForTrading = bd("0"),
    cashAvailableForWithdrawal = bd("0"),
    cashBalance = bd("0"),
    bondValue = bd("0"),
    cashReceipts = bd("0"),
    liquidationValue = bd("0"),
    longOptionMarketValue = bd("0"),
    longStockValue = bd("0"),
    moneyMarketFund = bd("0"),
    mutualFundValue = bd("0"),
    shortOptionMarketValue = bd("0"),
    shortStockValue = bd("0"),
    isInCall = bd("0"),
    unsettledCash = bd("0"),
    cashDebitCallValue = bd("0"),
    pendingDeposits = bd("0"),
    accountValue = bd("0")
)

fun sampleCashBalance() = CashBalance(
    cashAvailableForTrading = bd("0"),
    cashAvailableForWithdrawal = bd("0"),
    cashCall = bd("0"),
    longNonMarginableMarketValue = bd("0"),
    totalCash = bd("0"),
    cashDebitCallValue = bd("0"),
    unsettledCash = bd("0")
)

fun sampleMarginAccount() = MarginAccount(
    accountNumber = "24680",
    roundTrips = 0,
    isDayTrader = false,
    isClosingOnlyRestricted = false,
    pfcbFlag = false,
    positions = listOf(samplePosition()),
    initialBalances = sampleMarginInitialBalance(),
    currentBalances = sampleMarginBalance(),
    projectedBalances = sampleMarginBalance()
)

fun sampleCashAccount() = CashAccount(
    accountNumber = "13579",
    roundTrips = 0,
    isDayTrader = false,
    isClosingOnlyRestricted = false,
    pfcbFlag = false,
    positions = listOf(samplePosition()),
    initialBalances = sampleCashInitialBalance(),
    currentBalances = sampleCashBalance(),
    projectedBalances = sampleCashBalance()
)

fun sampleAccount() = Account(securitiesAccount = sampleMarginAccount())

fun sampleOrderLegCollection() = OrderLegCollection(
    orderLegType = OrderLegCollection.OrderLegType.EQUITY,
    legId = 1,
    instrument = sampleTransactionEquity(),
    instruction = Instruction.BUY,
    positionEffect = PositionEffect.OPENING,
    quantity = bd("10"),
    quantityType = OrderLegCollection.QuantityType.SHARES,
    divCapGains = OrderLegCollection.DivCapGains.REINVEST,
    toSymbol = null
)

fun sampleServiceError() = ServiceError(message = "error", errors = listOf("e1", "e2"))

fun sampleTransactionFixedIncome() = TransactionFixedIncome(
    cusip = "654321",
    symbol = "BND",
    description = "Bond",
    instrumentId = 2,
    netChange = bd("0.0"),
    type = TransactionFixedIncome.Type.CORPORATE_BOND,
    maturityDate = "2030-01-01",
    factor = bd("1.0"),
    multiplier = bd("1.0"),
    variableRate = bd("0.0")
)

fun sampleTransactionCashEquivalent() = TransactionCashEquivalent(
    cusip = "CASH1",
    symbol = "SWVXX",
    description = "CashEq",
    instrumentId = 6,
    netChange = bd("0.0"),
    type = TransactionCashEquivalent.Type.SWEEP_VEHICLE
)

fun sampleCollectiveInvestment() = CollectiveInvestment(
    cusip = "ETF1",
    symbol = "SPY",
    description = "ETF",
    instrumentId = 7,
    netChange = bd("0.0"),
    type = CollectiveInvestment.Type.EXCHANGE_TRADED_FUND
)

fun sampleCurrency() = Currency(
    cusip = "",
    symbol = "USD",
    description = "US Dollar",
    instrumentId = 840,
    netChange = bd("0.0")
)

fun sampleForex() = Forex(
    cusip = null,
    symbol = "EUR/USD",
    description = "Forex",
    instrumentId = 999,
    netChange = bd("0.0"),
    type = Forex.Type.STANDARD,
    baseCurrency = sampleCurrency(),
    counterCurrency = sampleCurrency()
)

fun sampleTransactionMutualFund() = TransactionMutualFund(
    cusip = "MF1",
    symbol = "MFX",
    description = "Mutual Fund",
    instrumentId = 3,
    netChange = bd("0.0"),
    fundFamilyName = "Family",
    fundFamilySymbol = "FF",
    fundGroup = "Group",
    type = TransactionMutualFund.Type.OPEN_END_TAXABLE,
    exchangeCutoffTime = null,
    purchaseCutoffTime = null,
    redemptionCutoffTime = null
)

fun sampleTransactionOption() = TransactionOption(
    cusip = "OPT1",
    symbol = "XYZ  240118C00100000",
    description = "Option",
    instrumentId = 4,
    netChange = bd("0.0"),
    expirationDate = null,
    optionDeliverables = TransactionAPIOptionDeliverable(
        rootSymbol = "XYZ",
        strikePercent = 100,
        deliverableNumber = 1,
        deliverableUnits = bd("100"),
        deliverable = sampleTransactionEquity(),
        assetType = AssetType.EQUITY
    ),
    optionPremiumMultiplier = 100,
    putCall = TransactionOption.PutCall.CALL,
    strikePrice = bd("10.0"),
    type = TransactionOption.Type.VANILLA,
    underlyingSymbol = "XYZ",
    underlyingCusip = "123456",
    deliverable = sampleTransactionEquity()
)

fun sampleProduct() = Product(
    cusip = null,
    symbol = "PRD",
    description = "Product",
    instrumentId = 5,
    netChange = bd("0.0"),
    type = Product.Type.TBD
)

fun sampleAccountAPIOptionDeliverable() = AccountAPIOptionDeliverable(
    symbol = "XYZ",
    deliverableUnits = bd("100"),
    apiCurrencyType = AccountAPIOptionDeliverable.ApiCurrencyType.USD,
    assetType = AssetType.EQUITY
)

fun sampleTransactionAPIOptionDeliverable() = TransactionAPIOptionDeliverable(
    rootSymbol = "XYZ",
    strikePercent = 100,
    deliverableNumber = 1,
    deliverableUnits = bd("100"),
    deliverable = sampleTransactionEquity(),
    assetType = AssetType.EQUITY
)

fun sampleUserDetails() = UserDetails(
    cdDomainId = "dom",
    login = "user",
    type = UserDetails.UserType.CLIENT_USER,
    userId = "u1",
    systemUserName = "sys",
    firstName = "First",
    lastName = "Last",
    brokerRepCode = "BR"
)

fun sampleTransferItem() = TransferItem(
    instrument = sampleTransactionEquity(),
    amount = bd("10"),
    cost = bd("12.34"),
    price = bd("12.34"),
    feeType = FeeType.COMMISSION,
    positionEffect = PositionEffect.OPENING
)

fun sampleTransaction() = Transaction(
    activityId = 1,
    time = null,
    user = sampleUserDetails(),
    description = "desc",
    accountNumber = "123456789",
    type = TransactionType.TRADE,
    status = Transaction.TransactionStatus.VALID,
    subAccount = Transaction.SubAccountType.CASH,
    tradeDate = null,
    settlementDate = null,
    positionId = 1,
    orderId = 123,
    netAmount = bd("12.34"),
    activityType = Transaction.ActivityType.EXECUTION,
    transferItems = listOf(sampleTransferItem())
)

fun sampleUserPreferenceAccount() = UserPreferenceAccount(
    accountNumber = "123456789",
    primaryAccount = true,
    type = "type",
    nickName = "nick",
    accountColor = "#ffffff",
    displayAcctId = "id",
    autoPositionEffect = false
)

fun sampleStreamerInfo() = StreamerInfo(
    streamerSocketUrl = "wss://example",
    schwabClientCustomerId = "cid",
    schwabClientCorrelId = "corr",
    schwabClientChannel = "ch",
    schwabClientFunctionId = "fn"
)

fun sampleOffer() = Offer(
    level2Permissions = true,
    mktDataPermission = "perm"
)

fun sampleUserPreference() = UserPreference(
    accounts = listOf(sampleUserPreferenceAccount()),
    streamerInfo = listOf(sampleStreamerInfo()),
    offers = listOf(sampleOffer())
)

fun sampleAccountNumberHashList() = listOf(sampleAccountNumberHash())
