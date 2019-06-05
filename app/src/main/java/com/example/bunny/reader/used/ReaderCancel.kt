package com.example.bunny.reader.used

import com.example.bunny.reader.base.BaseReader
import com.example.bunny.reader.base.Reader

class ReaderCancel : BaseReader() {

    fun cancel() {

        setTxPacket()
        val payload = arrayListOf(0x01, 0x00, 0x04, 0x00)

        Reader.txFormat.TX_SessionID[0] = 0x01

        Reader.txFormat.TX_SN_PACKET = setTraceNum(Reader.sequenceNumber)

        Reader.txFormat.TX_CMDID[0] = 0xAA.toByte()
        Reader.txFormat.TX_CMDID[1] = 0x00.toByte()

        Reader.txFormat.TX_PayloadType[0] = payload[0].toByte()
        Reader.txFormat.TX_PayloadType[1] = payload[1].toByte()

        Reader.txFormat.TX_PayloadLen[0] = payload[2].toByte()
        Reader.txFormat.TX_PayloadLen[1] = payload[3].toByte()

        Reader.txFormat.TX_Payload = arrayListOf(0x00, 0x00, 0x00, 0x00)

        setTxPacketList(Reader.txFormat)

        if (openSerialPort()) {
            //sendGetACK(Flow.ACK4)

            unPackRxMsgRec(uFlow)
        }
        closeSerialPort()
    }
}