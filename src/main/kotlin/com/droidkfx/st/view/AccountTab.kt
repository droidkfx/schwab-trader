package com.droidkfx.st.view

import com.droidkfx.st.strategy.StrategyAction
import com.droidkfx.st.util.databind.mapped
import com.droidkfx.st.view.model.AccountTabViewModel
import com.droidkfx.st.view.model.OrdersViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.FocusListener
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.JTextField

class AccountTab(
    accountVm: AccountTabViewModel,
    ordersVm: OrdersViewModel,
) : JPanel(BorderLayout()) {
    private val logger = logger {}

    /** Exposed so AccountTabs can place the tab strip at the JFrame SOUTH level. */
    val dock: BottomDock

    init {
        logger.trace { "Initializing" }
        val saveAllocationsButton = JButton("Save Allocations").apply {
            isEnabled = false
            addCoActionListener {
                accountVm.saveAccountPositions()
                CoroutineScope(Dispatchers.Swing).launch { isEnabled = false }
            }
            accountVm.data.mapped { it.symbol to it.allocationTarget }.addSwingListener {
                isEnabled = true
            }
        }
        val allocationTable = AllocationTable(accountVm.data).apply {
            addTableModelListener {
                saveAllocationsButton.isEnabled = true
            }
        }

        // JSplitPane: AllocationTable (top) + collapsible content area (bottom)
        val splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT)
        splitPane.resizeWeight = 1.0
        splitPane.isContinuousLayout = true
        val defaultDividerSize = splitPane.dividerSize

        dock = BottomDock { visible ->
            if (visible) {
                splitPane.dividerSize = defaultDividerSize
                splitPane.setDividerLocation(0.7)
            } else {
                splitPane.dividerSize = 0
                splitPane.setDividerLocation(1.0)
            }
        }
        dock.register("Orders", OrdersPanel(ordersVm))

        splitPane.topComponent = allocationTable
        splitPane.bottomComponent = dock.contentPanel
        // Start collapsed — zero-size divider so no gap appears above the bottom bar
        splitPane.dividerSize = 0
        splitPane.setDividerLocation(1.0)

        val processOrdersButton = JButton("Process Orders").apply {
            isEnabled = false
            addCoActionListener { accountVm.processOrders() }
            addActionListener { isEnabled = false }
        }

        add(
            JPanel(FlowLayout().apply { alignment = FlowLayout.LEFT }).apply {
                add(JTextField(accountVm.accountNameDataBinding.value).apply {
                    addActionListener {
                        logger.debug { "Account name changed to $text" }
                        accountVm.setAccountName(text)
                    }
                    addFocusListener(object : FocusListener {
                        override fun focusLost(e: java.awt.event.FocusEvent?) {
                            logger.debug { "Account name changed to $text" }
                            accountVm.setAccountName(text)
                        }

                        override fun focusGained(e: java.awt.event.FocusEvent?) {}
                    })
                })
                add(saveAllocationsButton)
                add(JButton("Refresh Data").apply {
                    addCoActionListener {
                        accountVm.refreshData()
                        ordersVm.refresh()
                        CoroutineScope(Dispatchers.Swing).launch {
                            processOrdersButton.isEnabled = accountVm.data.any {
                                it.tradeAction == StrategyAction.BUY.name || it.tradeAction == StrategyAction.SELL.name
                            }
                        }
                    }
                })
                add(processOrdersButton)
                add(JLabel("Account Cash: $ ${"%.2f".format(accountVm.accountCash.value)}").apply {
                    accountVm.accountCash.addSwingListener { text = "Account Cash: $ ${"%.2f".format(it)}" }
                })
            }, BorderLayout.NORTH
        )
        add(splitPane, BorderLayout.CENTER)
    }
}
