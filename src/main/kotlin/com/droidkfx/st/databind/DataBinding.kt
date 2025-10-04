package com.droidkfx.st.databind

import io.github.oshai.kotlinlogging.KotlinLogging.logger

class DataBinding<T>(initialValue: T) : ReadOnlyDataBinding<T>, ReadWriteDataBinding<T> {
    private val logger = logger {}
    private val listeners = mutableListOf<(T) -> Unit>()

    override fun addListener(listener: (T) -> Unit) {
        logger.trace { "addListener $listener" }
        listeners.add(listener)
    }

    override var value: T = initialValue
        set(value) {
            logger.trace { "value $value" }
            if (field == value) {
                logger.trace { "Value not changed, ignoring" }
                return
            }
            field = value
            listeners.forEach {
                logger.trace { "Invoking listener $it" }
                it(value)
            }
        }

    override fun update(block: (T) -> Boolean) {
        if (!block(value)) return
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

    fun update(block: (T) -> Boolean)
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
