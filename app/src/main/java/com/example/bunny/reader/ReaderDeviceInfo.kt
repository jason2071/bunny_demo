package com.example.bunny.reader

import com.example.bunny.base.BaseReader
import com.example.bunny.base.Reader

class ReaderDeviceInfo: BaseReader() {

    fun info() {
        setTxPacket()
        setTraceNum(Reader.sequenceNumber)
        val payload = arrayListOf(0x01, 0x00, 0x04, 0x00)
    }

}