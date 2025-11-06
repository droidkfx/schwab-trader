package com.droidkfx.st.util.databind

import io.github.oshai.kotlinlogging.KotlinLogging.logger

typealias DataBindChangeListener<T> = (oldValue: T, newValue: T) -> Unit

typealias DataBindValueListener<T> = (newValue: T) -> Unit

fun <T> DataBindValueListener<T>.asChangeListener(): DataBindChangeListener<T> {
    return { _, newValue -> this(newValue) }
}

class ValueDataBinding<T>(initialValue: T) : ReadOnlyValueDataBinding<T>, ReadWriteValueDataBinding<T> {
    private val logger = logger {}
    private val listeners = mutableListOf<DataBindChangeListener<T>>()

    override fun addListener(listener: DataBindChangeListener<T>) {
        logger.trace { "addListener $listener" }
        listeners.add(listener)
    }

    override fun addListener(listener: DataBindValueListener<T>) {
        addListener(listener.asChangeListener())
    }

    override var value: T = initialValue
        set(newValue) {
            logger.trace { "value $newValue" }
            val oldValue = field
            if (oldValue == newValue) {
                logger.trace { "Value not changed, ignoring" }
                return
            }
            field = newValue
            listeners.forEach {
                logger.trace { "Invoking listener $it" }
                it(oldValue, newValue)
            }
        }
}

interface SubscribeDataBinding<T> {
    fun addListener(listener: DataBindChangeListener<T>)
    fun addListener(listener: DataBindValueListener<T>)
}

interface ReadOnlyValueDataBinding<T> : SubscribeDataBinding<T> {
    val value: T
}

interface ReadWriteValueDataBinding<T> : SubscribeDataBinding<T> {
    var value: T
}

fun <T> ValueDataBinding<T>.readOnly(): ReadOnlyValueDataBinding<T> {
    return this.mapped { it }
}

fun <T> T.toDataBinding(): ValueDataBinding<T> = ValueDataBinding(this)

fun <T, U> ReadOnlyValueDataBinding<T>.mapped(mapper: (T) -> U): ReadOnlyValueDataBinding<U> {
    return object : ReadOnlyValueDataBinding<U> {
        private val logger = logger {}

        private val delegate = mapper
        override fun addListener(listener: DataBindChangeListener<U>) {
            this@mapped.addListener { oldValue, newValue ->
                val mappedOldValue = delegate(oldValue)
                val mappedNewValue = delegate(newValue)
                if (mappedOldValue == mappedNewValue) {
                    logger.trace { "Value not changed, ignoring" }
                    return@addListener
                }
                listener(delegate(oldValue), delegate(newValue))
            }
        }

        override fun addListener(listener: DataBindValueListener<U>) {
            addListener(listener.asChangeListener())
        }

        override val value: U
            get() = delegate(this@mapped.value)
    }
}
