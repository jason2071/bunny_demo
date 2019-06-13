package com.example.bunny.rabbit.model

import android.os.Parcel
import android.os.Parcelable

data class ReadModel(
        var rxStart: MutableList<Byte> = arrayListOf(0x10, 0x02)
        , var rxVersion: MutableList<Byte> = arrayListOf(0, 0)
        , var rxSessionId: MutableList<Byte> = arrayListOf(0, 0, 0, 0)
        , var rxMessageType: Byte = 0
        , var rxSnPacket: MutableList<Byte> = arrayListOf(0, 0)
        , var rxSnCurrent: MutableList<Byte> = arrayListOf(0, 0)
        , var rxSnTotal: MutableList<Byte> = arrayListOf(0, 0)
        , var rxCommandId: MutableList<Byte> = arrayListOf(0, 0)
        , var rxResult: MutableList<Byte> = arrayListOf(0, 0, 0, 0)
        , var rxPayloadType: MutableList<Byte> = arrayListOf(0, 0)
        , var rxPayloadLen: MutableList<Byte> = arrayListOf(0, 0)
        , var rxPayload: MutableList<Byte> = arrayListOf()
        , var rxChecksum: MutableList<Byte> = arrayListOf(0, 0)
        , var rxStop: MutableList<Byte> = arrayListOf(0x10, 0x03)
)