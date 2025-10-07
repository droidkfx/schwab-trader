package com.droidkfx.st.view.account

import com.droidkfx.st.controller.account.AccountPositionDetail
import com.droidkfx.st.controller.account.ManageAccountList
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.util.databind.DataBinding
import com.droidkfx.st.view.PerfectSize
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.BorderLayout
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.BevelBorder
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

@Suppress("USELESS_CAST") // Cast is required for overload ambiguity
abstract class ManageAccountsDialog(data: List<AccountPosition>) : JDialog(null as? Frame, "Manage Accounts", true) {
    private val logger = KotlinLogging.logger {}

    private val detailPane = JPanel(BorderLayout())
    private val detailPanelsByAccountName = mutableMapOf<String, AccountPositionDetail>()
    private val selectedAccountName = DataBinding<String?>(null)

    init {
        logger.trace { "Initializing" }
        minimumSize = PerfectSize(300)

        val accountNames = data.map { it.Account.name }
        selectedAccountName.value = accountNames.firstOrNull()
        selectedAccountName.addListener { setAccountByName(it) }
        data.forEach {
            detailPanelsByAccountName.getOrPut(it.Account.name) {
                AccountPositionDetail(it)
            }
        }

        add(JPanel(GridBagLayout()).apply {
            add(ManageAccountList(selectedAccountName, accountNames), GridBagConstraints().apply {
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
                add(JLabel("Get started by refreshing your accounts."), BorderLayout.NORTH)
            }, GridBagConstraints().apply {
                gridx = 1
                gridy = 0
                weightx = 3.0
                weighty = 1.0
                fill = GridBagConstraints.BOTH
            })
            setAccountByName(accountNames.firstOrNull())

            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    logger.trace { "Dialog is closing" }
                }
            })
            defaultCloseOperation = HIDE_ON_CLOSE
        })
    }

    fun setAccountByName(name: String?) {
        if (name == null) return
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