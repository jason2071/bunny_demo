package com.example.bunny

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.bunny.reader.base.Reader
import com.example.bunny.reader.used.ReaderBalance
import com.example.bunny.reader.used.ReaderCancel
import com.example.bunny.reader.used.ReaderDeviceInfo
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val byteCancel = DataSource.writeCancel().drop(2).dropLast(4).toByteArray()
        val byteStr3 = ByteArray(4)
        val demo3 = Crc16Utils.calculate_crc(byteCancel)
        byteStr3[0] = (demo3 and 0x000000ff).toByte()
        byteStr3[1] = (demo3 and 0x0000ff00).ushr(8).toByte()

        log("Cancel: " + Integer.toHexString(demo3))
        log("Cancel: " + Gson().toJson(byteStr3))
        log("Cancel: " + BytesUtil.byteArray2HexString(byteStr3))

        val newByteCancel = Reader.txFormatList.drop(2).dropLast(4).toIntArray()
        val newByteStr = ByteArray(4)
        val demo = Crc16Utils.calculateInt(newByteCancel)
        newByteStr[0] = (demo and 0x000000ff).toByte()
        newByteStr[1] = (demo and 0x0000ff00).ushr(8).toByte()

        log("newCancel: " + Integer.toHexString(demo))
        log("newCancel: " + Gson().toJson(newByteStr))
        log("newCancel: " + BytesUtil.byteArray2HexString(newByteStr))*/

        val readerCancel = ReaderCancel()
        val readerBalance = ReaderBalance()
        val readerInfo = ReaderDeviceInfo()

        btnCancel.setOnClickListener {
            readerCancel.cancel()
            tvDisplayTraceNum.text = Reader.sequenceNumber.toString()
        }

        btnBalance.setOnClickListener {
            readerBalance.balance()
            tvDisplayTraceNum.text = Reader.sequenceNumber.toString()
        }

        btnInfo.setOnClickListener {
            readerInfo.info()
            tvDisplayTraceNum.text = Reader.sequenceNumber.toString()
        }
    }
}
