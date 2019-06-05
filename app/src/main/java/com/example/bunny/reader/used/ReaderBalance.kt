package com.example.bunny.reader.used

import com.example.bunny.reader.base.BaseReader
import com.example.bunny.reader.base.Reader

class ReaderBalance: BaseReader() {

    fun balance() {
        setTxPacket()
        setTraceNum(Reader.sequenceNumber)
        val payload = arrayListOf(0x01, 0x00, 0x04, 0x00)
    }
}