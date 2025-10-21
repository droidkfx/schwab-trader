package com.droidkfx.st.view

import com.droidkfx.st.view.model.AllocationRowViewModel
import com.droidkfx.st.view.model.ObjectTableModel
import io.github.oshai.kotlinlogging.KotlinLogging
import javax.swing.JScrollPane
import javax.swing.JTable

abstract class AllocationTable(data: List<AllocationRowViewModel>) : JScrollPane() {
    private val logger = KotlinLogging.logger {}

    init {
        logger.trace { "Initializing" }
        setViewportView(
            JTable(
                ObjectTableModel<AllocationRowViewModel>(
                    data = data, AllocationRowViewModel::class.java
                )
            ).apply {
                autoCreateRowSorter = true
            })
    }
}