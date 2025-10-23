package com.droidkfx.st.view

import com.droidkfx.st.controller.AllocationTable
import com.droidkfx.st.view.model.AccountTabViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.FocusListener
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingUtilities

abstract class AccountTab(
    viewModel: AccountTabViewModel,
) : JPanel(BorderLayout()) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        val saveAllocationsButton = JButton("Save Allocations").apply {
            isEnabled = false
            addCoActionListener {
                saveAccountPositions()
                SwingUtilities.invokeLater { isEnabled = false }
            }
            viewModel.data.addSwingListener {
                isEnabled = true
            }
        }

        add(
            JPanel(
                FlowLayout().apply { alignment = FlowLayout.LEFT }).apply {
                add(JTextField(viewModel.accountNameDataBinding.value).apply {
                    // enter key
                    addActionListener {
                        logger.debug { "Account name changed to $text" }
                        viewModel.setAccountName(text)
                    }
                    addFocusListener(
                        object : FocusListener {
                            override fun focusLost(e: java.awt.event.FocusEvent?) {
                                logger.debug { "Account name changed to $text" }
                                viewModel.setAccountName(text)
                            }

                            override fun focusGained(e: java.awt.event.FocusEvent?) {}
                        }
                    )
                })
                add(saveAllocationsButton)
                add(JButton("Refresh Data").apply {
                    addCoActionListener { refreshData() }
                })
                add(JButton("Process Orders").apply {
                    addCoActionListener { processOrders() }
                })
            }, BorderLayout.NORTH
        )
        add(AllocationTable(viewModel.data).apply {
            addTableModelListener {
                saveAllocationsButton.isEnabled = true
            }
        }, BorderLayout.CENTER)
    }

    abstract suspend fun saveAccountPositions()
    abstract suspend fun refreshData()
    abstract suspend fun processOrders()
}