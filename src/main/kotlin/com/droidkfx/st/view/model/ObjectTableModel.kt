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
    val mapper: KClass<out TableValueMapper> = DefaultTableValueMapper::class,
    val editable: Boolean = false
)

open class ObjectTableModel<T>(
    private val data: List<T>, private val typeInfo: Class<T>
) : AbstractTableModel() {

    protected val columns = typeInfo.declaredFields.filter { it.name != "Companion" }.mapIndexed { index, field ->
        field.annotations.firstOrNull { it is Column }?.let {
                    val col = (it as Column)
                    val index = if (col.position != -1) col.position else index
                    val name = if (col.name != "") col.name else field.name
            field.isAccessible = true
                    return@mapIndexed index to ColumnInfo(
                        name,
                        fetchGetter(field.name),
                        fetchSetter(field.name, field.type),
                        createMapper(col.mapper),
                        col.editable
                    )
                } ?: (index to ColumnInfo(field.name, fetchGetter(field.name)))
    }.sortedBy { it.first }.map { it.second }

    private fun fetchGetter(name: String): Method? {
        return try {
            typeInfo.getMethod("get" + name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    getDefault()
                ) else it.toString()
            })
        } catch (_: Exception) {
            null
        }
    }

    private fun fetchSetter(name: String, type: Class<*>): Method? {
        return try {
            typeInfo.getMethod("set" + name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    getDefault()
                ) else it.toString()
            }, type)
        } catch (_: Exception) {
            null
        }
    }

    override fun getColumnName(column: Int): String = columns.getOrNull(column)?.let {
        return it.name
    } ?: "Unknown Column"

    internal fun isColumnEditable(column: Int): Boolean = columns.getOrNull(column)?.editable ?: false

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = false

    override fun getRowCount(): Int = data.size
    override fun getColumnCount(): Int = typeInfo.declaredFields.size
    override fun getValueAt(rowIndex: Int, columnIndex: Int): String {
        return columns.getOrNull(columnIndex)?.let {
            val rawValue = it.getter?.invoke(data[rowIndex]) ?: ""
            return it.mapper.map(rawValue)
        } ?: ""
    }

    data class ColumnInfo(
        val name: String,
        val getter: Method?,
        val setter: Method? = null,
        val mapper: TableValueMapper = createMapper(DefaultTableValueMapper::class),
        val editable: Boolean = false
    )

    companion object {
        private fun createMapper(mapperClass: KClass<out TableValueMapper>): TableValueMapper =
            mapperClass.java.getConstructor().newInstance() as TableValueMapper
    }
}
