package com.droidkfx.st.databind

class DataBinding<T>(initialValue: T) : ReadOnlyDataBinding<T>, ReadWriteDataBinding<T> {
    private val listeners = mutableListOf<(T) -> Unit>()

    override fun addListener(listener: (T) -> Unit) {
        listeners.add(listener)
    }

    override var value: T = initialValue
        set(value) {
            if (field == value) return
            field = value
            listeners.forEach { it(value) }
        }
}

interface ReadOnlyDataBinding<T> {
    fun addListener(listener: (T) -> Unit)
    val value: T
}

interface ReadWriteDataBinding<T> {
    fun addListener(listener: (T) -> Unit)
    var value: T
}

fun <T, U> ReadOnlyDataBinding<T>.mapped(mapper: (T) -> U): ReadOnlyDataBinding<U> {
    return object : ReadOnlyDataBinding<U> {
        private val delegate = mapper
        override fun addListener(listener: (U) -> Unit) {
            this@mapped.addListener { listener(delegate(it)) }
        }

        override val value: U
            get() = delegate(this@mapped.value)
    }
}
