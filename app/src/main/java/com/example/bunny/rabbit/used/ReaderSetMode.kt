package com.example.bunny.rabbit.used

import com.example.bunny.rabbit.base.BaseReader
import com.example.bunny.rabbit.base.RabbitObject.ACK5
import com.example.bunny.rabbit.base.RabbitObject.readDataList
import com.example.bunny.rabbit.base.RabbitObject.readModel
import com.example.bunny.rabbit.base.RabbitObject.traceNumber
import com.example.bunny.rabbit.base.RabbitObject.writeDataList
import com.example.bunny.rabbit.base.RabbitObject.writeModel
import com.example.bunny.rabbit.model.ModeResponse
import com.example.bunny.rabbit.model.ReaderResponse

class ReaderSetMode : BaseReader() {

    private val payload = byteArrayOf(0xF1.toByte(), 0x12, 0x01, 0x00)
    private var retStatus = false
    private var errorCode = ByteArray(4)
    private var mode1: Byte = 0x00

    fun setMode(mode1: Byte) {
        this.mode1 = mode1

        setWritePacket()

        writeModel.txSessionId[0] = 0x01
        writeModel.txSnPacket = setTraceNum(traceNumber)
        writeModel.txCommandId[0] = 0x65
        writeModel.txCommandId[1] = 0x00
        writeModel.txPayloadType = mutableListOf(payload[0], payload[1])
        writeModel.txPayloadLen = mutableListOf(payload[2], payload[3])
        writeModel.txPayload[0] = mode1

        setTxPacketList()

        if (openSerialPort()) {
            retStatus = sendGetACK(ACK5)
        }

        // check error code
        if (!nullComp(writeDataList) && !retStatus) {
            errorCode = littleEndian2Norm(readModel.rxResult).toByteArray()
            retStatus = false
        }

        closeSerialPort()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// after using setMode ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    val response: ReaderResponse
        get() = ReaderResponse(
                retStatus
                , writeDataList
                , readDataList
                , errorCode.toMutableList()
                , mode = ModeResponse(mode1)
        )
}