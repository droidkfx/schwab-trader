package com.droidkfx.st.view

import java.awt.event.ActionEvent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MultiSelectDropDownTest {

    private val allItems = listOf("Open", "Filled", "Canceled", "Expired")

    private fun drop(
        items: List<String> = allItems,
        initial: Set<String> = items.toSet(),
        onChanged: (Set<String>) -> Unit = {},
    ) = MultiSelectDropDown(items, { it }, initial, onChanged)

    private fun MultiSelectDropDown<String>.simulateToggle(item: String) {
        val (_, cb) = checkBoxItems.first { it.first == item }
        cb.isSelected = !cb.isSelected
        cb.actionListeners.forEach { it.actionPerformed(ActionEvent(cb, ActionEvent.ACTION_PERFORMED, "")) }
    }

    private fun MultiSelectDropDown<String>.simulateAllToggle() {
        allCheckBox.isSelected = !allCheckBox.isSelected
        allCheckBox.actionListeners.forEach {
            it.actionPerformed(
                ActionEvent(
                    allCheckBox,
                    ActionEvent.ACTION_PERFORMED,
                    "",
                ),
            )
        }
    }

    // --- initial display text ---

    @Test
    fun `text is All when all items initially selected`() {
        assertEquals("All", drop().text)
    }

    @Test
    fun `text is None when initial selection is empty`() {
        assertEquals("None", drop(initial = emptySet()).text)
    }

    @Test
    fun `text is the item label when one item initially selected`() {
        assertEquals("Filled", drop(initial = setOf("Filled")).text)
    }

    @Test
    fun `text is comma-joined labels when two items initially selected`() {
        assertEquals("Open, Canceled", drop(initial = setOf("Open", "Canceled")).text)
    }

    @Test
    fun `text shows count when three or more items are selected but not all`() {
        assertEquals("3 selected", drop(initial = setOf("Open", "Filled", "Canceled")).text)
    }

    // --- All checkbox initial state ---

    @Test
    fun `All checkbox is checked when all items initially selected`() {
        assertTrue(drop().allCheckBox.isSelected)
    }

    @Test
    fun `All checkbox is unchecked when no items initially selected`() {
        assertFalse(drop(initial = emptySet()).allCheckBox.isSelected)
    }

    @Test
    fun `All checkbox is unchecked when only some items initially selected`() {
        assertFalse(drop(initial = setOf("Open")).allCheckBox.isSelected)
    }

    // --- All checkbox toggle ---

    @Test
    fun `unchecking All deselects all items`() {
        val d = drop()
        d.simulateAllToggle()
        assertEquals(emptySet(), d.selection)
    }

    @Test
    fun `unchecking All updates text to None`() {
        val d = drop()
        d.simulateAllToggle()
        assertEquals("None", d.text)
    }

    @Test
    fun `checking All selects all items`() {
        val d = drop(initial = emptySet())
        d.simulateAllToggle()
        assertEquals(allItems.toSet(), d.selection)
    }

    @Test
    fun `checking All updates text to All`() {
        val d = drop(initial = emptySet())
        d.simulateAllToggle()
        assertEquals("All", d.text)
    }

    @Test
    fun `unchecking All fires callback with empty set`() {
        val captured = mutableListOf<Set<String>>()
        val d = drop(onChanged = { captured.add(it) })
        d.simulateAllToggle()
        assertEquals(1, captured.size)
        assertEquals(emptySet(), captured.first())
    }

    @Test
    fun `checking All fires callback with full set`() {
        val captured = mutableListOf<Set<String>>()
        val d = drop(initial = emptySet(), onChanged = { captured.add(it) })
        d.simulateAllToggle()
        assertEquals(1, captured.size)
        assertEquals(allItems.toSet(), captured.first())
    }

    // --- All checkbox syncs with individual toggles ---

    @Test
    fun `All checkbox becomes unchecked when any item is deselected`() {
        val d = drop()
        d.simulateToggle("Open")
        assertFalse(d.allCheckBox.isSelected)
    }

    @Test
    fun `All checkbox becomes checked when last missing item is selected`() {
        val d = drop(initial = setOf("Filled", "Canceled", "Expired"))
        d.simulateToggle("Open")
        assertTrue(d.allCheckBox.isSelected)
    }

    // --- selection property ---

    @Test
    fun `selection reflects initial set`() {
        val initial = setOf("Open", "Filled")
        assertEquals(initial, drop(initial = initial).selection)
    }

    @Test
    fun `selection is full set when all items initially selected`() {
        assertEquals(allItems.toSet(), drop().selection)
    }

    // --- individual item toggling ---

    @Test
    fun `toggling off an item removes it from selection`() {
        val d = drop()
        d.simulateToggle("Open")
        assertEquals(setOf("Filled", "Canceled", "Expired"), d.selection)
    }

    @Test
    fun `toggling on an item adds it to selection`() {
        val d = drop(initial = emptySet())
        d.simulateToggle("Filled")
        assertEquals(setOf("Filled"), d.selection)
    }

    @Test
    fun `toggling off the last item results in empty selection`() {
        val d = drop(initial = setOf("Open"))
        d.simulateToggle("Open")
        assertEquals(emptySet(), d.selection)
    }

    // --- text updates on toggle ---

    @Test
    fun `text updates to None after all items are toggled off`() {
        val d = drop()
        allItems.forEach { d.simulateToggle(it) }
        assertEquals("None", d.text)
    }

    @Test
    fun `text updates to All after all items are toggled back on`() {
        val d = drop(initial = emptySet())
        allItems.forEach { d.simulateToggle(it) }
        assertEquals("All", d.text)
    }

    @Test
    fun `text updates to single label when only one item remains`() {
        val d = drop()
        listOf("Open", "Filled", "Canceled").forEach { d.simulateToggle(it) }
        assertEquals("Expired", d.text)
    }

    @Test
    fun `text preserves original item order in comma-joined label`() {
        // Items list order: Open, Filled, Canceled, Expired
        // Initial set given in reverse — label must follow items-list order
        val d = drop(initial = setOf("Expired", "Open"))
        assertEquals("Open, Expired", d.text)
    }

    // --- callback ---

    @Test
    fun `onSelectionChanged fires with updated set when item is toggled off`() {
        val captured = mutableListOf<Set<String>>()
        val d = drop(onChanged = { captured.add(it) })
        d.simulateToggle("Open")
        assertEquals(1, captured.size)
        assertEquals(setOf("Filled", "Canceled", "Expired"), captured.first())
    }

    @Test
    fun `onSelectionChanged fires with updated set when item is toggled on`() {
        val captured = mutableListOf<Set<String>>()
        val d = drop(initial = emptySet(), onChanged = { captured.add(it) })
        d.simulateToggle("Canceled")
        assertEquals(1, captured.size)
        assertEquals(setOf("Canceled"), captured.first())
    }

    @Test
    fun `onSelectionChanged fires once per toggle`() {
        val captured = mutableListOf<Set<String>>()
        val d = drop(onChanged = { captured.add(it) })
        d.simulateToggle("Open")
        d.simulateToggle("Filled")
        assertEquals(2, captured.size)
    }
}
