package com.droidkfx.st.util

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PmapTest {

    @Test
    fun `pmap transforms each element`() {
        val result = runBlocking {
            listOf(1, 2, 3).pmap { it * 2 }
        }
        assertEquals(listOf(2, 4, 6), result)
    }

    @Test
    fun `pmap on empty list returns empty list`() {
        val result = runBlocking {
            emptyList<Int>().pmap { it * 2 }
        }
        assertEquals(emptyList(), result)
    }

    @Test
    fun `pmap preserves order`() {
        val result = runBlocking {
            (1..10).toList().pmap { it }
        }
        assertEquals((1..10).toList(), result)
    }

    @Test
    fun `pmap applies transform to strings`() {
        val result = runBlocking {
            listOf("a", "b", "c").pmap { it.uppercase() }
        }
        assertEquals(listOf("A", "B", "C"), result)
    }

    @Test
    fun `pmap executes all tasks`() {
        val counter = AtomicInteger(0)
        runBlocking {
            (1..20).toList().pmap { counter.incrementAndGet() }
        }
        assertEquals(20, counter.get())
    }

    @Test
    fun `pmap result size matches input size`() {
        val result = runBlocking {
            (1..50).toList().pmap { it.toString() }
        }
        assertEquals(50, result.size)
    }

    @Test
    fun `pmap works with suspend lambda`() {
        val result = runBlocking {
            listOf(1, 2, 3).pmap { value ->
                // Simulate a suspendable operation
                value + 10
            }
        }
        assertTrue(result.all { it > 10 })
    }
}
