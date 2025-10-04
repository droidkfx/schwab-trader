package com.droidkfx.st.view

import com.droidkfx.st.controller.AccountPositionDetail
import com.droidkfx.st.controller.ManageAccountList
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.Dimension
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog
import javax.swing.JPanel

@Suppress("USELESS_CAST") // Cast is required for overload ambiguity
abstract class ManageAccountsDialog : JDialog(null as? Frame, "Manage Accounts", true) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        minimumSize = Dimension(700, 500)
        add(JPanel(GridBagLayout()).apply {
            add(ManageAccountList(), GridBagConstraints().apply {
                gridx = 0
                gridy = 0
                weightx = 1.0
                weighty = 1.0
                fill = GridBagConstraints.BOTH
            })

            add(AccountPositionDetail(), GridBagConstraints().apply {
                gridx = 1
                gridy = 0
                weightx = 3.0
                weighty = 1.0
                fill = GridBagConstraints.BOTH
            })

            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    logger.trace { "Dialog is closing" }
                }
            })
            defaultCloseOperation = HIDE_ON_CLOSE
        })
    }

    fun showDialog() {
//        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }
}