package com.example.bunny.rabbit.used

import com.example.bunny.rabbit.base.BaseReader
import com.example.bunny.rabbit.base.RabbitObject.ACK4
import com.example.bunny.rabbit.base.RabbitObject.traceNumber
import com.example.bunny.rabbit.base.RabbitObject.writeDataList
import com.example.bunny.rabbit.base.RabbitObject.writeModel
import com.example.bunny.util.LogUtil

class ReaderCancel : BaseReader() {

    fun cancel(): Boolean {

        val payload: ArrayList<Byte> = arrayListOf(0x01, 0x00, 0x04, 0x00)
        var retStatus = false

        setWritePacket()

        writeModel.txSessionId[0] = 0x01
        writeModel.txSnPacket = setTraceNum(traceNumber)
        writeModel.txCommandId[0] = 0xAA.toByte()
        writeModel.txCommandId[1] = 0x00

        writeModel.txPayloadType = byteArrayOf(payload[0], payload[1])
        writeModel.txPayloadLen = byteArrayOf(payload[2], payload[3])
        writeModel.txPayload = byteArrayOf(0x00, 0x00, 0x00, 0x00)



        if (openSerialPort()) {
            retStatus = sendGetACK(ACK4)
            LogUtil.log("cancel : $retStatus")
        }
        closeSerialPort()
        return retStatus
    }
}