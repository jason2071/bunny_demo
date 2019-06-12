package com.example.bunny.rabbit.model

import android.os.Parcel
import android.os.Parcelable

data class WriteModel(
        var txStart: MutableList<Byte>
        , var txVersion: MutableList<Byte>
        , var txSessionId: MutableList<Byte>
        , var txMessageType: Byte
        , var txSnPacket: MutableList<Byte>
        , var txSnCurrent: MutableList<Byte>
        , var txSnTotal: MutableList<Byte>
        , var txCommandId: MutableList<Byte>
        , var txStatus: MutableList<Byte>
        , var txPayloadType: MutableList<Byte>
        , var txPayloadLen: MutableList<Byte>
        , var txPayload: MutableList<Byte>
        , var txCheckSum: MutableList<Byte>
        , var txStop: MutableList<Byte>
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
        writeList(txStart)
        writeList(txVersion)
        writeList(txSessionId)
        writeByte(txMessageType)
        writeList(txSnPacket)
        writeList(txSnCurrent)
        writeList(txSnTotal)
        writeList(txCommandId)
        writeList(txStatus)
        writeList(txPayloadType)
        writeList(txPayloadLen)
        writeList(txPayload)
        writeList(txCheckSum)
        writeList(txStop)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<WriteModel> = object : Parcelable.Creator<WriteModel> {
            override fun createFromParcel(source: Parcel): WriteModel = WriteModel(source)
            override fun newArray(size: Int): Array<WriteModel?> = arrayOfNulls(size)
        }
    }
}