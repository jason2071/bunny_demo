package com.example.bunny.reader.payment

import com.example.bunny.rabbit.base.RabbitObject.ACK4
import com.example.bunny.rabbit.base.RabbitObject.ACK5
import com.example.bunny.rabbit.base.RabbitObject.readModel
import com.example.bunny.rabbit.base.RabbitObject.traceNumber
import com.example.bunny.rabbit.model.WriteModel
import com.example.bunny.reader.interfaces.IMainReader
import com.example.bunny.reader.interfaces.IReaderResponse
import com.example.bunny.reader.manager.ReaderManager
import com.example.bunny.reader.model.BaseReaderResponse
import com.example.bunny.reader.model.CardModel

class CardEnquiry2Reader(private val iReaderResponse: IReaderResponse) : IMainReader {

    private val iMainReader: IMainReader = this
    private val readerManager: ReaderManager = ReaderManager(iMainReader)

    private val payload = byteArrayOf(0xF1.toByte(), 0x12, 0x00, 0x00)
    private val commandId = byteArrayOf(0x09, 0)

    private var errorCode = mutableListOf<Byte>()
    private var aTmp4 = mutableListOf<Byte>(0, 0, 0, 0)

    private var cardModel = CardModel()

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
            readerManager.sendGetACK(ACK4)
        }
    }

    override fun onResponse(res: BaseReaderResponse) {
        if (readerManager.openSerialPort()) {
            readerManager.closeSerialPort()
        }

        if (res.status) {
            res.status = readerManager.receiveSendACK(ACK4)
        } else if (readModel.rxMessageType == ACK5) {
            errorCode = readModel.rxResult.reversed().toMutableList()
        }

        if (!readerManager.nullCompare() && !res.status) {
            errorCode = readModel.rxResult.reversed().toMutableList()
            res.status = false
        }

        if (res.status) {

            cardModel = CardModel(
                    cscID5 = readModel.rxPayload.subList(0, 5)
                    , cardType1 = readModel.rxPayload[5]
                    , cardStatus1 = readModel.rxPayload[6]
                    , purseAmt6 = mutableListOf()                   //readModel.rxPayload[7]
                    , expd2 = readModel.rxPayload.subList(11, 13)
                    , lastUsageExpd4 = readModel.rxPayload.subList(13, 17)
                    , blacklist1 = readModel.rxPayload[17]
                    , lTxnTime4 = readModel.rxPayload.subList(543, 547)
                    , lTxnID1 = readModel.rxPayload[547]
                    , lTxnSpID2 = readModel.rxPayload.subList(548, 550)
                    , lTxnRwID4 = readModel.rxPayload.subList(550, 554)
                    , lTxnAmt = mutableListOf()                     //readModel.rxPayload[554]
                    , lTxnPurse = mutableListOf()                   //readModel.rxPayload[558]
                    , lTxnBonus2 = readModel.rxPayload.subList(562, 564)
                    , lTxnRemainTripNo2 = readModel.rxPayload.subList(564, 566)
                    , lTxnLocateCode2 = readModel.rxPayload.subList(566, 568)
                    , lTxnEntryDateTime4 = readModel.rxPayload.subList(568, 572)
                    , lTxnEntryLocateCode2 = readModel.rxPayload.subList(572, 574)
                    , lTxnEntryEquipNo2 = readModel.rxPayload.subList(574, 576)
                    , lTxnExitEquipNo2 = readModel.rxPayload.subList(576, 578)
            )

        } else {
            aTmp4 = mutableListOf(0, 0x11, 0, 0x07)

            if (aTmp4 != errorCode) {
                cardModel.cscID5 = readModel.rxPayload.subList(6, 11).reversed().toMutableList()
                cardModel.batchID4 = readModel.rxPayload.subList(19, 23).reversed().toMutableList()

                //val UDlen = readModel.rxPayload[28] * 256 + readModel.rxPayload[27]
                /**
                 * *UDlen = l_rx.RX_Payload[28] * 256 + l_rx.RX_Payload[27];
                 * memcpy ( UD, &l_rx.RX_Payload[31], UD_SIZE );
                 */
            }
        }
    }
}