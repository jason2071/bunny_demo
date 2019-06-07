package com.example.bunny.reader.used

import com.example.bunny.reader.base.BaseDemoReader
import com.example.bunny.reader.base.Flow
import com.example.bunny.reader.base.Reader
import com.example.bunny.util.LogUtil

class DemoReaderInitialize : BaseDemoReader() {

    fun initialize(
            merchantID8: ArrayList<Byte>
            , locateID4: ArrayList<Byte>
            , terminalID4: ArrayList<Byte>
            , serviceID4: ArrayList<Byte>
    ): Boolean {
        val payload = arrayListOf(0xF1.toByte(), 0x12.toByte(), 0x19.toByte(), 0x00.toByte())
        var retStatus = false
        val errorCode: ArrayList<Byte>

        setTxPacket()

        val txFormat = Reader.txFormat

        txFormat.TX_SessionID[0] = 0x01

        txFormat.TX_SN_PACKET = setTraceNum(Reader.termStan)

        txFormat.TX_CMDID[0] = 0x42.toByte()
        txFormat.TX_CMDID[1] = 0x00.toByte()

        txFormat.TX_PayloadType[0] = payload[0]
        txFormat.TX_PayloadType[1] = payload[1]

        txFormat.TX_PayloadLen[0] = payload[2]
        txFormat.TX_PayloadLen[1] = payload[3]

        //Version 0001
        txFormat.TX_Payload.add(0x01, 0x00)

        //EDC = 0001
        txFormat.TX_Payload.add(0x01, 0x00)

        //txFormat.TX_Payload.addAll( norm2LittleEndian(merchantID8) )
        //txFormat.TX_Payload.addAll( norm2LittleEndian(locateID4) )
        //txFormat.TX_Payload.addAll( norm2LittleEndian(terminalID4) )
        //txFormat.TX_Payload.addAll( norm2LittleEndian(serviceID4) )

        // topup enable
        txFormat.TX_Payload.add(0x01)

        setTxPacketList(Reader.txFormat)

        if (openSerialPort()) {

            retStatus = sendGetACK(Flow.ACK5)
            LogUtil.log("initialize : $retStatus")
        }

        // check error code
        if (!nullComp(Reader.txFormatList) && !retStatus) {

            //errorCode = littleEndian2Norm(Reader.rxFormat.RX_RESULT)
            //LogUtil.log("errorCode : $errorCode")
        }

        closeSerialPort()
        return retStatus
    }
}