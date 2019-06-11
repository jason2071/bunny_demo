package com.example.bunny.rabbit.used

import com.example.bunny.rabbit.base.BaseReader
import com.example.bunny.rabbit.base.RabbitObject.ACK5
import com.example.bunny.rabbit.base.RabbitObject.readDataList
import com.example.bunny.rabbit.base.RabbitObject.readModel
import com.example.bunny.rabbit.base.RabbitObject.traceNumber
import com.example.bunny.rabbit.base.RabbitObject.writeDataList
import com.example.bunny.rabbit.base.RabbitObject.writeModel
import com.example.bunny.rabbit.model.BaseResponse

class ReaderGetMode : BaseReader() {

    private val payload = byteArrayOf(0xF1.toByte(), 0x12, 0x00, 0x00)
    private var retStatus = false
    private var errorCode = ByteArray(4)
    private var mode1: Byte = 0x00

    fun getMode() {
        setWritePacket()

        writeModel.txSessionId[0] = 0x01
        writeModel.txSnPacket = setTraceNum(traceNumber)
        writeModel.txCommandId[0] = 0x64
        writeModel.txCommandId[1] = 0x00
        writeModel.txPayloadType = byteArrayOf(payload[0], payload[1])
        writeModel.txPayloadLen = byteArrayOf(payload[2], payload[3])

        setTxPacketList()

        if (openSerialPort()) {
            retStatus = sendGetACK(ACK5)
        }

        // check error code
        if (!nullComp(writeDataList) && !retStatus) {
            errorCode = littleEndian2Norm(readModel.rxResult)
            retStatus = false
        }

        // read the return from payload.
        if (retStatus) {
            mode1 = readModel.rxPayload[0]
        }

        closeSerialPort()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// after using getMode ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    val response: GetModeResponse
        get() {
            val items = GetModeResponse()
            items.status = retStatus
            items.errorCode = errorCode
            items.writeDataList = writeDataList
            items.readDataList = readDataList
            items.mode1 = mode1
            return items
        }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// data class response //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    class GetModeResponse : BaseResponse() {
        var mode1: Byte = 0x00
    }
}