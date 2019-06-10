package com.example.bunny.rabbit.base

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.RemoteException
import com.example.bunny.api.SerialPort
import com.example.bunny.manager.Contextor
import com.example.bunny.rabbit.base.RabbitObject.ACK5
import com.example.bunny.rabbit.base.RabbitObject.CNX
import com.example.bunny.rabbit.base.RabbitObject.NAK
import com.example.bunny.rabbit.base.RabbitObject.readModel
import com.example.bunny.rabbit.base.RabbitObject.readDataList
import com.example.bunny.rabbit.base.RabbitObject.traceNumber
import com.example.bunny.rabbit.base.RabbitObject.writeModel
import com.example.bunny.rabbit.base.RabbitObject.writeDataList
import com.example.bunny.rabbit.model.ReadModel
import com.example.bunny.rabbit.model.WriteModel
import com.example.bunny.util.Crc16Utils
import com.example.bunny.util.LogUtil
import com.usdk.apiservice.aidl.serialport.BaudRate
import com.usdk.apiservice.aidl.serialport.DataBit
import com.usdk.apiservice.aidl.serialport.DeviceName
import com.usdk.apiservice.aidl.serialport.ParityBit
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

open class BaseReader {

    private var mContext = Contextor.getInstance().context.applicationContext
    private var serialPort = SerialPort.getInstance()

    // buffer size
    private val payloadSize = 1050
    private var msgBufferSize = 1792

    // time
    private var timeoutTx       = 1500 / 100
    private var timeoutSendACK  = 1500 / 100
    private var timeoutRX       = 18000 / 100
    private var timeoutRecvACK  = 1500 / 100

    // flow result
    private var flowResult: Byte = 0

    init {
        val start = ByteArray(2)
        val version = ByteArray(2)
        val sessionId = ByteArray(4)
        val messageType: Byte = 0x00
        val snPacket = ByteArray(2)
        val snCurrent = ByteArray(2)
        val snTotal = ByteArray(2)
        val commandId = ByteArray(2)
        val statusOrResult = ByteArray(4)
        val payloadType = ByteArray(2)
        val payloadLen = ByteArray(2)
        val payload = ByteArray(payloadSize + 20)
        val checksum = ByteArray(2)
        val stop = ByteArray(2)

        writeModel = WriteModel(start, version, sessionId, messageType, snPacket, snCurrent, snTotal
                , commandId, statusOrResult, payloadType, payloadLen, payload, checksum, stop)

        readModel = ReadModel(start, version, sessionId, messageType, snPacket, snCurrent, snTotal
                , commandId, statusOrResult, payloadType, payloadLen, payload, checksum, stop)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// STATIC FUNCTION ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun setWritePacket() {
        writeModel.txStart = byteArrayOf(0x10, 0x02)
        writeModel.txVersion = byteArrayOf(0x01, 0)
        writeModel.txSnPacket = byteArrayOf(0x01, 0)
        writeModel.txSnCurrent = byteArrayOf(0x01, 0)
        writeModel.txSnTotal = byteArrayOf(0x01, 0)
        writeModel.txStop = byteArrayOf(0x10, 0x03)
    }

    fun setTraceNum(termStan: Int): ByteArray {
        var number = termStan + 1
        if (number == 0xFFFF) {
            number = 1
        }
        traceNumber = number
        return byteArrayOf((number and 0xff).toByte(), (number and 0xff00 shr 8).toByte())
    }

    fun setTxPacketList() {
        writeDataList.clear()
        writeDataList.addAll(writeModel.txStart.toMutableList())
        writeDataList.addAll(writeModel.txVersion.toMutableList())
        writeDataList.addAll(writeModel.txSessionId.toMutableList())
        writeDataList.add(writeModel.txMessageType)
        writeDataList.addAll(writeModel.txSnPacket.toMutableList())
        writeDataList.addAll(writeModel.txSnCurrent.toMutableList())
        writeDataList.addAll(writeModel.txSnTotal.toMutableList())
        writeDataList.addAll(writeModel.txCommandId.toMutableList())
        writeDataList.addAll(writeModel.txStatus.toMutableList())
        writeDataList.addAll(writeModel.txPayloadType.toMutableList())
        writeDataList.addAll(writeModel.txPayloadLen.toMutableList())
        writeDataList.addAll(writeModel.txPayload.toMutableList())
        writeDataList.addAll(writeModel.txCheckSum.toMutableList())
        writeDataList.addAll(writeModel.txStop.toMutableList())
    }

    fun sendGetACK(expectACK: Byte): Boolean {
        var count = 0
        var countCNX = 0
        var cnxRepeat = 0
        var retStatus = false

        do {
            retStatus = packTxMsg2Send()

            if (!retStatus) {
                return false
            }

            retStatus = if (readDataList.isEmpty()) {
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
                } else if (flowResult == CNX) {
                    countCNX = 3
                    retStatus = false
                } else if (flowResult == ACK5) {
                    retStatus = false
                    break
                } else if (flowResult == NAK) {
                    writeModel.txStatus[0] = (count + 1).toByte()
                } else {
                    retStatus = false
                }
            }

            count++

            if (countCNX == 3) {
                traceNumber = 1
                writeModel.txSnPacket[0] = 1
                writeModel.txSnPacket[1] = 0

                countCNX = 0
                cnxRepeat++
            }

        } while (count <= 5 && cnxRepeat < 3)

        return retStatus
    }

    fun nullComp(buffer: MutableList<Byte>): Boolean {
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
    ////////////////////////////////////////// REVERSED ////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun norm2LittleEndian(data: ByteArray): ByteArray {
        data.reverse()
        return data
    }

    fun littleEndian2Norm(data: ByteArray): ByteArray {
        data.reverse()
        return data
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// PRIVATE FUNCTION ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// WRITE //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun packTxMsg2Send(): Boolean {
        if (writeDataList.isNotEmpty()) {

            val data = writeDataList.drop(2).dropLast(4)
            val crc16 = Crc16Utils.calculate_crc(data.toByteArray())
            val size = writeDataList.size
            writeDataList[size - 4] = (crc16 and 0x000000ff).toByte()
            writeDataList[size - 3] = (crc16 and 0x0000ff00).ushr(8).toByte()

            if (stuff10Add()) {
                return writeData() != -1
            }
        }
        return false
    }

    private fun stuff10Add(): Boolean {
        if (writeDataList.isNotEmpty()) {
            val data = writeDataList.drop(2).dropLast(2).toMutableList()

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
            writeDataList = data
            return true
        }
        return false
    }

    private fun writeData(): Int {
        if (writeDataList.isNotEmpty()) {
            return serialPort.write(writeDataList.toByteArray(), timeoutTx)
        }
        return -1
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// READ ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun unPackRxMsgRec(needUnpack: Boolean): Boolean {
        receiveData()

        if (readDataList.isNotEmpty()) {

            if (readDataList.size < 15) return false

            // remove 0x10
            stuff10Remove()

            // set rxFormat
            setRxFormat()

            flowResult = readDataList[8]

            if (flowResult == 0x00.toByte() || flowResult == 0x05.toByte() && needUnpack) {

                if (readDataList.size >= 29) {
                    readModel.rxPayload.fill(0x00, 0, readModel.rxPayload.size)
                    readModel.rxPayload = readDataList.drop(25).dropLast(4).toByteArray()
                }

                // check sum
                val data = readDataList.drop(2).dropLast(4)
                val byteStr = ByteArray(2)
                val crc16 = Crc16Utils.calculate_crc(data.toByteArray())
                byteStr[0] = (crc16 and 0x000000ff).toByte()
                byteStr[1] = (crc16 and 0x0000ff00).ushr(8).toByte()

                if (byteStr[0] != readModel.rxChecksum[0] || byteStr[1] != readModel.rxChecksum[1]) {
                    flowResult = NAK
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
        val size = readDataList.size
        if (size >= 29) {
            readModel = ReadModel(
                    byteArrayOf(readDataList[0], readDataList[1])
                    , byteArrayOf(readDataList[2], readDataList[3])
                    , byteArrayOf(readDataList[4], readDataList[5], readDataList[6], readDataList[7])
                    , readDataList[8]
                    , byteArrayOf(readDataList[9], readDataList[10])
                    , byteArrayOf(readDataList[11], readDataList[12])
                    , byteArrayOf(readDataList[13], readDataList[14])
                    , byteArrayOf(readDataList[15], readDataList[16])
                    , byteArrayOf(readDataList[17], readDataList[18], readDataList[19], readDataList[20])
                    , byteArrayOf(readDataList[21], readDataList[22])
                    , byteArrayOf(readDataList[23], readDataList[24])
                    , byteArrayOf()
                    , byteArrayOf(readDataList[size - 4], readDataList[size - 3])
                    , byteArrayOf(readDataList[size - 2], readDataList[size - 1])
            )
        } else if (size >= 15) {
            readModel = ReadModel(
                    byteArrayOf(readDataList[0], readDataList[1])
                    , byteArrayOf(readDataList[2], readDataList[3])
                    , byteArrayOf(readDataList[4], readDataList[5], readDataList[6], readDataList[7])
                    , readDataList[8]
                    , byteArrayOf()
                    , byteArrayOf()
                    , byteArrayOf()
                    , byteArrayOf()
                    , byteArrayOf()
                    , byteArrayOf()
                    , byteArrayOf()
                    , byteArrayOf()
                    , byteArrayOf(readDataList[size - 4], readDataList[size - 3])
                    , byteArrayOf(readDataList[size - 2], readDataList[size - 1])
            )
        }
    }

    private fun stuff10Remove() {
        if (readDataList.isNotEmpty()) {
            val data = readDataList.drop(2).dropLast(2).toMutableList()

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
            readDataList.clear()
            readDataList.addAll(data)
        }
    }

    private fun receiveData() {
        val buffer = ByteArray(msgBufferSize)
        val callback = serialPort.read2(buffer, timeoutRecvACK).toMutableList()
        if (callback.isNotEmpty()) {
            val lastPosition = callback.lastIndexOf(0x03.toByte())
            val subList = callback.subList(0, lastPosition + 1)
            readDataList.clear()
            readDataList.addAll(subList)
        }
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