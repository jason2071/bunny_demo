package com.example.bunny.reader

import com.example.bunny.base.BaseReader
import com.example.bunny.base.Reader

class ReaderBalance: BaseReader() {

    fun balance() {
        setTxPacket()
        setTraceNum(Reader.sequenceNumber)
        val payload = arrayListOf(0x01, 0x00, 0x04, 0x00)
    }
}