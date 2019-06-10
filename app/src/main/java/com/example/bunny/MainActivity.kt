package com.example.bunny

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.bunny.rabbit.used.ReaderAuth
import com.example.bunny.util.LogUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val keyValue = byteArrayOf(0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //initInstance()

        var payload = byteArrayOf(
                0x00, 0x01, 0x00, 0x00
                , 0x30, 0x01, 0xFE.toByte(), 0x54, 0x1C, 0x4B, 0x6E, 0x58
                , 0x8A.toByte(), 0xF9.toByte(), 0xBE.toByte(), 0x37, 0x01, 0x00, 0x00, 0x02)

        val decrypt = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)

        val writeData = payload.dropLast(8).toMutableList()
        writeData.addAll(decrypt.toMutableList())

        LogUtil.log("payload: ${Gson().toJson(payload)}")
        LogUtil.log("writeData: ${Gson().toJson(writeData)}")

        payload = writeData.toByteArray()
        LogUtil.log("result: ${Gson().toJson(payload)}")


    }

    private fun initInstance() {
        val readerAuth = ReaderAuth()

        btnInitialize.setOnClickListener {
            readerAuth.auth(keyValue)
        }

        btnInfo.setOnClickListener {}

        btnCancel.setOnClickListener {}

        btnBalance.setOnClickListener {}
    }
}
