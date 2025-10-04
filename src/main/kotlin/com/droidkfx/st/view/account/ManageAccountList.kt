package com.droidkfx.st.view.account

import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.BorderLayout
import java.util.Vector
import javax.swing.JButton
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

abstract class ManageAccountList : JPanel(BorderLayout()) {
    private val logger = KotlinLogging.logger {}

    init {
        logger.trace { "Initializing" }

        add(JList(Vector(listOf("Account 1", "Account 2"))), BorderLayout.CENTER)
        add(JPanel().apply {
            add(JButton("Refresh"))
            border = EmptyBorder(5, 5, 5, 5)
        }, BorderLayout.SOUTH)
    }
}