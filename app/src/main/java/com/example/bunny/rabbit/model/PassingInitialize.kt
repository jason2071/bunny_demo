package com.example.bunny.rabbit.model

/**
 *  passing data to ReaderInitialize class
 */

data class PassingInitialize(
        var merchID8: ByteArray
        , var locateID4: ByteArray
        , var termID4: ByteArray
        , var serviceID4: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PassingInitialize

        if (!merchID8.contentEquals(other.merchID8)) return false
        if (!locateID4.contentEquals(other.locateID4)) return false
        if (!termID4.contentEquals(other.termID4)) return false
        if (!serviceID4.contentEquals(other.serviceID4)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = merchID8.contentHashCode()
        result = 31 * result + locateID4.contentHashCode()
        result = 31 * result + termID4.contentHashCode()
        result = 31 * result + serviceID4.contentHashCode()
        return result
    }
}