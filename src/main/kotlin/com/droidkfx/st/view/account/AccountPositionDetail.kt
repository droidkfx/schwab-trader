package com.droidkfx.st.view.account

import com.droidkfx.st.position.AccountPosition
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable

abstract class AccountPositionDetail(acctData: AccountPosition) : JPanel(GridBagLayout()) {
    private val logger = KotlinLogging.logger {}

    init {
        logger.trace { "Initializing" }

        addRow("Account Name:", acctData.Account.name, 0)
        addRow("Account ID:", acctData.Account.id.truncateMiddle(), 1)
        addRow("Account Number:", acctData.Account.accountNumber, 2)
        addRow("Account Hash:", acctData.Account.accountNumberHash.truncateMiddle(), 3)

        add(
            JScrollPane(
                JTable(
                    acctData.positionTargets.map {
                        arrayOf(it.symbol, "%.2f %%".format(it.allocationTarget * 100))
                    }.toTypedArray(),
                    arrayOf("Position", "Allocation")
                ).apply {

                }), GridBagConstraints().apply {
                gridy = 5
                gridwidth = 2
                weighty = 1.0
                weightx = 1.0
                fill = GridBagConstraints.BOTH
            })
    }

    private fun addRow(title: String, value: String, row: Int) {
        add(JLabel(title), GridBagConstraints().apply {
            gridx = 0
            gridy = row
            anchor = GridBagConstraints.EAST
            insets = Insets(1, 5, 1, 10)
        })
        add(JLabel(value), GridBagConstraints().apply {
            gridx = 1
            gridy = row
            anchor = GridBagConstraints.WEST
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
            insets = Insets(1, 0, 1, 5)
        })
    }

    private fun String.truncateMiddle(maxLength: Int = 20): String {
        return when {
            length <= maxLength -> this
            maxLength <= 3 -> "..."
            else -> {
                val sideLength = (maxLength - 3) / 2
                take(sideLength) + "..." + takeLast(sideLength)
            }
        }
    }
}