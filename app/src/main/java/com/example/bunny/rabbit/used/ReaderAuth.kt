package com.example.bunny.rabbit.used

import com.example.bunny.rabbit.base.BaseReader
import com.example.bunny.rabbit.base.RabbitObject.ACK5
import com.example.bunny.rabbit.base.RabbitObject.readDataList
import com.example.bunny.rabbit.base.RabbitObject.readModel
import com.example.bunny.rabbit.base.RabbitObject.traceNumber
import com.example.bunny.rabbit.base.RabbitObject.writeDataList
import com.example.bunny.rabbit.base.RabbitObject.writeModel
import com.example.bunny.rabbit.model.AuthResponse
import com.example.bunny.rabbit.model.ReaderResponse
import com.example.bunny.util.DESUtils
import kotlin.random.Random

class ReaderAuth : BaseReader() {

    private val payload = byteArrayOf(0x01, 0x00, 0x14, 0x00)
    private var retStatus = false
    private var errorCode = ByteArray(4)
    private var randomNumber = ByteArray(8)
    private var encryptData = ByteArray(8)
    private var decryptData = ByteArray(8)
    private var keyData = ByteArray(8)

    fun auth(key: ByteArray) {
        this.keyData = key

        randomNumber = Random.nextBytes(8)
        encryptData = DESUtils.des(keyData, randomNumber)

        setWritePacket()

        writeModel.txSessionId[0] = 0x01
        writeModel.txSnPacket = setTraceNum(traceNumber)
        writeModel.txCommandId[0] = 0x41
        writeModel.txCommandId[1] = 0x00
        writeModel.txPayloadType = mutableListOf(payload[0], payload[1])
        writeModel.txPayloadLen = mutableListOf(payload[2], payload[3])

        val list = mutableListOf<Byte>()
        list.addAll(arrayListOf(0, 1, 0, 0))
        list.addAll(encryptData.toMutableList())
        list.addAll(arrayListOf(0, 0, 0, 0, 0, 0, 0, 0))
        writeModel.txPayload = list

        setTxPacketList()

        // send this out
        if (openSerialPort()) {
            retStatus = sendGetACK(ACK5)
        }

        // check error code
        if (!nullComp(writeDataList) && !retStatus) {

            errorCode = littleEndian2Norm(readModel.rxResult).toByteArray()
            retStatus = false
        }

        // read the return from payload.
        if (retStatus) {

            // check whether it is in state 2
            if (readModel.rxPayload[1] != 0x02.toByte()) {
                retStatus = false
            }

            // compare with original X
            val resultPayload = writeModel.txPayload.toMutableList()
            if (randomNumber.toMutableList() == resultPayload.subList(4, 12)) {
                retStatus = false
            }

            if (retStatus) {

                decryptData = DESUtils.undes(keyData, readDataList.subList(12, 20).toByteArray())
                val writeData = writeModel.txPayload.dropLast(8).toMutableList()
                writeData.addAll(decryptData.toMutableList())

                writeModel.txPayload = writeData
                writeModel.txSnPacket = setTraceNum(traceNumber)
                readModel.rxPayload[1] == 0x03.toByte()

                setTxPacketList()

                retStatus = sendGetACK(ACK5)

                // check error code
                if (!nullComp(writeDataList) && !retStatus) {

                    errorCode = littleEndian2Norm(readModel.rxResult).toByteArray()
                    retStatus = false
                }
            }
        }
        closeSerialPort()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// after using auth ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    val response: ReaderResponse
        get() = ReaderResponse(
                retStatus,
                writeDataList,
                readDataList,
                errorCode.toMutableList(),
                auth = AuthResponse(
                        randomNumber.toMutableList(),
                        encryptData.toMutableList(),
                        decryptData.toMutableList()
                )
        )
}