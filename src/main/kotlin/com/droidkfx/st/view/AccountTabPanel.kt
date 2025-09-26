package com.droidkfx.st.view

import com.droidkfx.st.databind.ReadOnlyDataBinding
import com.droidkfx.st.view.model.AccountTabViewModel
import com.droidkfx.st.view.model.AllocationRowViewModel
import com.droidkfx.st.view.model.ObjectTableModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTabbedPane
import javax.swing.JTable

abstract class AccountTabPanel(accountTabs: ReadOnlyDataBinding<List<AccountTabViewModel>?>) : JTabbedPane() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        accountTabs.value
            ?.forEach {
                addTab(it.title, JTabbedPane().apply {
                    addTab("Allocation", AllocationTab(it.data))
                    addTab("Positions", PositionsTab())
                })
            }
            ?: run {
                addTab("Getting Started", JPanel().apply {
                    layout = GridBagLayout()
                    add(JButton("Click here to start balancing an account"))
                })
            }
    }


    private class PositionsTab : JPanel(GridBagLayout()) {
        private val logger = logger {}

        init {
            logger.trace { "Initializing" }
        }
    }

    private class AllocationTab(data: List<AllocationRowViewModel>) : JScrollPane() {
        private val logger = logger {}

        init {
            logger.trace { "Initializing" }
            setViewportView(
                JTable(
                    ObjectTableModel<AllocationRowViewModel>(
                        data = data,
                        AllocationRowViewModel::class.java
                    )
                )
            )
        }
    }
}