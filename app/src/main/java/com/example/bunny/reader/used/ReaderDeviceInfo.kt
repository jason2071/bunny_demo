package com.example.bunny.reader.used

import com.example.bunny.reader.base.BaseReader
import com.example.bunny.reader.base.Reader

class ReaderDeviceInfo: BaseReader() {

    fun info() {
        setTxPacket()
        setTraceNum(Reader.sequenceNumber)
        val payload = arrayListOf(0x01, 0x00, 0x04, 0x00)
    }

}