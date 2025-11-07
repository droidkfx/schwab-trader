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
            val oldValue = field
            if (oldValue == newValue) {
                logger.trace { "Value not changed, ignoring" }
                return
            }
            logger.trace { "value changed from $oldValue to $newValue" }
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
    return this.readOnlyMapped { it }
}

fun <T> T.toDataBinding(): ValueDataBinding<T> = ValueDataBinding(this)

private abstract class MappedDataBinding<T, U>(
    private val delegate: SubscribeDataBinding<T>,
    protected val mapperFrom: (T) -> U
) : SubscribeDataBinding<U> {
    private val logger = logger {}

    override fun addListener(listener: DataBindChangeListener<U>) {
        delegate.addListener { oldValue, newValue ->
            val mappedOldValue = mapperFrom(oldValue)
            val mappedNewValue = mapperFrom(newValue)
            if (mappedOldValue == mappedNewValue) {
                logger.trace { "Value not changed, ignoring" }
                return@addListener
            }
            listener(mapperFrom(oldValue), mapperFrom(newValue))
        }
    }

    override fun addListener(listener: DataBindValueListener<U>) {
        addListener(listener.asChangeListener())
    }
}

private class ReadOnlyMappedDataBinding<T, U>(
    private val delegate: ReadOnlyValueDataBinding<T>,
    mapperFrom: (T) -> U
) : MappedDataBinding<T, U>(delegate, mapperFrom), ReadOnlyValueDataBinding<U> {
    override val value: U
        get() = mapperFrom(delegate.value)
}

private class ReadWriteMappedDataBinding<T, U>(
    private val delegate: ReadWriteValueDataBinding<T>,
    mapperFrom: (T) -> U,
    private val mapperTo: (T, U) -> T
) : MappedDataBinding<T, U>(delegate, mapperFrom), ReadWriteValueDataBinding<U> {
    override var value: U
        get() = mapperFrom(delegate.value)
        set(value) {
            delegate.value = mapperTo(delegate.value, value)
        }
}

fun <T, U> ReadOnlyValueDataBinding<T>.readOnlyMapped(mapper: (T) -> U): ReadOnlyValueDataBinding<U> {
    return ReadOnlyMappedDataBinding(this, mapper)
}

fun <T, U> ReadWriteValueDataBinding<T>.mapped(
    mapperFrom: (T) -> U,
    mapperUp: (T, U) -> T
): ReadWriteValueDataBinding<U> {
    return ReadWriteMappedDataBinding(this, mapperFrom, mapperUp)
}
