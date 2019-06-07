package com.example.bunny.reader.base

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Handler
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

open class BaseDemoReader {

    private var mContext = Contextor.getInstance().context.applicationContext
    private var serialPort = SerialPort.getInstance()

    private var timeoutTx = 1500 / 100    // mS/100mS count
    private var timeoutSendACK = 1500 / 100
    private var timeoutRX = 18000 / 100
    private var timeoutRecvACK = 1500 / 100

    private var payloadSize = 1050
    private var msgBuffSize = 1792

    private var flowResult = 0x00.toByte()

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// STATIC FUNCTION ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun setTxPacket() {
        Reader.txFormat = TxFormat(
                arrayListOf(0x10, 0x02)     //txStart
                , arrayListOf(0x01, 0)      // TX_VER
                , arrayListOf(0, 0, 0, 0)   // TX_SessionID
                , 0             // TX_MSGTYPE
                , arrayListOf(0x01, 0)      // TX_SN_PACKET
                , arrayListOf(0x01, 0)      // TX_SN_CURRENT
                , arrayListOf(0x01, 0)      // TX_SN_TOTAL
                , arrayListOf(0, 0)         // TX_CMDID
                , arrayListOf(0, 0, 0, 0)   // TX_STATUS
                , arrayListOf(0, 0)         // TX_PayloadType
                , arrayListOf(0, 0)         // TX_PayloadLen
                , arrayListOf()             // TX_Payload
                , arrayListOf(0, 0)         // TX_Checksum
                , arrayListOf(0x10, 0x03)   // TX_DLEETX
        )
    }

    fun setTraceNum(termStan: Int): ArrayList<Byte> {
        var number = termStan + 1
        if (number == 0xFFFF) {
            number = 1
        }
        Reader.termStan = number
        return arrayListOf(((number and 0xff)).toByte(), ((number and 0xff00 shr 8)).toByte())
    }

    fun setTxPacketList(txFormat: TxFormat) {
        val txPacketList = ArrayList<Byte>()
        txPacketList.addAll(txFormat.TX_DLESTX)
        txPacketList.addAll(txFormat.TX_VER)
        txPacketList.addAll(txFormat.TX_SessionID)
        txPacketList.add(txFormat.TX_MSGTYPE)
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

    fun sendGetACK(expectACK: Byte): Boolean {
        var count = 0
        var countCNX = 0
        var cnxRepeat = 0
        var retStatus = false

        do {

            LogUtil.log("count: $count")

            retStatus = packTxMsg2Send()

            if (!retStatus) {
                return false
            }

            retStatus = if (Reader.rxFormatList.isNotEmpty()) {
                unPackRxMsgRec(true)
            } else {
                unPackRxMsgRec(false)
            }

            if (!retStatus) {
                return false
            } else {

                if (flowResult == expectACK) {
                    retStatus = true
                    break
                } else if (flowResult == Flow.CNX) {
                    countCNX = 3
                    retStatus = false
                } else if (flowResult == Flow.ACK5) {
                    retStatus = false
                    break
                } else if (flowResult == Flow.NAK) {
                    Reader.txFormat.TX_STATUS[0] = (count + 1).toByte()
                } else {
                    retStatus = false
                }
            }

            count++

            if (countCNX == 3) {
                Reader.termStan = 1
                Reader.txFormat.TX_SN_PACKET[0] = 1.toByte()
                Reader.txFormat.TX_SN_PACKET[1] = 0
                countCNX = 0
                cnxRepeat++
            }

        } while (count <= 5 && cnxRepeat < 3)

        return retStatus
    }

    fun receiveSendACK(sendACK: Byte): Boolean {
        var count = 0
        var retstatus: Boolean
        var retval = false

        do {
            retstatus = unPackRxMsgRec(true)
            if (!retstatus) {
                break
            } else {
                if (flowResult == Flow.NAK || flowResult == Flow.CNX) {
                    Handler().postDelayed({
                        acknowledgeSend(flowResult, Reader.rxFormat.RX_SN_PACKET)
                    }, 100)
                    count++
                } else {
                    acknowledgeSend(sendACK, Reader.rxFormat.RX_SN_PACKET)
                    retval = true
                }
            }
        } while (count < 3)

        return retval
    }

    fun nullComp(buffer: ArrayList<Byte>): Boolean {
        return !buffer.contains(0x00)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// OPEN ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun openSerialPort(): Boolean {
        val devices = getDeviceList()
        var status = if (devices.isNotEmpty()) {
            val deviceName = prefixesDeviceName("ttyUSB", "ttyACM")
                    ?: throw RemoteException("no device")
            serialPort.open(deviceName)
        } else {
            serialPort.open(DeviceName.USBD)
        }

        if (status) {
            status = serialPort.init(BaudRate.BPS_115200, ParityBit.NOPAR, DataBit.DBS_8)
            return status
        }

        return false
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// CLOSE //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun closeSerialPort(): Boolean {
        return serialPort.close()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// PRIVATE FUNCTION ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun acknowledgeSend(type: Byte, pTraceNum: ArrayList<Byte>): Boolean {
        val sendBuff = Reader.txFormat

        // 2 bytes DLESTX (10 02)
        sendBuff.TX_DLESTX = arrayListOf(0x10, 0x02)

        // 2 bytes version (01 02)
        sendBuff.TX_VER = arrayListOf(0x01, 0)

        // 4 bytes session header (01 04 05 06)
        sendBuff.TX_SessionID = arrayListOf(0, 0, 0, 0)

        // 1 bytes message type
        sendBuff.TX_MSGTYPE = type

        // 2 bytes packet sequence num
        sendBuff.TX_SN_PACKET = pTraceNum

        // append to list size 9
        val list = arrayListOf<Byte>()
        list.addAll(sendBuff.TX_VER)
        list.addAll(sendBuff.TX_SessionID)
        list.add(sendBuff.TX_MSGTYPE)
        list.addAll(sendBuff.TX_SN_PACKET)

        // 2 bytes CRC16 checksum
        val crc16 = Crc16Utils.calculate_crc(list.toByteArray())
        sendBuff.TX_Checksum[0] = (crc16 and 0x000000ff).toByte()
        sendBuff.TX_Checksum[1] = (crc16 and 0x0000ff00).ushr(8).toByte()

        // 2 bytes DLEETX (10 03)
        sendBuff.TX_DLESTX = arrayListOf(0x10, 0x03)

        // add 10
        if (stuff10Add()) {

            if (writeData() == -1) {
                return false
            }
            return true
        }
        return false
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// WRITE //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun packTxMsg2Send(): Boolean {
        if (Reader.txFormatList.isNotEmpty()) {
            val data = Reader.txFormatList.drop(2).dropLast(4)
            val crc16 = Crc16Utils.calculate_crc(data.toByteArray())
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

    private fun unPackRxMsgRec(needUnpack: Boolean): Boolean {
        readData()

        if (Reader.rxFormatList.isNotEmpty()) {

            if (Reader.rxFormatList.size < 15) return false

            // remove 0x10
            stuff10Remove()

            // set rxFormat
            setRxFormat()

            flowResult = Reader.rxFormatList[8]

            if (flowResult == 0x00.toByte() || flowResult == 0x05.toByte() && needUnpack) {

                if (Reader.rxFormatList.size >= 29) {
                    Reader.rxFormat.RX_Payload.clear()
                    Reader.rxFormat.RX_Payload.addAll(Reader.rxFormatList.drop(25).dropLast(4))
                }

                // check sum
                val data = Reader.rxFormatList.drop(2).dropLast(4)
                val byteStr = ByteArray(2)
                val crc16 = Crc16Utils.calculate_crc(data.toByteArray())
                byteStr[0] = (crc16 and 0x000000ff).toByte()
                byteStr[1] = (crc16 and 0x0000ff00).ushr(8).toByte()

                if (byteStr[0] != Reader.rxFormat.RX_Checksum[0] || byteStr[1] != Reader.rxFormat.RX_Checksum[1]) {
                    flowResult = Flow.NAK
                    return true
                }

                // check version
                // check message type
                // check packet sequence number
                // check payload length

            } else {
                // some checking
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
        val buffer = ByteArray(msgBuffSize)
        val callback = serialPort.read2(buffer, timeoutRecvACK).toMutableList()
        if (callback.isNotEmpty()) {
            val lastPosition = callback.lastIndexOf(0x03.toByte())
            val subList = callback.subList(0, lastPosition + 1)
            Reader.rxFormatList.clear()
            Reader.rxFormatList.addAll(subList)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////// REVERSED ////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun norm2LittleEndian(data: Any): MutableList<*> {
        data as MutableList<*>
        return data.reversed() as MutableList<*>
    }

    fun littleEndian2Norm(data: Any): MutableList<*> {
        data as MutableList<*>
        return data.reversed() as MutableList<*>
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// GET DEVICE ///////////////////////////////////////////
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