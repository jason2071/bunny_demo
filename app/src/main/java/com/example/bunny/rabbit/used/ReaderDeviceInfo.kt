package com.example.bunny.rabbit.used

import com.example.bunny.rabbit.base.BaseReader
import com.example.bunny.rabbit.base.RabbitObject
import com.example.bunny.rabbit.base.RabbitObject.ACK5
import com.example.bunny.rabbit.base.RabbitObject.readDataList
import com.example.bunny.rabbit.base.RabbitObject.readModel
import com.example.bunny.rabbit.base.RabbitObject.writeDataList
import com.example.bunny.rabbit.base.RabbitObject.writeModel
import com.example.bunny.rabbit.model.InfoResponse
import com.example.bunny.rabbit.model.ReaderResponse

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
        writeModel.txPayloadType = mutableListOf(payload[0], payload[1])
        writeModel.txPayloadLen = mutableListOf(payload[2], payload[3])

        setTxPacketList()

        if (openSerialPort()) {
            retStatus = sendGetACK(ACK5)
        }

        // check error code
        if (!nullComp(writeDataList) && !retStatus) {
            errorCode = littleEndian2Norm(readModel.rxResult).toByteArray()
            retStatus = false
        }

        if (retStatus) {
            val data = readModel.rxPayload.toMutableList()
            deviceID4 = littleEndian2Norm(data.subList(0, 4)).toByteArray()
            merchID8 = littleEndian2Norm(data.subList(4, 12)).toByteArray()
            firmVer4 = littleEndian2Norm(data.subList(12, 16)).toByteArray()
            appVer4 = littleEndian2Norm(data.subList(16, 20)).toByteArray()
            sAMVer4 = littleEndian2Norm(data.subList(20, 24)).toByteArray()
            pollTO4 = littleEndian2Norm(data.subList(24, 28)).toByteArray()
            authTO4 = littleEndian2Norm(data.subList(28, 32)).toByteArray()

        }
        closeSerialPort()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// after using devInfo //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    val response: ReaderResponse
        get() = ReaderResponse(
                retStatus,
                writeDataList,
                readDataList,
                errorCode.toMutableList(),
                InfoResponse(
                        deviceID4.toMutableList(),
                        merchID8.toMutableList(),
                        firmVer4.toMutableList(),
                        appVer4.toMutableList(),
                        sAMVer4.toMutableList(),
                        pollTO4.toMutableList(),
                        authTO4.toMutableList()
                )
        )
}