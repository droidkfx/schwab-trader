package com.droidkfx.st.util.databind

interface ListDataBinding<T> {
    fun addListener(listener: (ListDataBindingEvent<T>) -> Unit)
}

interface ReadOnlyListDataBinding<T> : List<T>, ListDataBinding<T>

interface ReadWriteListDataBinding<T> : MutableList<T>, ListDataBinding<T>

enum class ListDataBindingEventType {
    ADD, REMOVE, UPDATE
}

class ValueUpdatedListDataBindingEvent<T>(
    index: Int,
    currentValue: T,
    val previousValue: T
) : ListDataBindingEvent<T>(index, currentValue, ListDataBindingEventType.UPDATE) {
    val shouldEmit = currentValue != previousValue

    override fun <U> mapped(mapper: (T) -> U): ValueUpdatedListDataBindingEvent<U> {
        return ValueUpdatedListDataBindingEvent(index, mapper(currentValue), mapper(previousValue))
    }
}

open class ListDataBindingEvent<T>(val index: Int, val currentValue: T, val type: ListDataBindingEventType) {
    open fun <U> mapped(mapper: (T) -> U): ListDataBindingEvent<U> {
        return ListDataBindingEvent(index, mapper(currentValue), type)
    }
}

fun <T> List<T>.toDataBinding(): ReadOnlyListDataBinding<T> = ReadOnlyListDataBindingImpl(this)

fun <T> MutableList<T>.toDataBinding(): ReadWriteListDataBinding<T> = ReadWriteListDataBindingImpl(this)

fun <T> ReadWriteListDataBinding<T>.readOnly(): ReadOnlyListDataBinding<T> = ReadOnlyListDataBindingImpl(this)

fun <T, U> ReadWriteListDataBinding<T>.mapped(mapper: (T) -> U): ReadOnlyListDataBinding<U> =
    MappedListDataBinding(readOnly(), mapper)

fun <T, U> ReadOnlyListDataBinding<T>.mapped(mapper: (T) -> U): ReadOnlyListDataBinding<U> =
    MappedListDataBinding(this, mapper)

private class MappedListDataBinding<T, U>(
    private val delegate: ReadOnlyListDataBinding<T>,
    private val mapper: (T) -> U
) : ReadOnlyListDataBinding<U> {
    override val size: Int
        get() = delegate.size

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun contains(element: U): Boolean = delegate.any { mapper(it) == element }

    override fun iterator(): Iterator<U> = delegate.stream().map { mapper(it) }.iterator()

    override fun containsAll(elements: Collection<U>): Boolean = elements.all { contains(it) }

    override fun get(index: Int): U = mapper(delegate[index])

    override fun indexOf(element: U): Int = delegate.indexOfFirst { mapper(it) == element }

    override fun lastIndexOf(element: U): Int = delegate.indexOfLast { mapper(it) == element }

    override fun listIterator(): ListIterator<U> = throw UnsupportedOperationException("Not yet implemented")

    override fun listIterator(index: Int): ListIterator<U> = throw UnsupportedOperationException("Not yet implemented")

    override fun subList(fromIndex: Int, toIndex: Int): List<U> =
        throw UnsupportedOperationException("Not yet implemented")

    override fun addListener(listener: (ListDataBindingEvent<U>) -> Unit) {
        delegate.addListener {
            val mappedEvent = it.mapped(mapper)
            if (mappedEvent is ValueUpdatedListDataBindingEvent) {
                if (mappedEvent.shouldEmit) {
                    listener(mappedEvent)
                }
            } else {
                listener(mappedEvent)
            }
        }
    }
}

private open class ReadOnlyListDataBindingImpl<T>(open val delegate: List<T>) :
    ReadOnlyListDataBinding<T> {
    protected val listeners = mutableListOf<(ListDataBindingEvent<T>) -> Unit>()

    override fun listIterator(): ListIterator<T> = delegate.listIterator()

    override fun listIterator(index: Int): ListIterator<T> = delegate.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        throw UnsupportedOperationException("Sublist is not supported")
    }

    override val size: Int
        get() = delegate.size

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun contains(element: T): Boolean = delegate.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = delegate.containsAll(elements)

    override fun get(index: Int): T = delegate[index]

    override fun indexOf(element: T): Int = delegate.indexOf(element)

    override fun lastIndexOf(element: T): Int = delegate.lastIndexOf(element)

    override fun iterator(): Iterator<T> = delegate.iterator()

    override fun addListener(listener: (ListDataBindingEvent<T>) -> Unit) {
        if (delegate is ReadOnlyListDataBinding<T>) {
            (delegate as ReadOnlyListDataBinding<T>).addListener(listener)
        } else {
            listeners.add(listener)
        }
    }
}

private class ReadWriteListDataBindingImpl<T>(val delegate: MutableList<T>) :
    ReadWriteListDataBinding<T>,
    ReadOnlyListDataBinding<T> {
    private val listeners = mutableListOf<(ListDataBindingEvent<T>) -> Unit>()
    override fun add(element: T): Boolean {
        val retValue = delegate.add(element)
        if (retValue) {
            listeners.forEach { it(ListDataBindingEvent(size - 1, element, ListDataBindingEventType.ADD)) }
        }
        return retValue
    }

    override fun remove(element: T): Boolean {
        val index = delegate.indexOf(element)
        val retValue = delegate.remove(element)
        if (retValue) {
            listeners.forEach {
                it(ListDataBindingEvent(index, element, ListDataBindingEventType.REMOVE))
            }
        }
        return retValue
    }

    override fun addAll(elements: Collection<T>): Boolean = elements.map { add(it) }.any { it }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val retValue = delegate.addAll(index, elements)
        if (retValue) {
            elements.forEachIndexed { elemIndex, element ->
                listeners.forEach { it(ListDataBindingEvent(index + elemIndex, element, ListDataBindingEventType.ADD)) }
            }
        }
        return retValue
    }

    override fun removeAll(elements: Collection<T>): Boolean = elements.map { remove(it) }.any { it }

    override fun retainAll(elements: Collection<T>): Boolean {
        return delegate.removeAll(delegate.filter {
            !elements.contains(it)
        })
    }

    override fun clear() {
        while (delegate.isNotEmpty()) {
            removeAt(0)
        }
    }

    override fun set(index: Int, element: T): T {
        val retValue = delegate.set(index, element)
        listeners.forEach { it(ValueUpdatedListDataBindingEvent(index, element, retValue)) }
        return retValue
    }

    override fun add(index: Int, element: T) {
        val retValue = delegate.add(index, element)
        listeners.forEach { it(ListDataBindingEvent(index, element, ListDataBindingEventType.ADD)) }
        return retValue
    }

    override fun removeAt(index: Int): T {
        val retValue = delegate.removeAt(index)
        listeners.forEach { it(ListDataBindingEvent(index, retValue, ListDataBindingEventType.REMOVE)) }
        return retValue
    }

    override fun listIterator(): MutableListIterator<T> = delegate.listIterator()

    override fun listIterator(index: Int): MutableListIterator<T> = delegate.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        throw UnsupportedOperationException("Sublist is not supported")
    }

    override val size: Int
        get() = delegate.size

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun contains(element: T): Boolean = delegate.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = delegate.containsAll(elements)

    override fun get(index: Int): T = delegate[index]

    override fun indexOf(element: T): Int = delegate.indexOf(element)

    override fun lastIndexOf(element: T): Int = delegate.lastIndexOf(element)

    override fun iterator(): MutableIterator<T> = delegate.iterator()

    override fun addListener(listener: (ListDataBindingEvent<T>) -> Unit) {
        listeners.add(listener)
    }
}