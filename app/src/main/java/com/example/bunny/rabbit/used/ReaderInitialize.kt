package com.example.bunny.rabbit.used

import com.example.bunny.rabbit.base.BaseReader
import com.example.bunny.rabbit.base.RabbitObject.ACK5
import com.example.bunny.rabbit.base.RabbitObject.readDataList
import com.example.bunny.rabbit.base.RabbitObject.readModel
import com.example.bunny.rabbit.base.RabbitObject.traceNumber
import com.example.bunny.rabbit.base.RabbitObject.writeDataList
import com.example.bunny.rabbit.base.RabbitObject.writeModel
import com.example.bunny.rabbit.model.BaseResponse
import com.example.bunny.rabbit.model.PassingInitialize

class ReaderInitialize : BaseReader() {

    private val payload = byteArrayOf(0xF1.toByte(), 0x12, 0x19, 0x00)
    private var retStatus = false
    private var errorCode = ByteArray(4)
    private lateinit var initializeData: PassingInitialize

    fun initialize(passInitialize: PassingInitialize) {
        this.initializeData = passInitialize

         setWritePacket()

         writeModel.txSessionId[0] = 0x01
         writeModel.txSnPacket = setTraceNum(traceNumber)
         writeModel.txCommandId[0] = 0x42
         writeModel.txCommandId[1] = 0x00
         writeModel.txPayloadType = byteArrayOf(payload[0], payload[1])
         writeModel.txPayloadLen = byteArrayOf(payload[2], payload[3])


         val list = mutableListOf<Byte>()
         list.addAll(arrayListOf(1, 0, 1, 0))

         val merchID8 = norm2LittleEndian(initializeData.merchID8)
         val locateID4 = norm2LittleEndian(initializeData.locateID4)
         val termID4 = norm2LittleEndian(initializeData.locateID4)
         val serviceID4 = norm2LittleEndian(initializeData.locateID4)

         list.addAll(merchID8.toMutableList())
         list.addAll(locateID4.toMutableList())
         list.addAll(termID4.toMutableList())
         list.addAll(serviceID4.toMutableList())
         list.add(0x01)

         writeModel.txPayload = list.toByteArray()

         setTxPacketList()

         if (openSerialPort()) {
             retStatus = sendGetACK(ACK5)
         }

         // check error code
         if (!nullComp(writeDataList) && !retStatus) {
             errorCode = littleEndian2Norm(readModel.rxResult)
             retStatus = false
         }
         closeSerialPort()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// after using initialize ////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    val response: BaseResponse
        get() {
            val items = BaseResponse()
            items.status = retStatus
            items.writeDataList = writeDataList
            items.readDataList = readDataList
            items.errorCode = errorCode
            return items
        }
}

