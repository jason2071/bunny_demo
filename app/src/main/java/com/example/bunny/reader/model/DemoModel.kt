package com.example.bunny.reader.model

data class DemoModel(
        var start: ByteArray
        , var version: ByteArray
        , var stop: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DemoModel

        if (!start.contentEquals(other.start)) return false
        if (!version.contentEquals(other.version)) return false
        if (!stop.contentEquals(other.stop)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.contentHashCode()
        result = 31 * result + version.contentHashCode()
        result = 31 * result + stop.contentHashCode()
        return result
    }
}