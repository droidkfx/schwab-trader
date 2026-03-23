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

class AccountTab(
    vm: AccountTabViewModel
) : JPanel(BorderLayout()) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        val saveAllocationsButton = JButton("Save Allocations").apply {
            isEnabled = false
            addCoActionListener {
                vm.saveAccountPositions()
                CoroutineScope(Dispatchers.Swing).launch { isEnabled = false }
            }
            vm.data.mapped { it.symbol to it.allocationTarget }.addSwingListener {
                isEnabled = true
            }
        }
        val allocationTable = AllocationTable(vm.data).apply {
            addTableModelListener {
                saveAllocationsButton.isEnabled = true
            }
        }

        add(
            JPanel(FlowLayout().apply { alignment = FlowLayout.LEFT }).apply {
                add(JTextField(vm.accountNameDataBinding.value).apply {
                    addActionListener {
                        logger.debug { "Account name changed to $text" }
                        vm.setAccountName(text)
                    }
                    addFocusListener(object : FocusListener {
                        override fun focusLost(e: java.awt.event.FocusEvent?) {
                            logger.debug { "Account name changed to $text" }
                            vm.setAccountName(text)
                        }

                        override fun focusGained(e: java.awt.event.FocusEvent?) {}
                    })
                })
                add(saveAllocationsButton)
                val processOrdersButton = JButton("Process Orders").apply {
                    isEnabled = false
                    addCoActionListener { vm.processOrders() }
                    addActionListener { isEnabled = false }
                }
                add(JButton("Refresh Data").apply {
                    addCoActionListener {
                        vm.refreshData()
                        CoroutineScope(Dispatchers.Swing).launch {
                            processOrdersButton.isEnabled = vm.data.any {
                                it.tradeAction == StrategyAction.BUY.name || it.tradeAction == StrategyAction.SELL.name
                            }
                        }
                    }
                })
                add(processOrdersButton)
                add(JLabel("Account Cash: $ ${"%.2f".format(vm.accountCash.value)}").apply {
                    vm.accountCash.addSwingListener { text = "Account Cash: $ ${"%.2f".format(it)}" }
                })
            }, BorderLayout.NORTH
        )
        add(allocationTable, BorderLayout.CENTER)
    }
}
