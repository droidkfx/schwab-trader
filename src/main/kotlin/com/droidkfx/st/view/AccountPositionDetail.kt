package com.droidkfx.st.view

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.border.BevelBorder
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

abstract class AccountPositionDetail : JPanel(GridBagLayout()) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        border = CompoundBorder(
            EmptyBorder(0, 5, 5, 5),
            BevelBorder(BevelBorder.LOWERED)
        )

        add(JLabel("Account ID:"), GridBagConstraints().apply {
            gridx = 0
            gridy = 0
        })
        add(JLabel("123456789"), GridBagConstraints().apply {
            gridx = 1
            gridy = 0
        })

        add(JLabel("Account Hash:"), GridBagConstraints().apply {
            gridx = 0
            gridy = 1
        })
        add(JLabel("123456789"), GridBagConstraints().apply {
            gridx = 1
            gridy = 1
        })

        add(JLabel("Position Configuration:"), GridBagConstraints().apply {
            gridx = 0
            gridy = 2
        })

        add(
            JScrollPane(
                JTable(
                    arrayOf(
                        arrayOf("SPY", "35 %"),
                        arrayOf("VTA", "35 %"),
                        arrayOf("VNI", "15 %"),
                        arrayOf("SFY", "15 %")
                    ),
                    arrayOf("Position", "Allocation")
                ).apply {

                }), GridBagConstraints().apply {
                gridy = 3
                gridwidth = 2
                weighty = 1.0
                weightx = 1.0
                fill = GridBagConstraints.BOTH
            })
    }
}