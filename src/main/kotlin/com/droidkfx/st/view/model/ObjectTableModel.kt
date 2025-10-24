package com.droidkfx.st.view.model

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.math.BigDecimal
import java.util.Locale.getDefault
import javax.swing.table.AbstractTableModel
import kotlin.reflect.KClass

interface ReadTableValueMapper {
    fun mapOut(value: Any): String
}

interface ReadWriteTableValueMapper : ReadTableValueMapper {
    fun mapIn(value: String): Any
}

open class DefaultReadTableValueMapper : ReadWriteTableValueMapper {
    override fun mapOut(value: Any): String {
        return value.toString()
    }

    override fun mapIn(value: String): String {
        return value
    }
}

open class BigDecimalReadTableValueMapper(private val format: String = "%.2f") : ReadWriteTableValueMapper {
    override fun mapOut(value: Any): String {
        if (value !is BigDecimal) {
            return ""
        }
        if (value == BigDecimal.ZERO) {
            return "-"
        }
        return format.format(value)
    }

    override fun mapIn(value: String): BigDecimal {
        return BigDecimal(value.toDoubleOrNull() ?: 0.0)
    }
}

class DollarReadTableValueMapper : BigDecimalReadTableValueMapper() {
    override fun mapOut(value: Any): String {
        return "$ " + super.mapOut(value)
    }

    override fun mapIn(value: String): BigDecimal {
        return super.mapIn(value.replace("$ ", ""))
    }
}

class PercentReadTableValueMapper : BigDecimalReadTableValueMapper("%05.2f") {
    override fun mapOut(value: Any): String {
        return super.mapOut(value) + " %"
    }

    override fun mapIn(value: String): BigDecimal {
        return super.mapIn(value.replace(" %", ""))
    }
}

annotation class Column(
    val name: String = "",
    val position: Int = -1,
    val mapper: KClass<out ReadTableValueMapper> = DefaultReadTableValueMapper::class,
    val editable: Boolean = true
)

open class ObjectTableModel<T>(
    private val data: List<T>, private val typeInfo: Class<T>
) : AbstractTableModel() {

    protected val columns = typeInfo.declaredFields
        .filter { it.name != "Companion" }
        .mapIndexed { index, field ->
            field.annotations.firstOrNull { it is Column }
                ?.let {
                    val col = (it as Column)
                    val index = if (col.position != -1) col.position else index
                    val name = if (col.name != "") col.name else field.name
                    return@mapIndexed index to ColumnInfo(
                        name,
                        fetchGetter(field.name),
                        fetchSetter(field.name, field.type),
                        createMapper(col.mapper),
                        col.editable
                    )
                } ?: (index to ColumnInfo(field.name, fetchGetter(field.name)))
        }.toMutableList()
        .apply {
            addAll(
                typeInfo.declaredMethods
                    .filter { method -> method.annotations.any { it is Column } }
                    .filter { it.name.startsWith("get") }
                    .filter { Modifier.isPublic(it.modifiers) }
                    .mapIndexed { index, method ->
                        method.annotations.firstOrNull { it is Column }
                            ?.let {
                                val col = (it as Column)
                                val index = if (col.position != -1) col.position else index
                                val name = if (col.name != "") col.name else method.name.replace("get", "")
                                return@mapIndexed index to ColumnInfo(
                                    name,
                                    method,
                                    null,
                                    createMapper(col.mapper),
                                    false
                                )
                            } ?: (index to ColumnInfo(method.name.replace("get", ""), method))
                    }
            )
        }.sortedBy { it.first }
        .map { it.second }

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

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return isColumnEditable(columnIndex) && data.size > rowIndex && rowIndex >= 0
    }

    protected fun setValueOn(obj: T, index: Int, newValue: Any?) {
        val mapper = (columns[index].mapper as? ReadWriteTableValueMapper) ?: return
        if (newValue !is String) return
        val newValue = mapper.mapIn(newValue)
        columns.getOrNull(index)?.setter?.invoke(obj, newValue)
    }

    override fun setValueAt(value: Any?, rowIndex: Int, columnIndex: Int) {
        if (rowIndex >= data.size) {
            return
        }
        setValueOn(data[rowIndex], columnIndex, value)
        super.fireTableCellUpdated(rowIndex, columnIndex)
    }

    override fun getRowCount(): Int = data.size
    override fun getColumnCount(): Int = columns.size
    override fun getValueAt(rowIndex: Int, columnIndex: Int): String {
        return columns.getOrNull(columnIndex)?.let {
            val rawValue = it.getter?.invoke(data[rowIndex]) ?: ""
            return it.mapper.mapOut(rawValue)
        } ?: ""
    }

    class ColumnInfo(
        val name: String,
        val getter: Method?,
        val setter: Method? = null,
        val mapper: ReadTableValueMapper = createMapper(DefaultReadTableValueMapper::class),
        editable: Boolean = true,
    ) {
        val editable: Boolean = setter != null && Modifier.isPublic(setter.modifiers) && editable
    }

    companion object {
        private fun createMapper(mapperClass: KClass<out ReadTableValueMapper>): ReadTableValueMapper =
            mapperClass.java.getConstructor().newInstance() as ReadTableValueMapper
    }
}
