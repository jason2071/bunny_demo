package com.example.bunny.reader.read_write

import com.example.bunny.rabbit.base.RabbitObject.ACK5
import com.example.bunny.rabbit.base.RabbitObject.readDataList
import com.example.bunny.rabbit.base.RabbitObject.readModel
import com.example.bunny.rabbit.base.RabbitObject.traceNumber
import com.example.bunny.rabbit.base.RabbitObject.writeModel
import com.example.bunny.rabbit.model.WriteModel
import com.example.bunny.reader.interfaces.IMainReader
import com.example.bunny.reader.interfaces.IReaderResponse
import com.example.bunny.reader.manager.ReaderManager
import com.example.bunny.reader.model.AuthReaderResponse
import com.example.bunny.reader.model.BaseReaderResponse
import com.example.bunny.util.DESUtils
import kotlin.random.Random

class AuthReader(private val iReaderResponse: IReaderResponse) : IMainReader {

    private val iMainReader: IMainReader = this
    private val readerManager: ReaderManager = ReaderManager(iMainReader)

    private val payload = byteArrayOf(0x01, 0x00, 0x14, 0x00)
    private val commandId = byteArrayOf(0x41, 0)

    private var errorCode = mutableListOf<Byte>()
    private var randomNumber = mutableListOf<Byte>()
    private var encryptData = mutableListOf<Byte>()
    private var decryptData = mutableListOf<Byte>()
    private var keyData = mutableListOf<Byte>()

    private var isFirstLoad = false

    fun setKeyData(keyData: MutableList<Byte>) {
        this.keyData = keyData
    }

    override fun onInitReaderData() {

        randomNumber = Random.nextBytes(8).toMutableList()
        encryptData = DESUtils.des(keyData.toByteArray(), randomNumber.toByteArray()).toMutableList()

        readerManager.setReaderData(
                WriteModel(
                        txVersion = mutableListOf(0x01, 0)
                        , txSessionId = mutableListOf(0x01, 0, 0, 0)
                        , txSnPacket = mutableListOf(0x01, 0)
                        , txSnCurrent = mutableListOf(0x01, 0)
                        , txSnTotal = mutableListOf(0x01, 0)
                        , txCommandId = commandId.toMutableList()
                        , txPayloadType = mutableListOf(payload[0], payload[1])
                        , txPayloadLen = mutableListOf(payload[2], payload[3])
                )
        )
        readerManager.setTraceNumber(traceNumber)

        val list = mutableListOf<Byte>()
        list.addAll(arrayListOf(0, 1, 0, 0))
        list.addAll(encryptData.toMutableList())
        list.addAll(arrayListOf(0, 0, 0, 0, 0, 0, 0, 0))

        readerManager.setPayload(list)
        readerManager.setTxPacketList()

        if (readerManager.openSerialPort()) {
            readerManager.sendGetACK(ACK5)
        }
    }

    override fun onResponse(res: BaseReaderResponse) {
        if (readerManager.openSerialPort()) {
            readerManager.closeSerialPort()
        }

        if (!readerManager.nullCompare() && !res.status) {
            errorCode = readModel.rxResult.reversed().toMutableList()
            res.status = false
        }


        if (!isFirstLoad) {
            isFirstLoad = true

            if (res.status) {

                // check whether it is in state 2
                if (readModel.rxPayload[1] != 0x02.toByte()) {
                    res.status = false
                }

                // compare with original X
                val resultPayload = writeModel.txPayload
                if (randomNumber.toMutableList() == resultPayload.subList(4, 12)) {
                    res.status = false
                }

                if (res.status) {

                    decryptData = DESUtils.undes(keyData.toByteArray(), readDataList.subList(12, 20).toByteArray()).toMutableList()
                    val writeData = writeModel.txPayload.dropLast(8).toMutableList()
                    writeData.addAll(decryptData.toMutableList())

                    readerManager.setTraceNumber(traceNumber)
                    readerManager.setPayload(writeData)
                    readerManager.setTxPacketList()
                    readerManager.setWriteIndexPayload(0, 0x03)

                    if (readerManager.openSerialPort()) {
                        readerManager.sendGetACK(ACK5)
                    }
                }
            }
        } else {
            iReaderResponse.onResponseSuccess(res.copy(
                    errorCode = errorCode
                    , result = AuthReaderResponse(randomNumber, encryptData, decryptData)
            ))
        }
    }
}