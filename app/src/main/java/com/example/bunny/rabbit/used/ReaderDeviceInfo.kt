package com.example.bunny.rabbit.used

import com.example.bunny.rabbit.base.BaseReader
import com.example.bunny.rabbit.base.RabbitObject
import com.example.bunny.rabbit.base.RabbitObject.ACK4
import com.example.bunny.rabbit.base.RabbitObject.ACK5
import com.example.bunny.rabbit.base.RabbitObject.readModel
import com.example.bunny.rabbit.base.RabbitObject.writeDataList
import com.example.bunny.rabbit.base.RabbitObject.writeModel
import com.example.bunny.util.LogUtil

class ReaderDeviceInfo : BaseReader() {

    private val payload: ArrayList<Byte> = arrayListOf(0xF1.toByte(), 0x12, 0x00, 0x00)
    private var retStatus = false
    private var errorCode: ByteArray = byteArrayOf(0, 0, 0, 0)

    fun devInfo(): Boolean {

        setWritePacket()

        writeModel.txSessionId[0] = 0x01
        writeModel.txSnPacket = setTraceNum(RabbitObject.traceNumber)
        writeModel.txCommandId[0] = 0x62
        writeModel.txCommandId[1] = 0x00
        writeModel.txPayloadType = byteArrayOf(payload[0], payload[1])
        writeModel.txPayloadLen = byteArrayOf(payload[2], payload[3])

        setTxPacketList()

        if (openSerialPort()) {
            retStatus = sendGetACK(ACK5)
            LogUtil.log("devInfo : $retStatus")
        }

        // check error code
        if (!nullComp(writeDataList) && !retStatus) {

            errorCode = littleEndian2Norm(readModel.rxResult)
            retStatus = false
            LogUtil.log("errorCode : $errorCode")
        }

        if (retStatus) {

            val data = writeModel.txPayload.toMutableList()
            val deviceID4 = littleEndian2Norm(data.subList(0, 4).toByteArray())
            val merchID8 = littleEndian2Norm(data.subList(4, 12).toByteArray())
            val firmVer4 = littleEndian2Norm(data.subList(12, 16).toByteArray())
            val appVer4 = littleEndian2Norm(data.subList(16, 20).toByteArray())
            val sAMVer4 = littleEndian2Norm(data.subList(20, 24).toByteArray())
            val pollTO4 = littleEndian2Norm(data.subList(24, 28).toByteArray())
            val authTO4 = littleEndian2Norm(data.subList(28, 32).toByteArray())

        }

        closeSerialPort()
        return retStatus

    }
}