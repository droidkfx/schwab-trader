package com.droidkfx.st.util.progress

import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.readOnly

class ProgressService {
    private var nextId = 0L
    private val tasks = mutableListOf<Pair<Long, String>>()
    private val _displayText = ValueDataBinding("")
    val displayText: ReadOnlyValueDataBinding<String> = _displayText.readOnly()

    suspend fun <T> track(message: String, block: suspend () -> T): T {
        val handle = begin(message)
        try {
            return block()
        } finally {
            handle.complete()
        }
    }

    @Synchronized
    fun begin(message: String): ProgressHandle {
        val id = nextId++
        tasks.add(id to message)
        updateText()
        return ProgressHandle(id, ::complete)
    }

    @Synchronized
    internal fun complete(id: Long) {
        tasks.removeIf { it.first == id }
        updateText()
    }

    private fun updateText() {
        _displayText.value = when (tasks.size) {
            0 -> ""
            1 -> "${tasks[0].second}..."
            else -> "${tasks[0].second}... +${tasks.size - 1}"
        }
    }
}
