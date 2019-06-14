package com.example.bunny.reader.read_write

import com.example.bunny.rabbit.base.RabbitObject
import com.example.bunny.rabbit.base.RabbitObject.ACK5
import com.example.bunny.rabbit.base.RabbitObject.readModel
import com.example.bunny.rabbit.base.RabbitObject.traceNumber
import com.example.bunny.rabbit.model.WriteModel
import com.example.bunny.reader.interfaces.IMainReader
import com.example.bunny.reader.interfaces.IReaderResponse
import com.example.bunny.reader.manager.ReaderManager
import com.example.bunny.reader.model.BaseReaderResponse

class GetCDInfoReader(private val iReaderResponse: IReaderResponse) : IMainReader {

    private val iMainReader: IMainReader = this
    private val readerManager: ReaderManager = ReaderManager(iMainReader)

    private val payload = byteArrayOf(0xF1.toByte(), 0x12, 0x00, 0x00)
    private val commandId = byteArrayOf(0x63, 0)

    private var errorCode = mutableListOf<Byte>()

    override fun onInitReaderData() {
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
        }
        iReaderResponse.onResponseSuccess(res.copy(errorCode = errorCode))
    }
}