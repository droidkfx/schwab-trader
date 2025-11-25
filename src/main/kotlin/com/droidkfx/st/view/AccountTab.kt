package com.droidkfx.st.view

import com.droidkfx.st.strategy.StrategyAction
import com.droidkfx.st.util.databind.mapped
import com.droidkfx.st.view.model.AccountTabViewModel
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
import javax.swing.JTextField

interface AccountTabController {
    val viewModel: AccountTabViewModel
    suspend fun saveAccountPositions()
    suspend fun refreshData()
    suspend fun processOrders()
}

class AccountTab(
    c: AccountTabController
) : JPanel(BorderLayout()) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        val saveAllocationsButton = JButton("Save Allocations").apply {
            isEnabled = false
            addCoActionListener {
                c.saveAccountPositions()
                CoroutineScope(Dispatchers.Swing).launch { isEnabled = false }
            }
            c.viewModel.data.mapped { it.symbol to it.allocationTarget }.addSwingListener {
                isEnabled = true
            }
        }
        val allocationTable = AllocationTable(com.droidkfx.st.controller.AllocationTable(c.viewModel.data)).apply {
            addTableModelListener {
                saveAllocationsButton.isEnabled = true
            }
        }

        add(
            JPanel(
                FlowLayout().apply { alignment = FlowLayout.LEFT }).apply {
                add(JTextField(c.viewModel.accountNameDataBinding.value).apply {
                    // enter key
                    addActionListener {
                        logger.debug { "Account name changed to $text" }
                        c.viewModel.setAccountName(text)
                    }
                    addFocusListener(
                        object : FocusListener {
                            override fun focusLost(e: java.awt.event.FocusEvent?) {
                                logger.debug { "Account name changed to $text" }
                                c.viewModel.setAccountName(text)
                            }

                            override fun focusGained(e: java.awt.event.FocusEvent?) {}
                        }
                    )
                })
                add(saveAllocationsButton)
                val processOrdersButton = JButton("Process Orders").apply {
                    isEnabled = false
                    addCoActionListener {
                        c.processOrders()
                    }
                    addActionListener { isEnabled = false }
                }
                add(JButton("Refresh Data").apply {
                    addCoActionListener {
                        c.refreshData()
                        CoroutineScope(Dispatchers.Swing).launch {
                            processOrdersButton.isEnabled = c.viewModel.data.any {
                                it.tradeAction == StrategyAction.BUY.name || it.tradeAction == StrategyAction.SELL.name
                            }
//                            allocationTable.notifyDataChanged()
                        }
                    }
                })
                add(processOrdersButton)
                add(JLabel("Account Cash: $ ${"%.2f".format(c.viewModel.accountCash.value)}").apply {
                    c.viewModel.accountCash.addSwingListener { text = "Account Cash: $ ${"%.2f".format(it)}" }
                })
            }, BorderLayout.NORTH
        )
        add(allocationTable, BorderLayout.CENTER)
    }
}