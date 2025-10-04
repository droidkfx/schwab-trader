package com.droidkfx.st.view.account

import com.droidkfx.st.account.Account
import com.droidkfx.st.controller.account.AccountPositionDetail
import com.droidkfx.st.controller.account.ManageAccountList
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.view.PerfectSize
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.BorderLayout
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.UUID
import javax.swing.JDialog
import javax.swing.JPanel
import javax.swing.border.BevelBorder
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

@Suppress("USELESS_CAST") // Cast is required for overload ambiguity
abstract class ManageAccountsDialog : JDialog(null as? Frame, "Manage Accounts", true) {
    private val logger = KotlinLogging.logger {}

    private val detailPane = JPanel(BorderLayout())
    private val detailPanelsByAccountName = mutableMapOf<String, AccountPositionDetail>()

    init {
        logger.trace { "Initializing" }
        minimumSize = PerfectSize(300)

        val testData = listOf(
            AccountPosition(
                Account(
                    id = UUID.randomUUID().toString(),
                    name = "Account 1",
                    accountNumber = "123456789",
                    accountNumberHash = "D4541250B586296FCCE5DEA4463AE17F",
                ), positionTargets = listOf(
                    PositionTarget("SPY", 0.15),
                    PositionTarget("FOO", 0.15),
                    PositionTarget("FIZ", 0.15),
                    PositionTarget("BUZZ", 0.15),
                    PositionTarget("DONE", 0.40)
                )
            ), AccountPosition(
                Account(
                    id = UUID.randomUUID().toString(),
                    name = "Account 2",
                    accountNumber = "987654321",
                    accountNumberHash = "AE4F46B5D6406A0A9DDDE0547FAD9FE6",
                ), positionTargets = listOf(
                    PositionTarget("SPY", 0.40),
                    PositionTarget("FOO", 0.40),
                )
            )
        )
        val accountNames = testData.map { it.Account.name }
        testData.forEach {
            detailPanelsByAccountName.getOrPut(it.Account.name) {
                AccountPositionDetail(it)
            }
        }

        add(JPanel(GridBagLayout()).apply {
            add(ManageAccountList(accountNames) { setAccountByName(it) }, GridBagConstraints().apply {
                gridx = 0
                gridy = 0
                weightx = 1.0
                weighty = 1.0
                fill = GridBagConstraints.BOTH
            })

            add(detailPane.apply {
                border = CompoundBorder(
                    EmptyBorder(0, 5, 5, 5),
                    BevelBorder(BevelBorder.LOWERED)
                )
            }, GridBagConstraints().apply {
                gridx = 1
                gridy = 0
                weightx = 3.0
                weighty = 1.0
                fill = GridBagConstraints.BOTH
            })
            setAccountByName(accountNames.first())

            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    logger.trace { "Dialog is closing" }
                }
            })
            defaultCloseOperation = HIDE_ON_CLOSE
        })
    }

    fun setAccountByName(name: String) {
        logger.debug { "Setting account to: $name" }
        detailPane.removeAll()
        if (detailPanelsByAccountName[name] != null) {
            detailPane.add(detailPanelsByAccountName[name]!!, BorderLayout.CENTER)
        } else {
            detailPane.add(JPanel(), BorderLayout.CENTER)
        }
        detailPane.revalidate()
        detailPane.repaint()
    }

    fun showDialog() {
//        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }
}