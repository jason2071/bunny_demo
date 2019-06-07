package com.example.bunny.rabbit.model

data class WriteModel(
        var txStart: ByteArray
        , var txVersion: ByteArray
        , var txSessionId: ByteArray
        , var txMessageType: Byte
        , var txSnPacket: ByteArray
        , var txSnCurrent: ByteArray
        , var txSnTotal: ByteArray
        , var txCommandId: ByteArray
        , var txStatus: ByteArray
        , var txPayloadType: ByteArray
        , var txPayloadLen: ByteArray
        , var txPayload: ByteArray
        , var txCheckSum: ByteArray
        , var txStop: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WriteModel

        if (!txStart.contentEquals(other.txStart)) return false
        if (!txVersion.contentEquals(other.txVersion)) return false
        if (!txSessionId.contentEquals(other.txSessionId)) return false
        if (txMessageType != other.txMessageType) return false
        if (!txSnPacket.contentEquals(other.txSnPacket)) return false
        if (!txSnCurrent.contentEquals(other.txSnCurrent)) return false
        if (!txSnTotal.contentEquals(other.txSnTotal)) return false
        if (!txCommandId.contentEquals(other.txCommandId)) return false
        if (!txStatus.contentEquals(other.txStatus)) return false
        if (!txPayloadType.contentEquals(other.txPayloadType)) return false
        if (!txPayloadLen.contentEquals(other.txPayloadLen)) return false
        if (!txPayload.contentEquals(other.txPayload)) return false
        if (!txCheckSum.contentEquals(other.txCheckSum)) return false
        if (!txStop.contentEquals(other.txStop)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = txStart.contentHashCode()
        result = 31 * result + txVersion.contentHashCode()
        result = 31 * result + txSessionId.contentHashCode()
        result = 31 * result + txMessageType
        result = 31 * result + txSnPacket.contentHashCode()
        result = 31 * result + txSnCurrent.contentHashCode()
        result = 31 * result + txSnTotal.contentHashCode()
        result = 31 * result + txCommandId.contentHashCode()
        result = 31 * result + txStatus.contentHashCode()
        result = 31 * result + txPayloadType.contentHashCode()
        result = 31 * result + txPayloadLen.contentHashCode()
        result = 31 * result + txPayload.contentHashCode()
        result = 31 * result + txCheckSum.contentHashCode()
        result = 31 * result + txStop.contentHashCode()
        return result
    }
}