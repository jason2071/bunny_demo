package com.example.bunny.rabbit.model

import android.os.Parcel
import android.os.Parcelable

data class WriteModel(
        var txStart: MutableList<Byte> = arrayListOf(0x10, 0x02)
        , var txVersion: MutableList<Byte> = arrayListOf(0, 0)
        , var txSessionId: MutableList<Byte> = arrayListOf(0, 0, 0, 0)
        , var txMessageType: Byte = 0
        , var txSnPacket: MutableList<Byte> = arrayListOf(0, 0)
        , var txSnCurrent: MutableList<Byte> = arrayListOf(0, 0)
        , var txSnTotal: MutableList<Byte> = arrayListOf(0, 0)
        , var txCommandId: MutableList<Byte> = arrayListOf(0, 0)
        , var txStatus: MutableList<Byte> = arrayListOf(0, 0, 0, 0)
        , var txPayloadType: MutableList<Byte> = arrayListOf(0, 0)
        , var txPayloadLen: MutableList<Byte> = arrayListOf(0, 0)
        , var txPayload: MutableList<Byte> = arrayListOf()
        , var txCheckSum: MutableList<Byte> = arrayListOf(0, 0)
        , var txStop: MutableList<Byte> = arrayListOf(0x10, 0x03)
)