package com.example.bunny.rabbit.model

import android.os.Parcel
import android.os.Parcelable

data class ReadModel(
        var rxStart: MutableList<Byte>
        , var rxVersion: MutableList<Byte>
        , var rxSessionId: MutableList<Byte>
        , var rxMessageType: Byte
        , var rxSnPacket: MutableList<Byte>
        , var rxSnCurrent: MutableList<Byte>
        , var rxSnTotal: MutableList<Byte>
        , var rxCommandId: MutableList<Byte>
        , var rxResult: MutableList<Byte>
        , var rxPayloadType: MutableList<Byte>
        , var rxPayloadLen: MutableList<Byte>
        , var rxPayload: MutableList<Byte>
        , var rxChecksum: MutableList<Byte>
        , var rxStop: MutableList<Byte>
) : Parcelable {
    constructor(source: Parcel) : this(
            ArrayList<Byte>().apply { source.readList(this, Byte::class.java.classLoader) },
            ArrayList<Byte>().apply { source.readList(this, Byte::class.java.classLoader) },
            ArrayList<Byte>().apply { source.readList(this, Byte::class.java.classLoader) },
            source.readByte(),
            ArrayList<Byte>().apply { source.readList(this, Byte::class.java.classLoader) },
            ArrayList<Byte>().apply { source.readList(this, Byte::class.java.classLoader) },
            ArrayList<Byte>().apply { source.readList(this, Byte::class.java.classLoader) },
            ArrayList<Byte>().apply { source.readList(this, Byte::class.java.classLoader) },
            ArrayList<Byte>().apply { source.readList(this, Byte::class.java.classLoader) },
            ArrayList<Byte>().apply { source.readList(this, Byte::class.java.classLoader) },
            ArrayList<Byte>().apply { source.readList(this, Byte::class.java.classLoader) },
            ArrayList<Byte>().apply { source.readList(this, Byte::class.java.classLoader) },
            ArrayList<Byte>().apply { source.readList(this, Byte::class.java.classLoader) },
            ArrayList<Byte>().apply { source.readList(this, Byte::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeList(rxStart)
        writeList(rxVersion)
        writeList(rxSessionId)
        writeByte(rxMessageType)
        writeList(rxSnPacket)
        writeList(rxSnCurrent)
        writeList(rxSnTotal)
        writeList(rxCommandId)
        writeList(rxResult)
        writeList(rxPayloadType)
        writeList(rxPayloadLen)
        writeList(rxPayload)
        writeList(rxChecksum)
        writeList(rxStop)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ReadModel> = object : Parcelable.Creator<ReadModel> {
            override fun createFromParcel(source: Parcel): ReadModel = ReadModel(source)
            override fun newArray(size: Int): Array<ReadModel?> = arrayOfNulls(size)
        }
    }
}