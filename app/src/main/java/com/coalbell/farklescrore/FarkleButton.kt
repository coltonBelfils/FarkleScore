package com.coalbell.farklescrore

import android.os.Parcel
import android.os.Parcelable

data class FarkleButton(val id: Int, val textId: Int, val name: String, var value: Int, val diceNeeded: Int, var pressedCount: Int) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(textId)
        parcel.writeString(name)
        parcel.writeInt(value)
        parcel.writeInt(pressedCount)
        parcel.writeInt(diceNeeded)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FarkleButton> {

        override fun createFromParcel(parcel: Parcel): FarkleButton {
            return FarkleButton(parcel)
        }

        override fun newArray(size: Int): Array<FarkleButton?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            null -> false
            !is FarkleButton -> false
            else -> (other.id == this.id) && (other.value == this.value) && (other.diceNeeded == this.diceNeeded) && (other.pressedCount == this.pressedCount)
        }
    }

    override fun toString(): String {
        return "id: $id, value: $value, diceNeeded: $diceNeeded, pressedCount: $pressedCount"
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + textId
        result = 31 * result + name.hashCode()
        result = 31 * result + value
        result = 31 * result + diceNeeded
        result = 31 * result + pressedCount
        return result
    }
}