package com.example.bunny.reader.used

import com.example.bunny.reader.base.BaseDemoReader
import com.example.bunny.reader.base.Flow
import com.example.bunny.reader.base.Reader

class DemoReaderDeviceInfo : BaseDemoReader() {

    fun info(): Boolean {
        val payload: ArrayList<Byte> = arrayListOf(0xF1.toByte(), 0x12.toByte(), 0x00, 0x00)
        var retStatus = false
        val errorCode: ArrayList<Byte>

        setTxPacket()

        Reader.txFormat.TX_SessionID[0] = 0x01

        // set the sequence #
        Reader.txFormat.TX_SN_PACKET = setTraceNum(Reader.termStan)

        // set the command ID
        Reader.txFormat.TX_CMDID[0] = 0x62.toByte()
        Reader.txFormat.TX_CMDID[1] = 0x00.toByte()

        // set the payload
        Reader.txFormat.TX_PayloadType[0] = payload[0]
        Reader.txFormat.TX_PayloadType[1] = payload[1]
        Reader.txFormat.TX_PayloadLen[0] = payload[2]
        Reader.txFormat.TX_PayloadLen[1] = payload[3]

        // send this out
        if (openSerialPort()) {
            retStatus = sendGetACK(Flow.ACK5)
        }

        // check error code
        if (!nullComp(Reader.txFormatList) && !retStatus) {

            //errorCode = littleEndian2Norm(Reader.rxFormat.RX_RESULT)
            //LogUtil.log("errorCode : $errorCode")
        }

        if (retStatus) {

            val data = Reader.rxFormat.RX_Payload
            val deviceID4 = littleEndian2Norm( data.subList(0, 4) as ArrayList<Byte> )
            val merchID8 = littleEndian2Norm(data.subList(4, 12) as ArrayList<Byte>)
            val firmVer4 = littleEndian2Norm( data.subList(12, 16) as ArrayList<Byte> )
            val appVer4 = littleEndian2Norm( data.subList(16, 20) as ArrayList<Byte> )
            val sAMVer4 = littleEndian2Norm( data.subList(20, 24) as ArrayList<Byte> )
            val pollTO4 = littleEndian2Norm( data.subList(24, 28) as ArrayList<Byte> )
            val authTO4 = littleEndian2Norm( data.subList(28, 32) as ArrayList<Byte> )

        }

        closeSerialPort()
        return retStatus
    }

}