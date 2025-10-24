package com.droidkfx.st.view.account

import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.view.addCoActionListener
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.math.BigDecimal
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.border.EmptyBorder
import javax.swing.table.AbstractTableModel

private class AccountPositionTableModelRow(
    var symbol: String? = null,
    var allocationTarget: BigDecimal? = null
) {
    fun toPositionTarget() = symbol?.let { symbol ->
        allocationTarget?.let { allocationTarget ->
            PositionTarget(symbol, allocationTarget)
        }
    }
}

private class AccountPositionRowModel(val data: MutableList<AccountPositionTableModelRow>) : AbstractTableModel() {
    init {
        if (data.isEmpty()) {
            data.add(AccountPositionTableModelRow())
        }
    }

    override fun getColumnName(columnIndex: Int): String {
        return when (columnIndex) {
            0 -> "Position"
            1 -> "Allocation"
            else -> ""
        }
    }

    override fun getRowCount(): Int {
        return data.size
    }

    override fun getColumnCount(): Int {
        return 2
    }

    override fun setValueAt(value: Any?, rowIndex: Int, columnIndex: Int) {
        if (value == null) {
            return
        }
        when (columnIndex) {
            0 -> data[rowIndex].symbol = value.toString()
            1 -> data[rowIndex].allocationTarget = BigDecimal(value.toString()) ?: data[rowIndex].allocationTarget
        }
        if (rowIndex == data.size - 1) {
            data.add(AccountPositionTableModelRow())
        }
        fireTableDataChanged()
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): String? {
        return when (columnIndex) {
            0 -> data[rowIndex].symbol
            1 -> data[rowIndex].allocationTarget?.let {
                "%.2f %%".format(it * BigDecimal(100))
            } ?: ""

            else -> null
        }
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true
}

abstract class AccountPositionDetail(acctData: AccountPosition) : JPanel(GridBagLayout()) {
    private val logger = KotlinLogging.logger {}

    init {
        logger.trace { "Initializing" }

        addRow("Account Name:", acctData.account.name, 0)
        addRow("Account ID:", acctData.account.id.truncateMiddle(), 1)
        addRow("Account Number:", acctData.account.accountNumber, 2)
        addRow("Account Hash:", acctData.account.accountNumberHash.truncateMiddle(), 3)

        val tableModel = AccountPositionRowModel(acctData.positionTargets.map {
            AccountPositionTableModelRow(
                it.symbol,
                it.allocationTarget
            )
        }.toMutableList())
        val saveButton = JButton("Save").apply {
            isEnabled = false
            addActionListener {
                logger.trace { "Save button clicked" }
                isEnabled = false
            }
            addCoActionListener {
                save(acctData.account.id, tableModel.data.mapNotNull { it.toPositionTarget() })
            }
        }
        tableModel.apply {
            addTableModelListener {
                saveButton.isEnabled = true
            }
        }

        add(
            JScrollPane(JTable(tableModel)),
            GridBagConstraints().apply {
                gridy = 5
                gridwidth = 2
                weighty = 1.0
                weightx = 1.0
                fill = GridBagConstraints.BOTH
            })

        add(JPanel().apply {
            border = EmptyBorder(5, 0, 5, 0)
            add(saveButton)
        }, GridBagConstraints().apply {
            gridx = 0
            gridy = 6
            gridwidth = 2
            anchor = GridBagConstraints.CENTER
        })
    }

    abstract suspend fun save(accountId: String, newPositionTargets: List<PositionTarget>)

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