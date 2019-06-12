package com.example.bunny.rabbit.used

import com.example.bunny.rabbit.base.BaseReader
import com.example.bunny.rabbit.base.RabbitObject.ACK4
import com.example.bunny.rabbit.base.RabbitObject.readDataList
import com.example.bunny.rabbit.base.RabbitObject.readModel
import com.example.bunny.rabbit.base.RabbitObject.traceNumber
import com.example.bunny.rabbit.base.RabbitObject.writeDataList
import com.example.bunny.rabbit.base.RabbitObject.writeModel
import com.example.bunny.rabbit.model.ReaderResponse
import com.example.bunny.rabbit.model.TimeResponse

class ReaderSyncTime : BaseReader() {

    private val payload = byteArrayOf(0xF1.toByte(), 0x12, 0x07, 0x00)
    private var retStatus = false
    private var errorCode = ByteArray(4)
    private var timeData = ByteArray(7)

    fun syncTime(readerTime7: ByteArray) {
        this.timeData = readerTime7

        setWritePacket()
        writeModel.txSessionId[0] = 0x01
        writeModel.txSnPacket = setTraceNum(traceNumber)
        writeModel.txCommandId[0] = 0x67
        writeModel.txCommandId[1] = 0x00
        writeModel.txPayloadType = mutableListOf(payload[0], payload[1])
        writeModel.txPayloadLen = mutableListOf(payload[2], payload[3])
        writeModel.txPayload = timeData.toMutableList()

        if (openSerialPort()) {
            retStatus = sendGetACK(ACK4)
        }

        // check error code
        if (!nullComp(writeDataList) && !retStatus) {
            errorCode = littleEndian2Norm(readModel.rxResult).toByteArray()
            retStatus = false
        }

        closeSerialPort()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// after using syncTime ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    val response: ReaderResponse
        get() = ReaderResponse(
                retStatus
                , writeDataList
                , readDataList
                , errorCode.toMutableList()
                , time = TimeResponse(timeData.toMutableList())
        )
}