package com.example.bunny

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.bunny.base.Reader
import com.example.bunny.data.DataSource
import com.example.bunny.reader.ReaderBalance
import com.example.bunny.reader.ReaderCancel
import com.example.bunny.reader.ReaderDeviceInfo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        log("static: " + Gson().toJson(DataSource.writeCancelInt()))

        tvDisplayTraceNum.text = Reader.sequenceNumber.toString()

        val readerCancel = ReaderCancel()
        readerCancel.cancel()
        log("dynamic: " + Gson().toJson(Reader.txFormatList))

        /*val readerBalance = ReaderBalance()
        val readerInfo = ReaderDeviceInfo()

        btnCancel.setOnClickListener {
            readerCancel.cancel()
            tvDisplayTraceNum.text = Reader.sequenceNumber.toString()
            log(Gson().toJson(Reader.txFormatList))
        }

        btnBalance.setOnClickListener {
            readerBalance.balance()
            tvDisplayTraceNum.text = Reader.sequenceNumber.toString()
        }

        btnInfo.setOnClickListener {
            readerInfo.info()
            tvDisplayTraceNum.text = Reader.sequenceNumber.toString()
        }*/
    }

    private fun log(s: String) {
        Log.d("MainActivityA", s)
    }
}
