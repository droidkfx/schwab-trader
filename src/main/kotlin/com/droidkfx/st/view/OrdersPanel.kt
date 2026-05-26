package com.droidkfx.st.view

import com.droidkfx.st.view.model.OrderFilter
import com.droidkfx.st.view.model.OrdersViewModel
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel

class OrdersPanel(vm: OrdersViewModel) : JPanel(BorderLayout()) {

    init {
        val ordersTable = OrdersTable(vm.displayOrders)

        val statusDropDown = MultiSelectDropDown(
            items = OrderFilter.StatusFilter.entries,
            labelOf = { it.label },
            initialSelection = vm.filter.value.statusStatuses,
        ) { selected ->
            vm.filter.value = vm.filter.value.copy(statusStatuses = selected)
        }

        val lookbackOptions =
            arrayOf("No limit", "Last 7 days", "Last 14 days", "Last 30 days", "Last 60 days", "Last 90 days")
        val lookbackDays = arrayOf(null, 7, 14, 30, 60, 90)
        val defaultLookbackIndex =
            lookbackDays.indexOfFirst { it == vm.filter.value.lookbackDays }.takeIf { it >= 0 } ?: 3

        val lookbackCombo = JComboBox(lookbackOptions).apply {
            selectedIndex = defaultLookbackIndex
            addActionListener {
                val days = lookbackDays[selectedIndex]
                vm.filter.value = vm.filter.value.copy(lookbackDays = days)
            }
        }

        val dateFieldOptions = arrayOf("Entered date", "Closed date")
        val dateFields = arrayOf(OrderFilter.DateField.ENTERED, OrderFilter.DateField.CLOSE)
        val defaultDateIndex = dateFields.indexOfFirst { it == vm.filter.value.dateField }.takeIf { it >= 0 } ?: 0

        val dateFieldCombo = JComboBox(dateFieldOptions).apply {
            selectedIndex = defaultDateIndex
            addActionListener {
                val field = dateFields[selectedIndex]
                vm.filter.value = vm.filter.value.copy(dateField = field)
            }
        }

        val filterBar = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            add(JLabel("Status:"))
            add(statusDropDown)
            add(JLabel("Lookback:"))
            add(lookbackCombo)
            add(JLabel("Date:"))
            add(dateFieldCombo)
        }

        add(filterBar, BorderLayout.NORTH)
        add(ordersTable, BorderLayout.CENTER)
    }
}
