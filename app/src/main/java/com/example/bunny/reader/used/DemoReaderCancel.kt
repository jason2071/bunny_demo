package com.example.bunny.reader.used

import com.example.bunny.reader.base.BaseDemoReader
import com.example.bunny.reader.base.Flow
import com.example.bunny.reader.base.Reader
import com.example.bunny.util.LogUtil

class DemoReaderCancel : BaseDemoReader() {

    fun cancel(): Boolean {

        val payload = arrayListOf(0x01.toByte(), 0x00.toByte(), 0x04.toByte(), 0x00.toByte())
        var retStatus = false

        setTxPacket()

        Reader.txFormat.TX_SessionID[0] = 0x01

        Reader.txFormat.TX_SN_PACKET = setTraceNum(Reader.termStan)

        Reader.txFormat.TX_CMDID[0] = 0xAA.toByte()
        Reader.txFormat.TX_CMDID[1] = 0x00.toByte()

        Reader.txFormat.TX_PayloadType[0] = payload[0]
        Reader.txFormat.TX_PayloadType[1] = payload[1]

        Reader.txFormat.TX_PayloadLen[0] = payload[2]
        Reader.txFormat.TX_PayloadLen[1] = payload[3]

        Reader.txFormat.TX_Payload = arrayListOf(0x00, 0x00, 0x00, 0x00)

        setTxPacketList(Reader.txFormat)

        if (openSerialPort()) {
            retStatus = sendGetACK(Flow.ACK4)
            LogUtil.log("cancel : $retStatus")
        }
        closeSerialPort()
        return retStatus
    }
}