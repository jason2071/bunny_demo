package com.example.bunny.rabbit.model

data class ReadModel(
        var rxStart: ByteArray
        , var rxVersion: ByteArray
        , var rxSessionId: ByteArray
        , var rxMessageType: Byte
        , var rxSnPacket: ByteArray
        , var rxSnCurrent: ByteArray
        , var rxSnTotal: ByteArray
        , var rxCommandId: ByteArray
        , var rxResult: ByteArray
        , var rxPayloadType: ByteArray
        , var rxPayloadLen: ByteArray
        , var rxPayload: ByteArray
        , var rxChecksum: ByteArray
        , var rxStop: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReadModel

        if (!rxStart.contentEquals(other.rxStart)) return false
        if (!rxVersion.contentEquals(other.rxVersion)) return false
        if (!rxSessionId.contentEquals(other.rxSessionId)) return false
        if (rxMessageType != other.rxMessageType) return false
        if (!rxSnPacket.contentEquals(other.rxSnPacket)) return false
        if (!rxSnCurrent.contentEquals(other.rxSnCurrent)) return false
        if (!rxSnTotal.contentEquals(other.rxSnTotal)) return false
        if (!rxCommandId.contentEquals(other.rxCommandId)) return false
        if (!rxResult.contentEquals(other.rxResult)) return false
        if (!rxPayloadType.contentEquals(other.rxPayloadType)) return false
        if (!rxPayloadLen.contentEquals(other.rxPayloadLen)) return false
        if (!rxPayload.contentEquals(other.rxPayload)) return false
        if (!rxChecksum.contentEquals(other.rxChecksum)) return false
        if (!rxStop.contentEquals(other.rxStop)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rxStart.contentHashCode()
        result = 31 * result + rxVersion.contentHashCode()
        result = 31 * result + rxSessionId.contentHashCode()
        result = 31 * result + rxMessageType
        result = 31 * result + rxSnPacket.contentHashCode()
        result = 31 * result + rxSnCurrent.contentHashCode()
        result = 31 * result + rxSnTotal.contentHashCode()
        result = 31 * result + rxCommandId.contentHashCode()
        result = 31 * result + rxResult.contentHashCode()
        result = 31 * result + rxPayloadType.contentHashCode()
        result = 31 * result + rxPayloadLen.contentHashCode()
        result = 31 * result + rxPayload.contentHashCode()
        result = 31 * result + rxChecksum.contentHashCode()
        result = 31 * result + rxStop.contentHashCode()
        return result
    }
}