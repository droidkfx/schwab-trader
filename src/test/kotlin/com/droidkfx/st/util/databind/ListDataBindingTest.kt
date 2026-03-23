package com.droidkfx.st.util.databind

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ListDataBindingTest {

    // --- ReadWriteListDataBinding (mutable) ---

    @Test
    fun `add fires ADD event with correct value and index`() {
        val list = mutableListOf<String>().toDataBinding()
        val events = mutableListOf<ListDataBindingEvent<String>>()
        list.addListener { events.add(it) }

        list.add("hello")

        assertEquals(1, events.size)
        assertEquals(ListDataBindingEventType.ADD, events[0].type)
        assertEquals("hello", events[0].currentValue)
        assertEquals(0, events[0].index)
    }

    @Test
    fun `add at index fires ADD event at correct index`() {
        val list = mutableListOf("a", "c").toDataBinding()
        val events = mutableListOf<ListDataBindingEvent<String>>()
        list.addListener { events.add(it) }

        list.add(1, "b")

        assertEquals(1, events.size)
        assertEquals(ListDataBindingEventType.ADD, events[0].type)
        assertEquals("b", events[0].currentValue)
        assertEquals(1, events[0].index)
    }

    @Test
    fun `remove fires REMOVE event with correct value`() {
        val list = mutableListOf("a", "b").toDataBinding()
        val events = mutableListOf<ListDataBindingEvent<String>>()
        list.addListener { events.add(it) }

        list.remove("a")

        assertEquals(1, events.size)
        assertEquals(ListDataBindingEventType.REMOVE, events[0].type)
        assertEquals("a", events[0].currentValue)
    }

    @Test
    fun `remove returns false and does not fire event for missing element`() {
        val list = mutableListOf("a").toDataBinding()
        val events = mutableListOf<ListDataBindingEvent<String>>()
        list.addListener { events.add(it) }

        val result = list.remove("missing")

        assertFalse(result)
        assertEquals(0, events.size)
    }

    @Test
    fun `removeAt fires REMOVE event at correct index`() {
        val list = mutableListOf("a", "b", "c").toDataBinding()
        val events = mutableListOf<ListDataBindingEvent<String>>()
        list.addListener { events.add(it) }

        list.removeAt(1)

        assertEquals(1, events.size)
        assertEquals(1, events[0].index)
        assertEquals("b", events[0].currentValue)
        assertEquals(ListDataBindingEventType.REMOVE, events[0].type)
    }

    @Test
    fun `set fires UPDATE event with old and new values`() {
        val list = mutableListOf("a", "b").toDataBinding()
        val updateEvents = mutableListOf<ValueUpdatedListDataBindingEvent<String>>()
        list.addListener { if (it is ValueUpdatedListDataBindingEvent) updateEvents.add(it) }

        list[0] = "x"

        assertEquals(1, updateEvents.size)
        assertEquals("x", updateEvents[0].currentValue)
        assertEquals("a", updateEvents[0].previousValue)
        assertEquals(ListDataBindingEventType.UPDATE, updateEvents[0].type)
    }

    @Test
    fun `addAll fires an ADD event for each element`() {
        val list = mutableListOf<String>().toDataBinding()
        val events = mutableListOf<ListDataBindingEvent<String>>()
        list.addListener { events.add(it) }

        list.addAll(listOf("x", "y", "z"))

        assertEquals(3, events.size)
        assertTrue(events.all { it.type == ListDataBindingEventType.ADD })
    }

    @Test
    fun `addAll at index fires ADD events with correct indices`() {
        val list = mutableListOf("a", "d").toDataBinding()
        val events = mutableListOf<ListDataBindingEvent<String>>()
        list.addListener { events.add(it) }

        list.addAll(1, listOf("b", "c"))

        assertEquals(2, events.size)
        assertEquals(1, events[0].index)
        assertEquals("b", events[0].currentValue)
        assertEquals(2, events[1].index)
        assertEquals("c", events[1].currentValue)
    }

    @Test
    fun `clear fires REMOVE events for each element`() {
        val list = mutableListOf("a", "b", "c").toDataBinding()
        val events = mutableListOf<ListDataBindingEvent<String>>()
        list.addListener { events.add(it) }

        list.clear()

        assertTrue(list.isEmpty())
        assertEquals(3, events.size)
        assertTrue(events.all { it.type == ListDataBindingEventType.REMOVE })
    }

    @Test
    fun `size reflects mutations`() {
        val list = mutableListOf<Int>().toDataBinding()
        assertEquals(0, list.size)
        list.add(1)
        assertEquals(1, list.size)
        list.add(2)
        assertEquals(2, list.size)
        list.remove(1)
        assertEquals(1, list.size)
    }

    @Test
    fun `contains returns true for present element`() {
        val list = mutableListOf("a", "b").toDataBinding()
        assertTrue(list.contains("a"))
        assertFalse(list.contains("c"))
    }

    @Test
    fun `indexOf returns correct index`() {
        val list = mutableListOf("a", "b", "c").toDataBinding()
        assertEquals(1, list.indexOf("b"))
        assertEquals(-1, list.indexOf("z"))
    }

    // --- ReadOnlyListDataBinding (immutable) ---

    @Test
    fun `immutable list toDataBinding wraps correctly`() {
        val list = listOf("x", "y", "z").toDataBinding()
        assertEquals(3, list.size)
        assertEquals("x", list[0])
        assertEquals("y", list[1])
        assertEquals("z", list[2])
    }

    @Test
    fun `readOnly exposes same list contents`() {
        val source = mutableListOf("a", "b").toDataBinding()
        val readOnly = source.readOnly()
        assertEquals(2, readOnly.size)
        assertEquals("a", readOnly[0])
    }

    @Test
    fun `readOnly propagates ADD events from source`() {
        val source = mutableListOf<String>().toDataBinding()
        val readOnly = source.readOnly()
        val events = mutableListOf<ListDataBindingEvent<String>>()
        readOnly.addListener { events.add(it) }

        source.add("hello")

        assertEquals(1, events.size)
        assertEquals(ListDataBindingEventType.ADD, events[0].type)
        assertEquals("hello", events[0].currentValue)
    }

    @Test
    fun `readOnly propagates REMOVE events from source`() {
        val source = mutableListOf("x").toDataBinding()
        val readOnly = source.readOnly()
        val events = mutableListOf<ListDataBindingEvent<String>>()
        readOnly.addListener { events.add(it) }

        source.remove("x")

        assertEquals(1, events.size)
        assertEquals(ListDataBindingEventType.REMOVE, events[0].type)
    }

    // --- MappedListDataBinding ---

    @Test
    fun `mapped transforms values`() {
        val list = mutableListOf(1, 2, 3).toDataBinding()
        val mapped = list.mapped { it * 10 }
        assertEquals(10, mapped[0])
        assertEquals(20, mapped[1])
        assertEquals(30, mapped[2])
    }

    @Test
    fun `mapped size matches source`() {
        val list = mutableListOf(1, 2, 3).toDataBinding()
        val mapped = list.mapped { it.toString() }
        assertEquals(3, mapped.size)
    }

    @Test
    fun `mapped fires ADD event with mapped value`() {
        val list = mutableListOf(1, 2).toDataBinding()
        val mapped = list.mapped { it.toString() }
        val events = mutableListOf<ListDataBindingEvent<String>>()
        mapped.addListener { events.add(it) }

        list.add(3)

        assertEquals(1, events.size)
        assertEquals("3", events[0].currentValue)
        assertEquals(ListDataBindingEventType.ADD, events[0].type)
    }

    @Test
    fun `mapped UPDATE event is suppressed when mapped value does not change`() {
        val list = mutableListOf(1, 2).toDataBinding()
        val mapped = list.mapped { "constant" }
        val events = mutableListOf<ListDataBindingEvent<String>>()
        mapped.addListener { events.add(it) }

        list[0] = 99

        assertEquals(0, events.size, "Event should be suppressed when mapped value is unchanged")
    }

    @Test
    fun `mapped UPDATE event fires when mapped value changes`() {
        val list = mutableListOf(1, 2).toDataBinding()
        val mapped = list.mapped { it * 10 }
        val events = mutableListOf<ListDataBindingEvent<Int>>()
        mapped.addListener { events.add(it) }

        list[0] = 5

        assertEquals(1, events.size)
        assertEquals(50, events[0].currentValue)
    }

    @Test
    fun `mapped contains uses mapped values`() {
        val list = mutableListOf(1, 2, 3).toDataBinding()
        val mapped = list.mapped { it * 10 }
        assertTrue(mapped.contains(20))
        assertFalse(mapped.contains(2))
    }

    @Test
    fun `mapped indexOf uses mapped values`() {
        val list = mutableListOf(1, 2, 3).toDataBinding()
        val mapped = list.mapped { it * 10 }
        assertEquals(1, mapped.indexOf(20))
        assertEquals(-1, mapped.indexOf(2))
    }

    @Test
    fun `ReadOnlyListDataBinding mapped from readOnly`() {
        val source = listOf(1, 2, 3).toDataBinding()
        val mapped = source.mapped { it.toString() }
        assertEquals("1", mapped[0])
        assertEquals("2", mapped[1])
    }

    // --- ValueUpdatedListDataBindingEvent ---

    @Test
    fun `ValueUpdatedListDataBindingEvent shouldEmit is false when values are equal`() {
        val event = ValueUpdatedListDataBindingEvent(0, "same", "same")
        assertFalse(event.shouldEmit)
    }

    @Test
    fun `ValueUpdatedListDataBindingEvent shouldEmit is true when values differ`() {
        val event = ValueUpdatedListDataBindingEvent(0, "new", "old")
        assertTrue(event.shouldEmit)
    }

    @Test
    fun `ValueUpdatedListDataBindingEvent mapped preserves event type`() {
        val event = ValueUpdatedListDataBindingEvent(2, 10, 5)
        val mappedEvent = event.mapped { it * 2 }
        assertEquals(20, mappedEvent.currentValue)
        assertEquals(10, mappedEvent.previousValue)
        assertEquals(ListDataBindingEventType.UPDATE, mappedEvent.type)
    }
}
