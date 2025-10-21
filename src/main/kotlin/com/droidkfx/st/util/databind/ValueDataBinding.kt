package com.droidkfx.st.util.databind

import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ValueDataBinding<T>(initialValue: T) : ReadOnlyValueDataBinding<T>, ReadWriteValueDataBinding<T> {
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
            notifyChanged()
        }

    override fun update(block: (T) -> Boolean) {
        if (!block(value)) return
        listeners.forEach { it(value) }
    }

    override fun notifyChanged() {
        listeners.forEach {
            logger.trace { "Invoking listener $it" }
            it(value)
        }
    }
}

interface ReadOnlyValueDataBinding<T> {
    fun addListener(listener: (T) -> Unit)
    val value: T
}

interface ReadWriteValueDataBinding<T> {
    fun addListener(listener: (T) -> Unit)
    var value: T

    fun update(block: (T) -> Boolean)
    fun notifyChanged()
}

fun <T> ValueDataBinding<T>.readOnly(): ReadOnlyValueDataBinding<T> {
    return this.mapped { it }
}

fun <T, U> ReadOnlyValueDataBinding<T>.mapped(mapper: (T) -> U): ReadOnlyValueDataBinding<U> {
    return object : ReadOnlyValueDataBinding<U> {
        private val delegate = mapper
        override fun addListener(listener: (U) -> Unit) {
            this@mapped.addListener { listener(delegate(it)) }
        }

        override val value: U
            get() = delegate(this@mapped.value)
    }
}
