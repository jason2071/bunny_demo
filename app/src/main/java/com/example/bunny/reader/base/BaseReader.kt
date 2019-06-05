package com.example.bunny.reader.base

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.RemoteException
import com.example.bunny.api.SerialPort
import com.example.bunny.manager.Contextor
import com.example.bunny.reader.model.RxFormat
import com.example.bunny.reader.model.TxFormat
import com.example.bunny.util.Crc16Utils
import com.example.bunny.util.LogUtil
import com.usdk.apiservice.aidl.serialport.BaudRate
import com.usdk.apiservice.aidl.serialport.DataBit
import com.usdk.apiservice.aidl.serialport.DeviceName
import com.usdk.apiservice.aidl.serialport.ParityBit
import java.io.File
import kotlin.collections.ArrayList

open class BaseReader {

    private var mContext = Contextor.getInstance().context.applicationContext
    private var serialPort = SerialPort.getInstance()

    private var timeoutTx = 1500 / 100    // mS/100mS count
    private var timeoutSendACK = 1500 / 100
    private var timeoutRX = 18000 / 100
    private var timeoutRecvACK = 1500 / 100

    private var payloadSize = 1050
    private var msgbuffSize = 1792

    private var flowResult = 0.toByte()

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// STATIC FUNCTION ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun setTxPacket() {
        Reader.txFormat = TxFormat(
                arrayListOf(0x10, 0x02)                 //TX_DLESTX
                , arrayListOf(0x01, 0)                  // TX_VER
                , arrayListOf(0x00, 0x00, 0x00, 0x00)   // TX_SessionID
                , arrayListOf(0x00)                     // TX_MSGTYPE
                , arrayListOf(0x01, 0x00)               // TX_SN_PACKET
                , arrayListOf(0x01, 0x00)               // TX_SN_CURRENT
                , arrayListOf(0x01, 0x00)               // TX_SN_TOTAL
                , arrayListOf(0x00, 0x00)               // TX_CMDID
                , arrayListOf(0x00, 0x00, 0x00, 0x00)   // TX_STATUS
                , arrayListOf(0x00, 0x00)               // TX_PayloadType
                , arrayListOf(0x00, 0x00)               // TX_PayloadLen
                , arrayListOf()                         // TX_Payload
                , arrayListOf(0x00, 0x00)               // TX_Checksum
                , arrayListOf(0x10, 0x03)               // TX_DLEETX
        )
    }

    fun norm2LittleEndian(items: ArrayList<Int>): ArrayList<Int> {
        items.reverse()
        return items
    }

    fun setTraceNum(termStan: Int): ArrayList<Byte> {
        var number = termStan + 1
        if (number == 0xFFFF) {
            number = 1
        }
        Reader.sequenceNumber = number
        return arrayListOf(((number and 0xff)).toByte(), ((number and 0xff00 shr 8)).toByte())
    }

    fun setTxPacketList(txFormat: TxFormat) {
        val txPacketList = ArrayList<Byte>()
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

    fun sendGetACK(expectACK: Int): Boolean {
        var countCNX = 0
        var CNXRepeat = 0
        var retStatus = false

        do {
            retStatus = packTxMsg2Send()

            if (!retStatus) {
                return false
            }

            retStatus = if (Reader.rxFormatList.isNotEmpty()) {
                unPackRxMsgRec()
            } else {
                unPackRxMsgRec()
            }

            if (!retStatus) {
                return false
            } else {

                //if ()

            }

        } while (countCNX <= 5 && CNXRepeat < 3)

        return retStatus
    }

    fun appendRXArray(rxFormat: RxFormat): ArrayList<Byte> {
        val list = arrayListOf<Byte>()
        list.addAll(rxFormat.RX_DLESTX)
        list.addAll(rxFormat.RX_VER)
        list.addAll(rxFormat.RX_SessionID)
        list.add(rxFormat.RX_MSGTYPE)
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// OPEN ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun openSerialPort(): Boolean {
        val devices = getDeviceList()
        LogUtil.log("open")

        var status = if (devices.isNotEmpty()) {
            val deviceName = prefixesDeviceName("ttyUSB", "ttyACM")
                    ?: throw RemoteException("no device")
            serialPort.open(deviceName)
        } else {
            serialPort.open(DeviceName.USBD)
        }

        if (status) {
            LogUtil.log("init")

            status = serialPort.init(BaudRate.BPS_115200, ParityBit.NOPAR, DataBit.DBS_8)

            return status
        }

        return false
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// CLOSE //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun closeSerialPort(): Boolean {

        LogUtil.log("close")

        return serialPort.close()
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// PRIVATE FUNCTION ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// WRITE //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun packTxMsg2Send(): Boolean {
        if (Reader.txFormatList.isNotEmpty()) {
            val data = Reader.txFormatList.drop(2).dropLast(4).toByteArray()
            val crc16 = Crc16Utils.calculate_crc(data)
            val size = Reader.txFormatList.size
            Reader.txFormatList[size - 4] = (crc16 and 0x000000ff).toByte()
            Reader.txFormatList[size - 3] = (crc16 and 0x0000ff00).ushr(8).toByte()

            if (stuff10Add()) {
                return writeData() != -1
            }
        }
        return false
    }

    private fun stuff10Add(): Boolean {
        if (Reader.txFormatList.isNotEmpty()) {
            val data = Reader.txFormatList.drop(2).dropLast(2).toMutableList()
            if (data[data.size - 1] == 0x10.toByte()) {
                data.add(9999999.toByte())
            }

            for (i in 0 until data.size) {
                if (data[i] == 0x10.toByte()) data.add(i + 1, -1)
            }

            for (i in 0 until data.size) {
                if (data[i] == 9999999.toByte()) data[i] = 0x10
            }

            data.add(0, 0x10)
            data.add(1, 0x02)
            data.add(0x10)
            data.add(0x03)
            Reader.txFormatList = data as ArrayList<Byte>
            return true
        }
        return false
    }

    private fun writeData(): Int {
        if (Reader.txFormatList.isNotEmpty()) {
            return serialPort.write(Reader.txFormatList.toByteArray(), timeoutTx)
        }
        return -1
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// READ ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun unPackRxMsgRec(): Boolean {
        readData()
        if (Reader.rxFormatList.isNotEmpty()) {

            if (Reader.rxFormatList.size < 15) {
                return false
            }

            stuff10Remove()
            setRxFormat()

            flowResult = Reader.rxFormatList[8]

            if (flowResult == 0.toByte() || flowResult == 5.toByte()) {


                flowResult = Flow.NAK
            }

            return true
        }
        return false
    }

    private fun setRxFormat() {
        val data = Reader.rxFormatList
        val size = data.size
        if (size >= 29) {
            val rxFormat = RxFormat(
                    arrayListOf(data[0], data[1])
                    , arrayListOf(data[2], data[3])
                    , arrayListOf(data[4], data[5], data[6], data[7])
                    , data[8]
                    , arrayListOf(data[9], data[10])
                    , arrayListOf(data[11], data[12])
                    , arrayListOf(data[13], data[14])
                    , arrayListOf(data[15], data[16])
                    , arrayListOf(data[17], data[18], data[19], data[20])
                    , arrayListOf(data[21], data[22])
                    , arrayListOf(data[23], data[24])
                    , arrayListOf()
                    , arrayListOf(data[size - 4], data[size - 3])
                    , arrayListOf(data[size - 2], data[size - 1])
            )
            Reader.rxFormat = rxFormat
        } else if (size >= 15) {
            val rxFormat = RxFormat(
                    arrayListOf(data[0], data[1])
                    , arrayListOf(data[2], data[3])
                    , arrayListOf(data[4], data[5], data[6], data[7])
                    , data[8]
                    , arrayListOf()
                    , arrayListOf()
                    , arrayListOf()
                    , arrayListOf()
                    , arrayListOf()
                    , arrayListOf()
                    , arrayListOf()
                    , arrayListOf()
                    , arrayListOf(data[size - 4], data[size - 3])
                    , arrayListOf(data[size - 2], data[size - 1])
            )
            Reader.rxFormat = rxFormat
        }
    }

    private fun stuff10Remove() {
        if (Reader.rxFormatList.isNotEmpty()) {
            val data = Reader.rxFormatList.drop(2).dropLast(2).toMutableList()

            try {

                for (i in 0 until data.size) {
                    if (data[i] == 0x10.toByte() && data[i + 1] == 0x10.toByte()) {
                        data.removeAt(i + 1)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            data.add(0, 0x10)
            data.add(1, 0x02)
            data.add(0x10)
            data.add(0x03)
            Reader.rxFormatList.clear()
            Reader.rxFormatList.addAll(data)
        }
    }

    private fun readData() {
        val buffer = ByteArray(msgbuffSize)
        val callback = serialPort.read2(buffer, timeoutRecvACK).toMutableList()

        if (callback.isNotEmpty()) {
            val callbackSize = callback.indexOfLast { it > 0 } + 1
            val size = msgbuffSize - callbackSize
            val result = callback.dropLast(size)

            Reader.rxFormatList.clear()
            Reader.rxFormatList.addAll(result)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// GET DEVICE INFO /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun prefixesDeviceName(vararg prefixes: String): String? {
        val dev = File("/dev")
        for (file in dev.listFiles()!!) {
            for (prefix in prefixes) {
                if (file.absolutePath.startsWith("/dev/$prefix")) {
                    return file.toString().substring(5)
                }
            }
        }
        return null
    }

    private fun getDeviceList(): ArrayList<UsbDevice> {
        val manager = mContext.getSystemService(Context.USB_SERVICE) as UsbManager
        val devices = manager.deviceList
        return ArrayList(devices.values)
    }
}