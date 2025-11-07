package com.droidkfx.st.util.databind

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ValueDataBindingTest {
    @Test
    fun `test asChangeListener executes callback`() {
        var data = 0

        val myListener = { value: Int -> data = value }
        val changeListener = myListener.asChangeListener()

        changeListener(0, 1)

        assertEquals(1, data)
    }

    @Test
    fun `test asChangeListener does not swallow exceptions`() {
        val myListener = { _: Int -> throw Exception("Something went wrong") }
        val changeListener = myListener.asChangeListener()

        assertThrows<Exception> { changeListener(0, 1) }
    }

    @Test
    fun `test setValue with same value does not modify value`() {
        val valueDataBinding = ValueDataBinding(1)
        assertEquals(1, valueDataBinding.value)
        valueDataBinding.value = 1
        assertEquals(1, valueDataBinding.value)
    }

    @Test
    fun `test setValue with updates value`() {
        val valueDataBinding = ValueDataBinding(1)
        assertEquals(1, valueDataBinding.value)
        valueDataBinding.value = 2
        assertEquals(2, valueDataBinding.value)
    }

    @Test
    fun `test setValue does not notify when value is the same`() {
        val valueDataBinding = ValueDataBinding(1)
        valueDataBinding.addListener { _, _ -> throw Exception("Should not be called") }
        valueDataBinding.addListener { _ -> throw Exception("Should not be called") }
        assertDoesNotThrow { valueDataBinding.value = 1 }
    }

    @Test
    fun `test setValue does notifies when value is different`() {
        val valueDataBinding = ValueDataBinding(1)
        var notified1 = false
        var notified1OldValue = 0
        var notified1NewValue = 0
        var notified2 = false
        var notified2NewValue = 0
        valueDataBinding.addListener { oldValue, newValue ->
            notified1 = true
            notified1OldValue = oldValue
            notified1NewValue = newValue
        }
        valueDataBinding.addListener { newValue ->
            notified2 = true
            notified2NewValue = newValue
        }
        assertDoesNotThrow { valueDataBinding.value = 2 }

        assertTrue(notified1)
        assertEquals(1, notified1OldValue)
        assertEquals(2, notified1NewValue)
        assertTrue(notified2)
        assertEquals(2, notified2NewValue)
    }

    @Test
    fun `test readOnly gives value`() {
        val valueDataBinding = ValueDataBinding(1)
        val readOnlyDataBinding = valueDataBinding.readOnly()
        assertEquals(1, readOnlyDataBinding.value)

        valueDataBinding.value = 2
        assertEquals(2, readOnlyDataBinding.value)
    }

    @Test
    fun `test readOnly cannot be cast back to ReadWriteValueDataBinding`() {
        val valueDataBinding = ValueDataBinding(1)
        val readOnlyDataBinding = valueDataBinding.readOnly()
        assertFalse(readOnlyDataBinding is ReadWriteValueDataBinding<*>)
    }

    @Test
    fun `test readOnlyMappedDataBinding returns mapped value`() {
        val valueDataBinding = ValueDataBinding(1)
        val readOnlyDataBinding = valueDataBinding.readOnlyMapped { it.toString() }
        assertEquals("1", readOnlyDataBinding.value)
    }

    @Test
    fun `test readOnlyMappedDataBinding updates when value changes`() {
        val valueDataBinding = ValueDataBinding(1)
        val readOnlyDataBinding = valueDataBinding.readOnlyMapped { it.toString() }
        assertEquals("1", readOnlyDataBinding.value)

        valueDataBinding.value = 2
        assertEquals("2", readOnlyDataBinding.value)
    }

    @Test
    fun `test readOnlyMappedDataBinding cannot be cast back to ReadWriteValueDataBinding`() {
        val valueDataBinding = ValueDataBinding(1)
        val readOnlyDataBinding = valueDataBinding.readOnlyMapped { it.toString() }
        assertFalse(readOnlyDataBinding is ReadWriteValueDataBinding<*>)
    }

    @Test
    fun `test readOnlyMappedDataBinding does not notify when mapped value is the same`() {
        val valueDataBinding = ValueDataBinding(1)
        var listenerExecuted = false
        valueDataBinding.addListener { _ -> listenerExecuted = true }
        val readOnlyDataBinding = valueDataBinding.readOnlyMapped { "some constant value" }
        readOnlyDataBinding.addListener { _, _ -> throw Exception("Should not be called") }

        assertDoesNotThrow { valueDataBinding.value = 2 }

        assertEquals("some constant value", readOnlyDataBinding.value)
        assertTrue(listenerExecuted)
    }

    @Test
    fun `test readOnlyMappedDataBinding does notify when mapped value is not`() {
        val valueDataBinding = ValueDataBinding(1)
        var listenerExecuted = false
        valueDataBinding.addListener { _ -> listenerExecuted = true }
        val readOnlyDataBinding = valueDataBinding.readOnlyMapped { it.toString() }
        var mappedListenerExecuted1 = false
        var mappedListenerExecuted2 = false
        readOnlyDataBinding.addListener { _, _ -> mappedListenerExecuted1 = true }
        readOnlyDataBinding.addListener { _ -> mappedListenerExecuted2 = true }

        assertDoesNotThrow { valueDataBinding.value = 2 }

        assertEquals("2", readOnlyDataBinding.value)
        assertTrue(listenerExecuted)
        assertTrue(mappedListenerExecuted1)
        assertTrue(mappedListenerExecuted2)
    }

    @Test
    fun `test readWriteMappedDataBinding returns mapped value`() {
        val valueDataBinding = ValueDataBinding(1)
        val mappedDatabind = valueDataBinding.mapped(
            mapperFrom = { it.toString() },
            mapperUp = { it, _ -> it }
        )
        assertEquals("1", mappedDatabind.value)
    }

    @Test
    fun `test readWriteMappedDataBinding updates when value changes`() {
        val valueDataBinding = ValueDataBinding(1)
        val mappedDatabind = valueDataBinding.mapped(
            mapperFrom = { it.toString() },
            mapperUp = { it, _ -> it }
        )
        assertEquals("1", mappedDatabind.value)

        valueDataBinding.value = 2
        assertEquals("2", mappedDatabind.value)
    }

    @Test
    fun `test readWriteMappedDataBinding does not notify when mapped value is the same`() {
        val valueDataBinding = ValueDataBinding(1)
        var listenerExecuted = false
        valueDataBinding.addListener { _ -> listenerExecuted = true }
        val mappedDatabind = valueDataBinding.mapped(
            mapperFrom = { "TROLOLOL" },
            mapperUp = { it, _ -> it }
        )
        mappedDatabind.addListener { _, _ -> throw Exception("Should not be called") }

        assertDoesNotThrow { valueDataBinding.value = 2 }

        assertEquals("TROLOLOL", mappedDatabind.value)
        assertTrue(listenerExecuted)
    }

    @Test
    fun `test readWriteMappedDataBinding does notify when mapped value is not`() {
        val valueDataBinding = ValueDataBinding(1)
        var listenerExecuted = false
        valueDataBinding.addListener { _ -> listenerExecuted = true }
        val mappedDatabind = valueDataBinding.mapped(
            mapperFrom = { it.toString() },
            mapperUp = { it, _ -> it }
        )
        var mappedListenerExecuted1 = false
        var mappedListenerExecuted2 = false
        mappedDatabind.addListener { _, _ -> mappedListenerExecuted1 = true }
        mappedDatabind.addListener { _ -> mappedListenerExecuted2 = true }

        assertDoesNotThrow { valueDataBinding.value = 2 }

        assertEquals("2", mappedDatabind.value)
        assertTrue(listenerExecuted)
        assertTrue(mappedListenerExecuted1)
        assertTrue(mappedListenerExecuted2)
    }

    @Test
    fun `test readWriteMappedDataBinding propogates changes to source`() {
        val valueDataBinding = ValueDataBinding(1)
        val mappedDatabind = valueDataBinding.mapped(
            mapperFrom = { it.toString() },
            mapperUp = { _, str -> str.toInt() }
        )

        mappedDatabind.value = "2"

        assertEquals("2", mappedDatabind.value)
        assertEquals(2, valueDataBinding.value)
    }

    @Test
    fun `test readWriteMappedDataBinding case study`() {
        data class Foo(val id: Int, val name: String)

        val valueDataBinding = ValueDataBinding(Foo(1, "Foo"))
        val mappedDatabind = valueDataBinding.mapped(
            mapperFrom = { it.name },
            mapperUp = { it, str -> it.copy(name = str) }
        )

        mappedDatabind.value = "2"

        assertEquals("2", mappedDatabind.value)
        assertEquals(Foo(1, "2"), valueDataBinding.value)
    }
}
