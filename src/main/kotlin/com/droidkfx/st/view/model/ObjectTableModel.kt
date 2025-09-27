package com.droidkfx.st.view.model

import java.lang.reflect.Method
import java.util.Locale.getDefault
import javax.swing.table.AbstractTableModel
import kotlin.reflect.KClass

interface TableValueMapper {
    fun map(value: Any): String
}

open class DefaultTableValueMapper : TableValueMapper {
    override fun map(value: Any): String {
        return value.toString()
    }
}

open class DoubleTableValueMapper() : TableValueMapper {
    private var format = "%.2f"

    constructor(format: String) : this() {
        this.format = format
    }

    override fun map(value: Any): String {
        if (value !is Double) {
            return value.toString()
        }
        return format.format(value)
    }
}

class DollarTableValueMapper : DoubleTableValueMapper() {
    override fun map(value: Any): String {
        return "$ " + super.map(value)
    }
}

class PercentTableValueMapper : DoubleTableValueMapper("%05.2f") {
    override fun map(value: Any): String {
        return super.map(value) + " %"
    }
}

annotation class Column(
    val name: String = "",
    val position: Int = -1,
    val mapper: KClass<out TableValueMapper> = DefaultTableValueMapper::class
)

class ObjectTableModel<T>(
    private val data: List<T>,
    private val typeInfo: Class<T>
) : AbstractTableModel() {

    private val columns = typeInfo.declaredFields
        .mapIndexed { index, field ->
            field.annotations
                .firstOrNull { it is Column }
                ?.let {
                    val col = (it as Column)
                    val index = if (col.position != -1) col.position else index
                    val name = if (col.name != "") col.name else field.name
                    return@mapIndexed ((index to field.name) to (name to createMapper(col.mapper)))
                } ?: ((index to field.name) to (field.name to createMapper(DefaultTableValueMapper::class)))
        }.sortedBy { it.first.first }
        .map { it ->
            ColumnInfo(
                it.second.first,
                it.second.second,
                typeInfo.getMethod("get" + it.first.second.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        getDefault()
                    ) else it.toString()
                })
            )
        }

    private fun createMapper(mapperClass: KClass<out TableValueMapper>): TableValueMapper =
        mapperClass.java.getConstructor().newInstance() as TableValueMapper

    override fun getColumnName(column: Int): String =
        columns.getOrNull(column)?.let {
            return it.name
        } ?: "Unknown Column"

    override fun getRowCount(): Int = data.size
    override fun getColumnCount(): Int = typeInfo.declaredFields.size
    override fun getValueAt(rowIndex: Int, columnIndex: Int): String {
        return columns.getOrNull(columnIndex)?.let {
            val rawValue = it.getter.invoke(data[rowIndex])
            return it.mapper.map(rawValue)
        } ?: ""
    }

    data class ColumnInfo(val name: String, val mapper: TableValueMapper, val getter: Method)
}
