package com.example.bunny.rabbit.used

import com.example.bunny.rabbit.base.BaseReader
import com.example.bunny.rabbit.base.RabbitObject
import com.example.bunny.rabbit.base.RabbitObject.ACK4
import com.example.bunny.rabbit.base.RabbitObject.ACK5
import com.example.bunny.rabbit.base.RabbitObject.readDataList
import com.example.bunny.rabbit.base.RabbitObject.readModel
import com.example.bunny.rabbit.base.RabbitObject.writeDataList
import com.example.bunny.rabbit.base.RabbitObject.writeModel
import com.example.bunny.rabbit.model.BaseResponse
import com.example.bunny.util.LogUtil

class ReaderDeviceInfo : BaseReader() {

    private val payload = byteArrayOf(0xF1.toByte(), 0x12, 0x00, 0x00)
    private var retStatus = false
    private var errorCode = ByteArray(4)

    private var deviceID4 = ByteArray(4)
    private var merchID8 = ByteArray(8)
    private var firmVer4 = ByteArray(4)
    private var appVer4 = ByteArray(4)
    private var sAMVer4 = ByteArray(4)
    private var pollTO4 = ByteArray(4)
    private var authTO4 = ByteArray(4)

    fun devInfo() {

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
        }

        // check error code
        if (!nullComp(writeDataList) && !retStatus) {
            errorCode = littleEndian2Norm(readModel.rxResult)
            retStatus = false
        }

        if (retStatus) {
            val data = readModel.rxPayload.toMutableList()
            deviceID4 = littleEndian2Norm(data.subList(0, 4).toByteArray())
            merchID8 = littleEndian2Norm(data.subList(4, 12).toByteArray())
            firmVer4 = littleEndian2Norm(data.subList(12, 16).toByteArray())
            appVer4 = littleEndian2Norm(data.subList(16, 20).toByteArray())
            sAMVer4 = littleEndian2Norm(data.subList(20, 24).toByteArray())
            pollTO4 = littleEndian2Norm(data.subList(24, 28).toByteArray())
            authTO4 = littleEndian2Norm(data.subList(28, 32).toByteArray())

        }
        closeSerialPort()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// after using devInfo //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    val response: InfoResponse
        get() {
            val items = InfoResponse()
            items.status = retStatus
            items.errorCode = errorCode
            items.writeDataList = writeDataList
            items.readDataList = readDataList
            items.deviceID4 = deviceID4
            items.merchID8 = merchID8
            items.firmVer4 = firmVer4
            items.appVer4 = appVer4
            items.sAMVer4 = sAMVer4
            items.pollTO4 = pollTO4
            items.authTO4 = authTO4
            return items
        }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// data class response //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    class InfoResponse : BaseResponse() {
        var deviceID4 = ByteArray(4)
        var merchID8 = ByteArray(8)
        var firmVer4 = ByteArray(4)
        var appVer4 = ByteArray(4)
        var sAMVer4 = ByteArray(4)
        var pollTO4 = ByteArray(4)
        var authTO4 = ByteArray(4)
    }

}