package com.droidkfx.st.view.model

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObjectTableModelTest {

    // --- Test data classes ---

    data class AnnotatedRow(
        @field:Column(name = "Name", position = 0)
        var name: String = "default",
        @field:Column(name = "Amount", position = 1, mapper = BigDecimalReadTableValueMapper::class)
        var amount: BigDecimal = BigDecimal.ZERO,
        @field:Column(name = "ReadOnly", position = 2, editable = false)
        var readOnly: String = "fixed"
    )

    data class OrderedRow(
        @field:Column(name = "Third", position = 2)
        var third: String = "c",
        @field:Column(name = "First", position = 0)
        var first: String = "a",
        @field:Column(name = "Second", position = 1)
        var second: String = "b"
    )

    data class ValRow(
        @field:Column(name = "Immutable", position = 0)
        val immutable: String = "fixed"
    )

    // --- Column count and names ---

    @Test
    fun `getColumnCount returns number of annotated fields`() {
        val model = ObjectTableModel(emptyList<AnnotatedRow>(), AnnotatedRow::class.java)
        assertEquals(3, model.columnCount)
    }

    @Test
    fun `getColumnName returns annotation name`() {
        val model = ObjectTableModel(emptyList<AnnotatedRow>(), AnnotatedRow::class.java)
        assertEquals("Name", model.getColumnName(0))
        assertEquals("Amount", model.getColumnName(1))
        assertEquals("ReadOnly", model.getColumnName(2))
    }

    @Test
    fun `columns are ordered by position annotation`() {
        val model = ObjectTableModel(emptyList<OrderedRow>(), OrderedRow::class.java)
        assertEquals("First", model.getColumnName(0))
        assertEquals("Second", model.getColumnName(1))
        assertEquals("Third", model.getColumnName(2))
    }

    @Test
    fun `getColumnName returns Unknown Column for out-of-range index`() {
        val model = ObjectTableModel(emptyList<AnnotatedRow>(), AnnotatedRow::class.java)
        assertEquals("Unknown Column", model.getColumnName(99))
    }

    // --- Row count ---

    @Test
    fun `getRowCount returns zero for empty data`() {
        val model = ObjectTableModel(emptyList<AnnotatedRow>(), AnnotatedRow::class.java)
        assertEquals(0, model.rowCount)
    }

    @Test
    fun `getRowCount returns data list size`() {
        val data = listOf(AnnotatedRow("a", BigDecimal.ONE), AnnotatedRow("b", BigDecimal.TEN))
        val model = ObjectTableModel(data, AnnotatedRow::class.java)
        assertEquals(2, model.rowCount)
    }

    // --- getValueAt ---

    @Test
    fun `getValueAt returns string value for String field`() {
        val row = AnnotatedRow("hello", BigDecimal.ZERO)
        val model = ObjectTableModel(listOf(row), AnnotatedRow::class.java)
        assertEquals("hello", model.getValueAt(0, 0))
    }

    @Test
    fun `getValueAt uses BigDecimalReadTableValueMapper for BigDecimal field`() {
        val row = AnnotatedRow("x", BigDecimal("1.50"))
        val model = ObjectTableModel(listOf(row), AnnotatedRow::class.java)
        assertEquals("1.50", model.getValueAt(0, 1))
    }

    @Test
    fun `getValueAt returns dash for BigDecimal zero`() {
        val row = AnnotatedRow("x", BigDecimal.ZERO)
        val model = ObjectTableModel(listOf(row), AnnotatedRow::class.java)
        assertEquals("-", model.getValueAt(0, 1))
    }

    @Test
    fun `getValueAt returns empty string for out-of-range column`() {
        val row = AnnotatedRow("x", BigDecimal.ONE)
        val model = ObjectTableModel(listOf(row), AnnotatedRow::class.java)
        assertEquals("", model.getValueAt(0, 99))
    }

    // --- Editability ---

    @Test
    fun `isCellEditable is true for var field with editable=true`() {
        val data = listOf(AnnotatedRow("a", BigDecimal.ONE))
        val model = ObjectTableModel(data, AnnotatedRow::class.java)
        assertTrue(model.isCellEditable(0, 0))
    }

    @Test
    fun `isCellEditable is false for editable=false annotation`() {
        val data = listOf(AnnotatedRow("a", BigDecimal.ONE))
        val model = ObjectTableModel(data, AnnotatedRow::class.java)
        assertFalse(model.isCellEditable(0, 2))
    }

    @Test
    fun `isCellEditable is false for val field without setter`() {
        val data = listOf(ValRow())
        val model = ObjectTableModel(data, ValRow::class.java)
        assertFalse(model.isCellEditable(0, 0))
    }

    @Test
    fun `isCellEditable is false for out-of-bounds row`() {
        val model = ObjectTableModel(emptyList<AnnotatedRow>(), AnnotatedRow::class.java)
        assertFalse(model.isCellEditable(0, 0))
    }

    @Test
    fun `isCellEditable is false for negative row`() {
        val data = listOf(AnnotatedRow())
        val model = ObjectTableModel(data, AnnotatedRow::class.java)
        assertFalse(model.isCellEditable(-1, 0))
    }

    @Test
    fun `isColumnEditable matches isCellEditable for valid row`() {
        val data = listOf(AnnotatedRow())
        val model = ObjectTableModel(data, AnnotatedRow::class.java)
        assertEquals(model.isColumnEditable(0), model.isCellEditable(0, 0))
        assertEquals(model.isColumnEditable(2), model.isCellEditable(0, 2))
    }

    // --- setValueAt ---

    @Test
    fun `setValueAt updates var field on the data object`() {
        val row = AnnotatedRow("original", BigDecimal.ONE)
        val model = ObjectTableModel(mutableListOf(row), AnnotatedRow::class.java)
        model.setValueAt("updated", 0, 0)
        assertEquals("updated", row.name)
    }

    @Test
    fun `setValueAt does nothing when row index is out of bounds`() {
        val row = AnnotatedRow("original", BigDecimal.ONE)
        val model = ObjectTableModel(mutableListOf(row), AnnotatedRow::class.java)
        model.setValueAt("updated", 5, 0)
        assertEquals("original", row.name)
    }
}

// --- Mapper tests ---

class BigDecimalReadTableValueMapperTest {

    @Test
    fun `mapOut formats decimal with two places`() {
        val mapper = BigDecimalReadTableValueMapper()
        assertEquals("3.14", mapper.mapOut(BigDecimal("3.14")))
    }

    @Test
    fun `mapOut returns dash for BigDecimal zero`() {
        val mapper = BigDecimalReadTableValueMapper()
        assertEquals("-", mapper.mapOut(BigDecimal.ZERO))
    }

    @Test
    fun `mapOut returns empty string for non-BigDecimal`() {
        val mapper = BigDecimalReadTableValueMapper()
        assertEquals("", mapper.mapOut("not a decimal"))
    }

    @Test
    fun `mapIn parses numeric string`() {
        val mapper = BigDecimalReadTableValueMapper()
        val result = mapper.mapIn("3.14")
        assertEquals(3.14, result.toDouble(), 0.0001)
    }

    @Test
    fun `mapIn returns zero for invalid string`() {
        val mapper = BigDecimalReadTableValueMapper()
        val result = mapper.mapIn("not-a-number")
        assertEquals(0.0, result.toDouble(), 0.0001)
    }
}

class DollarReadTableValueMapperTest {

    @Test
    fun `mapOut prepends dollar sign`() {
        val mapper = DollarReadTableValueMapper()
        assertEquals("$ 3.14", mapper.mapOut(BigDecimal("3.14")))
    }

    @Test
    fun `mapOut returns dash with dollar prefix for zero`() {
        val mapper = DollarReadTableValueMapper()
        assertEquals("$ -", mapper.mapOut(BigDecimal.ZERO))
    }

    @Test
    fun `mapIn strips dollar prefix before parsing`() {
        val mapper = DollarReadTableValueMapper()
        val result = mapper.mapIn("$ 3.14")
        assertEquals(3.14, result.toDouble(), 0.0001)
    }
}

class PercentReadTableValueMapperTest {

    @Test
    fun `mapOut appends percent sign`() {
        val mapper = PercentReadTableValueMapper()
        val result = mapper.mapOut(BigDecimal("5.25"))
        assertTrue(result.endsWith(" %"), "Expected percent suffix, got: $result")
    }

    @Test
    fun `mapOut formats with zero padding`() {
        val mapper = PercentReadTableValueMapper()
        val result = mapper.mapOut(BigDecimal("5.25"))
        assertEquals("05.25 %", result)
    }

    @Test
    fun `mapOut returns dash with percent for zero`() {
        val mapper = PercentReadTableValueMapper()
        assertEquals("- %", mapper.mapOut(BigDecimal.ZERO))
    }

    @Test
    fun `mapIn strips percent suffix before parsing`() {
        val mapper = PercentReadTableValueMapper()
        val result = mapper.mapIn("05.25 %")
        assertEquals(5.25, result.toDouble(), 0.0001)
    }
}

class DefaultReadTableValueMapperTest {

    @Test
    fun `mapOut calls toString`() {
        val mapper = DefaultReadTableValueMapper()
        assertEquals("42", mapper.mapOut(42))
        assertEquals("hello", mapper.mapOut("hello"))
    }

    @Test
    fun `mapIn returns the string unchanged`() {
        val mapper = DefaultReadTableValueMapper()
        assertEquals("some value", mapper.mapIn("some value"))
    }
}
