package com.example.bunny.rabbit.used

import com.example.bunny.rabbit.base.BaseReader
import com.example.bunny.rabbit.base.RabbitObject.ACK4
import com.example.bunny.rabbit.base.RabbitObject.readDataList
import com.example.bunny.rabbit.base.RabbitObject.traceNumber
import com.example.bunny.rabbit.base.RabbitObject.writeDataList
import com.example.bunny.rabbit.base.RabbitObject.writeModel
import com.example.bunny.rabbit.model.BaseResponse

class ReaderCancel : BaseReader() {

    private val payload = byteArrayOf(0x01, 0x00, 0x04, 0x00)
    private var retStatus = false

    fun cancel() {
        setWritePacket()

        writeModel.txSessionId[0] = 0x01
        writeModel.txSnPacket = setTraceNum(traceNumber)
        writeModel.txCommandId[0] = 0xAA.toByte()
        writeModel.txCommandId[1] = 0x00
        writeModel.txPayloadType = byteArrayOf(payload[0], payload[1])
        writeModel.txPayloadLen = byteArrayOf(payload[2], payload[3])
        writeModel.txPayload = byteArrayOf(0x00, 0x01, 0x00, 0x00)

        setTxPacketList()

        if (openSerialPort()) {
            retStatus = sendGetACK(ACK4)
        }

        closeSerialPort()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// after using cancel //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    val response: BaseResponse
        get() {
            val items = BaseResponse()
            items.status = retStatus
            items.writeDataList = writeDataList
            items.readDataList = readDataList
            return items
        }

}