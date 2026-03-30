package com.droidkfx.st.util.progress

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ProgressServiceTest {

    private val service = ProgressService()

    @Test
    fun `displayText is empty initially`() {
        assertEquals("", service.displayText.value)
    }

    @Test
    fun `begin shows task message with ellipsis`() {
        service.begin("Loading data")
        assertEquals("Loading data...", service.displayText.value)
    }

    @Test
    fun `complete clears the task`() {
        val handle = service.begin("Loading data")
        handle.complete()
        assertEquals("", service.displayText.value)
    }

    @Test
    fun `two concurrent tasks show first message plus count`() {
        val h1 = service.begin("Task A")
        val h2 = service.begin("Task B")
        assertEquals("Task A... +1", service.displayText.value)
        h1.complete()
        h2.complete()
    }

    @Test
    fun `three concurrent tasks show first message plus two count`() {
        service.begin("Task A")
        service.begin("Task B")
        service.begin("Task C")
        assertEquals("Task A... +2", service.displayText.value)
    }

    @Test
    fun `completing first task shifts display to second task`() {
        val h1 = service.begin("Task A")
        service.begin("Task B")
        h1.complete()
        assertEquals("Task B...", service.displayText.value)
    }

    @Test
    fun `completing all tasks clears displayText`() {
        val h1 = service.begin("Task A")
        val h2 = service.begin("Task B")
        h1.complete()
        h2.complete()
        assertEquals("", service.displayText.value)
    }

    @Test
    fun `track returns the value produced by the block`() {
        val result = runBlocking { service.track("Computing") { 42 } }
        assertEquals(42, result)
    }

    @Test
    fun `track shows message during block execution`() {
        var messageDuringExecution = ""
        runBlocking {
            service.track("Processing") {
                messageDuringExecution = service.displayText.value
            }
        }
        assertEquals("Processing...", messageDuringExecution)
    }

    @Test
    fun `track clears task after block completes`() {
        runBlocking { service.track("Working") { } }
        assertEquals("", service.displayText.value)
    }

    @Test
    fun `track clears task even when block throws`() {
        assertThrows<RuntimeException> {
            runBlocking { service.track("Failing task") { throw RuntimeException("oops") } }
        }
        assertEquals("", service.displayText.value)
    }

    @Test
    fun `nested track shows both tasks with collapsed format`() {
        var innerMessage = ""
        runBlocking {
            service.track("Outer") {
                service.track("Inner") {
                    innerMessage = service.displayText.value
                }
            }
        }
        assertEquals("Outer... +1", innerMessage)
    }

    @Test
    fun `nested track clears all tasks on completion`() {
        runBlocking {
            service.track("Outer") {
                service.track("Inner") { }
            }
        }
        assertEquals("", service.displayText.value)
    }
}
