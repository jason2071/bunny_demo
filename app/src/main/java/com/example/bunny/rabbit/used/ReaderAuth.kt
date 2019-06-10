package com.example.bunny.rabbit.used

import com.example.bunny.rabbit.base.BaseReader
import com.example.bunny.rabbit.base.RabbitObject.ACK5
import com.example.bunny.rabbit.base.RabbitObject.readDataList
import com.example.bunny.rabbit.base.RabbitObject.readModel
import com.example.bunny.rabbit.base.RabbitObject.traceNumber
import com.example.bunny.rabbit.base.RabbitObject.writeDataList
import com.example.bunny.rabbit.base.RabbitObject.writeModel
import com.example.bunny.util.DESUtils
import com.example.bunny.util.LogUtil
import kotlin.random.Random

class ReaderAuth : BaseReader() {

    private val payload: ArrayList<Byte> = arrayListOf(0x01, 0x00, 0x14, 0x00)
    private var retStatus = false
    private var errorCode: ByteArray = byteArrayOf(0, 0, 0, 0)

    fun auth(key: ByteArray): Boolean {
        val random = Random.nextBytes(8)
        val encrypt = DESUtils.des(key, random)

        setWritePacket()

        writeModel.txSessionId[0] = 0x01
        writeModel.txSnPacket = setTraceNum(traceNumber)
        writeModel.txCommandId[0] = 0x41
        writeModel.txCommandId[1] = 0x00
        writeModel.txPayloadType = byteArrayOf(payload[0], payload[1])
        writeModel.txPayloadLen = byteArrayOf(payload[2], payload[3])

        val list = mutableListOf<Byte>()
        list.addAll(arrayListOf(0, 1, 0, 0))
        list.addAll(encrypt.toMutableList())
        list.addAll(arrayListOf(0, 0, 0, 0, 0, 0, 0, 0))
        writeModel.txPayload = list.toByteArray()

        setTxPacketList()

        // send this out
        if (openSerialPort()) {
            retStatus = sendGetACK(ACK5)
            LogUtil.log("auth : $retStatus")
        }

        // check error code
        if (!nullComp(writeDataList) && !retStatus) {

            errorCode = littleEndian2Norm(readModel.rxResult)
            retStatus = false
            LogUtil.log("errorCode : $errorCode")
        }

        // read the return from payload.
        if (retStatus) {

            // check whether it is in state 2
            if (readModel.rxPayload[1] != 0x02.toByte()) {
                retStatus = false
            }

            // compare with original X
            val resultPayload = writeModel.txPayload.toMutableList()
            if (random.toMutableList() == resultPayload.subList(4, 12)) {
                retStatus = false
            }

            if (retStatus) {

                val decrypt = DESUtils.undes(key, readDataList.subList(12, 20).toByteArray())
                val writeData = writeModel.txPayload.dropLast(8).toMutableList()
                writeData.addAll(decrypt.toMutableList())
                writeModel.txPayload = writeData.toByteArray()
                writeModel.txSnPacket = setTraceNum(traceNumber)
                readModel.rxPayload[1] == 0x03.toByte()

                setTxPacketList()

                retStatus = sendGetACK(ACK5)

                // check error code
                if (!nullComp(writeDataList) && !retStatus) {

                    errorCode = littleEndian2Norm(readModel.rxResult)
                    retStatus = false
                    LogUtil.log("errorCode : $errorCode")
                }
            }
        }
        closeSerialPort()
        return retStatus
    }
}