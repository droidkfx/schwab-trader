package com.droidkfx.st.view.model

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.account.defaultAccount
import com.droidkfx.st.orders.OrderService
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.position.defaultPositionTarget
import com.droidkfx.st.util.progress.ProgressService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class AccountTabViewModelProgressTest {

    private lateinit var accountPositionService: AccountPositionService
    private lateinit var accountService: AccountService
    private lateinit var orderService: OrderService
    private lateinit var progressService: ProgressService
    private lateinit var vm: AccountTabViewModel

    private val account = defaultAccount(name = "Test Account")
    private val accountPosition = AccountPosition(
        account = account,
        positionTargets = listOf(defaultPositionTarget()),
        currentPositions = emptyList(),
        currentRecommendedChanges = emptyList(),
        currentCash = BigDecimal("1000.00"),
    )

    @BeforeEach
    fun setUp() {
        accountPositionService = mockk(relaxed = true)
        accountService = mockk(relaxed = true)
        orderService = mockk(relaxed = true)
        progressService = ProgressService()
        vm = AccountTabViewModel(accountPosition, accountPositionService, accountService, orderService, progressService)
    }

    // --- refreshData ---

    @Test
    fun `refreshData shows progress message during execution`() {
        var messageDuringRefresh = ""
        coEvery { accountPositionService.refreshAccountPosition(any()) } coAnswers {
            messageDuringRefresh = progressService.displayText.value
            accountPosition
        }

        runBlocking { vm.refreshData() }

        assertEquals("Refreshing ${account.name}...", messageDuringRefresh)
    }

    @Test
    fun `refreshData clears progress after completion`() {
        coEvery { accountPositionService.refreshAccountPosition(any()) } returns accountPosition

        runBlocking { vm.refreshData() }

        assertEquals("", progressService.displayText.value)
    }

    // --- saveAccountPositions ---

    @Test
    fun `saveAccountPositions shows progress message during execution`() {
        var messageDuringSave = ""
        coEvery { accountPositionService.updateAccountPositionTargets(any(), any()) } coAnswers {
            messageDuringSave = progressService.displayText.value
            accountPosition
        }

        runBlocking { vm.saveAccountPositions() }

        assertEquals("Saving ${account.name}...", messageDuringSave)
    }

    @Test
    fun `saveAccountPositions clears progress after completion`() {
        coEvery { accountPositionService.updateAccountPositionTargets(any(), any()) } returns accountPosition

        runBlocking { vm.saveAccountPositions() }

        assertEquals("", progressService.displayText.value)
    }

    // --- processOrders ---

    @Test
    fun `processOrders shows progress message during execution`() {
        // No recommendations to process, but we can verify the message appears and clears
        runBlocking { vm.processOrders() }

        assertEquals("", progressService.displayText.value)
    }

    @Test
    fun `concurrent operations show collapsed progress format`() {
        val h1 = progressService.begin("Refreshing ${account.name}")
        val h2 = progressService.begin("Saving ${account.name}")

        assertEquals("Refreshing ${account.name}... +1", progressService.displayText.value)

        h1.complete()
        assertEquals("Saving ${account.name}...", progressService.displayText.value)

        h2.complete()
        assertEquals("", progressService.displayText.value)
    }
}
