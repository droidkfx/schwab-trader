package com.droidkfx.st.view.account

import com.droidkfx.st.controller.account.AccountPositionDetail
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.mapped
import com.droidkfx.st.view.GoldenRatioSize
import com.droidkfx.st.view.addSwingListener
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.BorderLayout
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.BevelBorder
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

@Suppress("USELESS_CAST") // Cast is required for overload ambiguity
abstract class ManageAccountsDialog(
    data: ReadOnlyValueDataBinding<List<AccountPosition>>,
    selectedAccountName: ValueDataBinding<String?>,
    private val manageAccountList: JComponent,
) : JDialog(null as? Frame, "Manage Accounts", true) {
    private val logger = KotlinLogging.logger {}

    private val detailPane = JPanel(BorderLayout())
    private val detailPanelsByAccountName = data.mapped { list ->
        list.associate { it.Account.name to AccountPositionDetail(it, ::onPositionSave) }
    }

    init {
        logger.trace { "Initializing" }
        minimumSize = GoldenRatioSize(500)

        selectedAccountName.addSwingListener { setAccountByName(it) }
        data.addSwingListener { list ->
            // TODO there is a bug where if we do this we end up in a refresh loop when we update positions
            // However we want to do this in the case of going from no accounts to many in a refresh.
//            selectedAccountName.value = list.firstOrNull()?.Account?.name
        }

        add(JPanel(GridBagLayout()).apply {
            add(manageAccountList, GridBagConstraints().apply {
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
            setAccountByName(selectedAccountName.value)

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
        if (detailPanelsByAccountName.value[name] != null) {
            detailPane.add(detailPanelsByAccountName.value[name]!!, BorderLayout.CENTER)
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

    abstract fun onPositionSave(accountId: String, newPositions: List<PositionTarget>)
}