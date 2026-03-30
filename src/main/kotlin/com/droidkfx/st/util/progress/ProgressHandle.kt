package com.droidkfx.st.util.progress

class ProgressHandle(val id: Long, private val onComplete: (Long) -> Unit) {
    fun complete() = onComplete(id)
}
