package com.droidkfx.st.view

import com.droidkfx.st.controller.AllocationTable
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.view.model.AllocationRowViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.SwingUtilities

abstract class AccountTab(
    allocations: ReadWriteListDataBinding<AllocationRowViewModel>,
) : JPanel(BorderLayout()) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        add(
            JPanel(
                FlowLayout().apply { alignment = FlowLayout.LEFT }).apply {
                add(JButton("Save Allocations").apply {
                    isEnabled = false
                    addCoActionListener {
                        saveAccountPositions()
                        SwingUtilities.invokeLater { isEnabled = false }
                    }
                    allocations.addSwingListener {
                        isEnabled = true
                    }
                })
            }, BorderLayout.NORTH
        )
        add(AllocationTable(allocations), BorderLayout.CENTER)
    }

    abstract suspend fun saveAccountPositions()
}