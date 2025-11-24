package com.droidkfx.st.schwab.client

// Minimal, nullable-friendly fixtures for Schwab client models.
// Intentionally keep KInstant fields null to avoid formatter differences.

fun sampleAccountNumberHash() = AccountNumberHash(
    accountNumber = "123456789",
    hashValue = "abcdef123456"
)

fun sampleOrderLeg() = OrderLeg(
    askPrice = "1.23".toBigDecimal(),
    bidPrice = "1.20".toBigDecimal(),
    lastPrice = "1.22".toBigDecimal(),
    markPrice = "1.215".toBigDecimal(),
    projectedCommission = "0.01".toBigDecimal(),
    quantity = "10".toBigDecimal(),
    finalSymbol = "XYZ",
    legId = 1,
    assetType = AssetType.EQUITY,
    instruction = Instruction.BUY
)

fun sampleOrderBalance() = OrderBalance(
    orderValue = "12.34".toBigDecimal(),
    projectedAvailableFund = "1000".toBigDecimal(),
    projectedBuyingPower = "2000".toBigDecimal(),
    projectedCommission = "0.01".toBigDecimal()
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
    filledQuantity = "0".toBigDecimal(),
    orderType = OrderType.MARKET,
    orderValue = "12.34".toBigDecimal(),
    price = "12.34".toBigDecimal(),
    quantity = "10".toBigDecimal(),
    remainingQuantity = "10".toBigDecimal(),
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

fun sampleCommissionValue() = FeeValue(value = "0.01".toBigDecimal(), type = FeeType.COMMISSION)
fun sampleCommissionLeg() = CommissionLeg(commissionValues = listOf(sampleCommissionValue()))
fun sampleCommission() = Commission(commissionLegs = listOf(sampleCommissionLeg()))

fun sampleFeeValue() = FeeValue(value = "0.01".toBigDecimal(), type = FeeType.SEC_FEE)
fun sampleFeeLeg() = FeeLeg(feeValues = listOf(sampleFeeValue()))
fun sampleFees() = Fees(feeLegs = listOf(sampleFeeLeg()))

fun sampleCommissionAndFee() = CommissionAndFee(
    commission = sampleCommission(),
    fee = sampleFees(),
    trueCommission = sampleCommission()
)

fun sampleExecutionLeg() = ExecutionLeg(
    legId = 1,
    price = "12.34".toBigDecimal(),
    quantity = "10".toBigDecimal(),
    mismarkedQuantity = "0".toBigDecimal(),
    instrumentId = 111
)

fun sampleOrderActivity() = OrderActivity(
    activityType = OrderActivity.Activity.EXECUTION,
    executionType = "TRADE",
    quantity = "10".toBigDecimal(),
    orderRemainingQuantity = "0".toBigDecimal(),
    executionLegs = listOf(sampleExecutionLeg())
)

fun sampleOrder() = Order(
    session = Session.NORMAL,
    duration = Duration.DAY,
    orderType = OrderType.MARKET,
    // cancelTime, releaseTime, enteredTime, closeTime null
    complexOrderStrategyType = ComplexOrderStrategyType.NONE,
    quantity = "10".toBigDecimal(),
    filledQuantity = "0".toBigDecimal(),
    remainingQuantity = "10".toBigDecimal(),
    requestedDestination = RequestedDestination.AUTO,
    destinationLinkName = "AUTO",
    stopPrice = null,
    stopPriceLinkBasis = StopPriceLinkBasis.MARK,
    stopPriceLinkType = StopPriceLinkType.VALUE,
    stopPriceOffset = null,
    stopType = StopType.STANDARD,
    priceLinkBasis = PriceLinkBasis.LAST,
    priceLinkType = PriceLinkType.VALUE,
    price = "12.34".toBigDecimal(),
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
    quantity = "10".toBigDecimal(),
    filledQuantity = "0".toBigDecimal(),
    remainingQuantity = "10".toBigDecimal(),
    destinationLinkName = "AUTO",
    stopPrice = null,
    stopPriceLinkBasis = StopPriceLinkBasis.MARK,
    stopPriceLinkType = StopPriceLinkType.VALUE,
    stopPriceOffset = null,
    stopType = StopType.STANDARD,
    priceLinkBasis = PriceLinkBasis.LAST,
    priceLinkType = PriceLinkType.VALUE,
    price = "12.34".toBigDecimal(),
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
    netChange = "0.1".toBigDecimal(),
    type = TransactionEquity.Type.COMMON_STOCK
)

fun samplePosition() = Position(
    shortQuantity = "0".toBigDecimal(),
    averagePrice = "12.34".toBigDecimal(),
    currentDayProfitLoss = "0".toBigDecimal(),
    currentDayProfitLossPercentage = "0".toBigDecimal(),
    longQuantity = "10".toBigDecimal(),
    settledLongQuantity = "10".toBigDecimal(),
    settledShortQuantity = "0".toBigDecimal(),
    agedQuantity = "0".toBigDecimal(),
    instrument = sampleTransactionEquity(),
    marketValue = "123.4".toBigDecimal(),
    maintenanceRequirement = "50".toBigDecimal(),
    averageLongPrice = "12.34".toBigDecimal(),
    averageShortPrice = null,
    taxLotAverageLongPrice = "12.34".toBigDecimal(),
    taxLotAverageShortPrice = null,
    longOpenProfitLoss = "0".toBigDecimal(),
    shortOpenProfitLoss = null,
    previousSessionLongQuantity = "10".toBigDecimal(),
    previousSessionShortQuantity = "0".toBigDecimal(),
    currentDayCost = "0".toBigDecimal()
)

fun sampleMarginInitialBalance() = MarginInitialBalance(
    accruedInterest = "0".toBigDecimal(),
    availableFundsNonMarginableTrade = "0".toBigDecimal(),
    bondValue = "0".toBigDecimal(),
    buyingPower = "0".toBigDecimal(),
    cashBalance = "0".toBigDecimal(),
    cashAvailableForTrading = "0".toBigDecimal(),
    cashReceipts = "0".toBigDecimal(),
    dayTradingBuyingPower = "0".toBigDecimal(),
    dayTradingBuyingPowerCall = "0".toBigDecimal(),
    dayTradingEquityCall = "0".toBigDecimal(),
    equity = "0".toBigDecimal(),
    equityPercentage = "0".toBigDecimal(),
    liquidationValue = "0".toBigDecimal(),
    longMarginValue = "0".toBigDecimal(),
    longOptionMarketValue = "0".toBigDecimal(),
    longStockValue = "0".toBigDecimal(),
    maintenanceCall = "0".toBigDecimal(),
    maintenanceRequirement = "0".toBigDecimal(),
    margin = "0".toBigDecimal(),
    marginEquity = "0".toBigDecimal(),
    moneyMarketFund = "0".toBigDecimal(),
    mutualFundValue = "0".toBigDecimal(),
    regTCall = "0".toBigDecimal(),
    shortMarginValue = "0".toBigDecimal(),
    shortOptionMarketValue = "0".toBigDecimal(),
    shortStockValue = "0".toBigDecimal(),
    totalCash = "0".toBigDecimal(),
    isInCall = false,
    unsettledCash = "0".toBigDecimal(),
    pendingDeposits = "0".toBigDecimal(),
    marginBalance = "0".toBigDecimal(),
    shortBalance = "0".toBigDecimal(),
    accountValue = "0".toBigDecimal()
)

fun sampleMarginBalance() = MarginBalance(
    availableFunds = "0".toBigDecimal(),
    availableFundsNonMarginableTrade = "0".toBigDecimal(),
    buyingPower = "0".toBigDecimal(),
    buyingPowerNonMarginableTrade = "0".toBigDecimal(),
    dayTradingBuyingPower = "0".toBigDecimal(),
    dayTradingBuyingPowerCall = "0".toBigDecimal(),
    equity = "0".toBigDecimal(),
    equityPercentage = "0".toBigDecimal(),
    longMarginValue = "0".toBigDecimal(),
    maintenanceCall = "0".toBigDecimal(),
    maintenanceRequirement = "0".toBigDecimal(),
    marginBalance = "0".toBigDecimal(),
    regTCall = "0".toBigDecimal(),
    shortBalance = "0".toBigDecimal(),
    shortMarginValue = "0".toBigDecimal(),
    sma = "0".toBigDecimal(),
    isInCall = false,
    stockBuyingPower = "0".toBigDecimal(),
    optionBuyingPower = "0".toBigDecimal()
)

fun sampleCashInitialBalance() = CashInitialBalance(
    accruedInterest = "0".toBigDecimal(),
    cashAvailableForTrading = "0".toBigDecimal(),
    cashAvailableForWithdrawal = "0".toBigDecimal(),
    cashBalance = "0".toBigDecimal(),
    bondValue = "0".toBigDecimal(),
    cashReceipts = "0".toBigDecimal(),
    liquidationValue = "0".toBigDecimal(),
    longOptionMarketValue = "0".toBigDecimal(),
    longStockValue = "0".toBigDecimal(),
    moneyMarketFund = "0".toBigDecimal(),
    mutualFundValue = "0".toBigDecimal(),
    shortOptionMarketValue = "0".toBigDecimal(),
    shortStockValue = "0".toBigDecimal(),
    isInCall = false,
    unsettledCash = "0".toBigDecimal(),
    cashDebitCallValue = "0".toBigDecimal(),
    pendingDeposits = "0".toBigDecimal(),
    accountValue = "0".toBigDecimal()
)

fun sampleCashBalance() = CashBalance(
    cashAvailableForTrading = "0".toBigDecimal(),
    cashAvailableForWithdrawal = "0".toBigDecimal(),
    cashCall = "0".toBigDecimal(),
    longNonMarginableMarketValue = "0".toBigDecimal(),
    totalCash = "0".toBigDecimal(),
    cashDebitCallValue = "0".toBigDecimal(),
    unsettledCash = "0".toBigDecimal()
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
    quantity = "10".toBigDecimal(),
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
    netChange = "0.0".toBigDecimal(),
    type = TransactionFixedIncome.Type.CORPORATE_BOND,
    maturityDate = "2030-01-01",
    factor = "1.0".toBigDecimal(),
    multiplier = "1.0".toBigDecimal(),
    variableRate = "0.0".toBigDecimal()
)

fun sampleTransactionCashEquivalent() = TransactionCashEquivalent(
    cusip = "CASH1",
    symbol = "SWVXX",
    description = "CashEq",
    instrumentId = 6,
    netChange = "0.0".toBigDecimal(),
    type = TransactionCashEquivalent.Type.SWEEP_VEHICLE
)

fun sampleCollectiveInvestment() = CollectiveInvestment(
    cusip = "ETF1",
    symbol = "SPY",
    description = "ETF",
    instrumentId = 7,
    netChange = "0.0".toBigDecimal(),
    type = CollectiveInvestment.Type.EXCHANGE_TRADED_FUND
)

fun sampleCurrency() = Currency(
    cusip = "",
    symbol = "USD",
    description = "US Dollar",
    instrumentId = 840,
    netChange = "0.0".toBigDecimal()
)

fun sampleForex() = Forex(
    cusip = null,
    symbol = "EUR/USD",
    description = "Forex",
    instrumentId = 999,
    netChange = "0.0".toBigDecimal(),
    type = Forex.Type.STANDARD,
    baseCurrency = sampleCurrency(),
    counterCurrency = sampleCurrency()
)

fun sampleTransactionMutualFund() = TransactionMutualFund(
    cusip = "MF1",
    symbol = "MFX",
    description = "Mutual Fund",
    instrumentId = 3,
    netChange = "0.0".toBigDecimal(),
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
    netChange = "0.0".toBigDecimal(),
    expirationDate = null,
    optionDeliverables = TransactionAPIOptionDeliverable(
        rootSymbol = "XYZ",
        strikePercent = 100,
        deliverableNumber = 1,
        deliverableUnits = "100".toBigDecimal(),
        deliverable = sampleTransactionEquity(),
        assetType = AssetType.EQUITY
    ),
    optionPremiumMultiplier = 100,
    putCall = TransactionOption.PutCall.CALL,
    strikePrice = "10.0".toBigDecimal(),
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
    netChange = "0.0".toBigDecimal(),
    type = Product.Type.TBD
)

fun sampleAccountAPIOptionDeliverable() = AccountAPIOptionDeliverable(
    symbol = "XYZ",
    deliverableUnits = "100".toBigDecimal(),
    apiCurrencyType = AccountAPIOptionDeliverable.ApiCurrencyType.USD,
    assetType = AssetType.EQUITY
)

fun sampleTransactionAPIOptionDeliverable() = TransactionAPIOptionDeliverable(
    rootSymbol = "XYZ",
    strikePercent = 100,
    deliverableNumber = 1,
    deliverableUnits = "100".toBigDecimal(),
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
    amount = "10".toBigDecimal(),
    cost = "12.34".toBigDecimal(),
    price = "12.34".toBigDecimal(),
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
    netAmount = "12.34".toBigDecimal(),
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
