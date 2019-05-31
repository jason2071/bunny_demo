package com.example.bunny.base

import com.example.bunny.model.RxFormat
import com.example.bunny.model.TxFormat
import com.example.bunny.util.Crc16Utils

open class BaseReader {

    fun setTxPacket() {
        val txPacket = TxFormat(
                arrayListOf(0x10, 0x02)     //TX_DLESTX
                , arrayListOf(0x01, 0)      // TX_VER
                , arrayListOf(0x00, 0x00, 0x00, 0x00) // TX_SessionID
                , arrayListOf(0x00)         // TX_MSGTYPE
                , arrayListOf(0x01, 0x00)   // TX_SN_PACKET
                , arrayListOf(0x01, 0x00)   // TX_SN_CURRENT
                , arrayListOf(0x01, 0x00)   // TX_SN_TOTAL
                , arrayListOf(0x00, 0x00)             // TX_CMDID
                , arrayListOf(0x00, 0x00, 0x00, 0x00) // TX_STATUS
                , arrayListOf(0x00, 0x00)   // TX_PayloadType
                , arrayListOf(0x00, 0x00)   // TX_PayloadLen
                , arrayListOf()             // TX_Payload
                , arrayListOf(0x00, 0x00)   // TX_Checksum
                , arrayListOf(0x10, 0x03)   // TX_DLEETX
        )
        Reader.txFormat = txPacket
    }

    fun norm2LittleEndian(items: ArrayList<Int>): ArrayList<Int> {
        items.reverse()
        return items
    }

    fun setTraceNum(termStan: Int): ArrayList<Int> {
        var number = termStan + 1
        if (number == 0xFFFF) {
            number = 1
        }
        Reader.sequenceNumber = number
        return arrayListOf(((number and 0xff)), ((number and 0xff00 shr 8)))
    }

    fun setTxPacketList(txFormat: TxFormat) {
        val txPacketList = ArrayList<Int>()
        txPacketList.addAll(txFormat.TX_DLESTX)
        txPacketList.addAll(txFormat.TX_VER)
        txPacketList.addAll(txFormat.TX_SessionID)
        txPacketList.addAll(txFormat.TX_MSGTYPE)
        txPacketList.addAll(txFormat.TX_SN_PACKET)
        txPacketList.addAll(txFormat.TX_SN_CURRENT)
        txPacketList.addAll(txFormat.TX_SN_TOTAL)
        txPacketList.addAll(txFormat.TX_CMDID)
        txPacketList.addAll(txFormat.TX_STATUS)
        txPacketList.addAll(txFormat.TX_PayloadType)
        txPacketList.addAll(txFormat.TX_PayloadLen)
        txPacketList.addAll(txFormat.TX_Payload)
        txPacketList.addAll(txFormat.TX_Checksum)
        txPacketList.addAll(txFormat.TX_DLEETX)
        Reader.txFormatList = txPacketList
    }

    fun sendGetACK() {
        val rxPacketList = ArrayList<Int>()
        var countCNX = 0
        var CNXRepeat = 0
        var retStatus = false


        for (count in 0..5) {
            retStatus = packTxMsg2Send()
            break
        }

    }

    private fun packTxMsg2Send(): Boolean {
        val data = Reader.txFormatList.drop(2).dropLast(4).toIntArray()
        val crc16 = Crc16Utils.calculateInt(data)

        val size = Reader.txFormatList.size

        Reader.txFormatList[size - 4] = crc16 and 0x000000ff
        Reader.txFormatList[size - 3] = (crc16 and 0x0000ff00).ushr(8)

        return true
    }

    fun appendRXArray(rxFormat: RxFormat): ArrayList<Int> {
        val list = arrayListOf<Int>()
        list.addAll(rxFormat.RX_DLESTX)
        list.addAll(rxFormat.RX_VER)
        list.addAll(rxFormat.RX_SessionID)
        list.addAll(rxFormat.RX_MSGTYPE)
        list.addAll(rxFormat.RX_SN_PACKET)
        list.addAll(rxFormat.RX_SN_CURRENT)
        list.addAll(rxFormat.RX_SN_TOTAL)
        list.addAll(rxFormat.RX_CMDID)
        list.addAll(rxFormat.RX_STATUS)
        list.addAll(rxFormat.RX_PayloadType)
        list.addAll(rxFormat.RX_PayloadLen)
        list.addAll(rxFormat.RX_Payload)
        list.addAll(rxFormat.RX_Checksum)
        list.addAll(rxFormat.RX_DLEETX)
        return list
    }
}