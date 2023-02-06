package com.mlytics.mlysdk.util

//import Foundation
interface Deque<Element> {
    var first: Element?
    var last: Element?
    var size: Int
    var isEmpty: Boolean
    fun append(element: Element)
    fun insert(element: Element)
    fun removeFirst(): Element?
    fun removeLast(): Element?
    fun removeAll()
}

class DoubleDequeItem<Element> {
    var prev: DoubleDequeItem<Element>? = null
    var next: DoubleDequeItem<Element>? = null
    var value: Element?

    constructor (value: Element? = null) {
        this.value = value
    }

}

class Queue<Element> : Deque<Element> {

    override var size: Int = 0
    var firstItem: DoubleDequeItem<Element>? = null
    var lastItem: DoubleDequeItem<Element>? = null
    override var isEmpty: Boolean = true
        get() = this.firstItem == null
    override var first: Element? = null
        get() {
            return this.firstItem?.value
        }

    override var last: Element? = null
        get() {
            return this.lastItem?.value
        }

    constructor (array: MutableList<Element>? = null) {
        this.append(array)
    }

    fun append(array: List<Element>?) {
        array?.forEach {
            this.append(it)
        }
    }

    override fun append(element: Element) {
        val current = DoubleDequeItem<Element>(element)
        val last = this.lastItem
        if (last != null) {
            last.next = current
            current.prev = last
        } else {
            this.lastItem = current
            this.firstItem = current
        }

        this.size += 1
    }

    override fun insert(element: Element) {
        val current = DoubleDequeItem<Element>(element)
        val first = this.firstItem
        if (first != null) {
            first.prev = current
            current.next = first
        } else {
            this.lastItem = current
            this.firstItem = current
        }

        this.size += 1
    }

    override fun removeFirst(): Element? {
        val value = this.firstItem?.value
        if (value != null) else {
            return null
        }

        this.firstItem = this.firstItem?.next
        this.size -= 1
        return value
    }

    override fun removeLast(): Element? {
        val value = this.lastItem?.value
        if (value != null) else {
            return null
        }

        this.lastItem = this.lastItem?.prev
        this.size -= 1
        return value
    }

    override fun removeAll() {
        this.firstItem = null
        this.lastItem = null
        this.size = 0
    }

}
