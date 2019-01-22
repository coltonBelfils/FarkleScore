package com.coalbell.farklescrore

import android.os.Parcel
import android.os.Parcelable

class HistoryStackGame() : Parcelable {
    private var head: Node<HistoryStackTurn>
    private var tail: Node<HistoryStackTurn>
    var size: Int = 0
        private set
    private var queueMark: Node<HistoryStackTurn>?
    private var queueMarkLength = 0

    var currentTotalScore = 0
        set(value) {
            if (value < 0 || value % 50 != 0) throw IllegalArgumentException("currentTotalScore not mod 50 or less than 0 at value: $field") else field = value
        }

    constructor(parcel: Parcel) : this() {
        val array = parcel.readParcelableArray(HistoryStackTurn::class.java.classLoader)
        if (array != null) for (i in array) {
            this.push(i as HistoryStackTurn)
        }

        this.size = parcel.readInt()
        this.queueMarkLength = parcel.readInt()

        this.resetQueue()
        for (i in 0 until this.queueMarkLength) {
            this.deQueue()
        }
    }

    private class Node<H>(var next: Node<H>?, var prev: Node<H>?, var data: H?)

    init {
        val temp = Node<HistoryStackTurn>(null, null, null)
        head = Node(temp, null, null)
        tail = Node(null, head, null)
        head.next = tail
        queueMark = tail
    }

    fun isEmpty() = this.head.next == this.tail

    fun clear() {
        this.head.next = this.tail
        this.tail.prev = this.head
        this.size = 0
    }

    fun push(data: HistoryStackTurn) {
        val nn = Node(this.head.next, this.head, data)
        this.head.next?.prev = nn
        this.head.next = nn
        this.size++
    }

    fun pop(): HistoryStackTurn? {
        if(!isEmpty()) {
            val temp = this.head.next?.data
            this.head = this.head.next!!
            this.head.data = null
            if (size > 0) this.size--
            return temp
        }
        return null
    }

    fun peek(): HistoryStackTurn? {
        if(!isEmpty()) {
            return this.head.next?.data
        }
        return null
    }

    fun deQueue(): HistoryStackTurn? {
        if (this.queueMark == this.head) {
            return null
        }
        if (queueMarkLength < size) queueMarkLength++
        queueMark = queueMark?.prev
        return queueMark?.data
    }

    fun resetQueue() {
        queueMarkLength = 0
        queueMark = tail
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        val tempMarker = queueMark
        this.resetQueue()

        val array = Array<HistoryStackTurn?>(size) { null }
        var histRead = this.deQueue()
        var index = 0
        while (histRead != null && index < size) {
            array[index] = histRead
            histRead = this.deQueue()
            index++
        }
        parcel.writeParcelableArray(array, 0)
        parcel.writeInt(this.size)
        parcel.writeInt(this.queueMarkLength)

        queueMark = tempMarker
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        val tempMarker = queueMark
        val str = StringBuilder().append("HistoryStackTurn: size==$size, isEmpty==${isEmpty()}, contents:\n")

        var histRead = this.deQueue()
        while (histRead != null) {
            str.append("$histRead\n")
            histRead = this.deQueue()
        }
        queueMark = tempMarker
        return str.append("\n").toString()
    }

    companion object CREATOR : Parcelable.Creator<HistoryStackTurn> {
        override fun createFromParcel(parcel: Parcel): HistoryStackTurn {
            return HistoryStackTurn(parcel)
        }

        override fun newArray(size: Int): Array<HistoryStackTurn?> {
            return arrayOfNulls(size)
        }
    }
}