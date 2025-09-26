package com.droidkfx.st.view.model

import java.util.Locale.getDefault
import javax.swing.table.AbstractTableModel

annotation class Column(val name: String = "", val position: Int = -1)

class ObjectTableModel<T>(
    private val data: List<T>,
    private val typeInfo: Class<T>
) : AbstractTableModel() {

    private val columnNames = typeInfo.declaredFields.mapIndexed { index, field ->
        field.annotations.firstOrNull { it is Column }?.let {
            val col = (it as Column)
            val index = if (col.position != -1) col.position else index
            val name = if (col.name != "") col.name else field.name
            return@mapIndexed (name to index)
        } ?: (field.name to index)
    }.sortedBy { it.second }.map { it.first }

    override fun getColumnName(column: Int): String =
        columnNames.getOrNull(column)?.let {
            return it
        } ?: "Unknown Column"

    override fun getRowCount(): Int = data.size
    override fun getColumnCount(): Int = typeInfo.declaredFields.size
    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return typeInfo
            .declaredMethods
            .filter { it.name.startsWith("get") }
            .firstOrNull { it ->
                it.name.endsWith(typeInfo.declaredFields[columnIndex].name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        getDefault()
                    ) else it.toString()
                })
            }
            ?.invoke(data[rowIndex])
            ?.toString()
            ?: ""
    }
}
