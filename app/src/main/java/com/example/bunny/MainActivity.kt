package com.example.bunny

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.bunny.demo.SubManager
import com.example.bunny.reader.base.Reader
import com.example.bunny.reader.used.DemoReaderBalance
import com.example.bunny.reader.used.DemoReaderCancel
import com.example.bunny.reader.used.DemoReaderDeviceInfo
import com.example.bunny.reader.used.DemoReaderInitialize
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val readerCancel = DemoReaderCancel()
        val readerBalance = DemoReaderBalance()
        val readerInfo = DemoReaderDeviceInfo()
        val readerInitialize = DemoReaderInitialize()

        btnInitialize.setOnClickListener {
            //readerInitialize.initialize()
        }

        btnInfo.setOnClickListener {
            readerInfo.info()
            tvDisplayTraceNum.text = Reader.termStan.toString()
        }

        btnCancel.setOnClickListener {
            readerCancel.cancel()
            tvDisplayTraceNum.text = Reader.termStan.toString()
        }

        btnBalance.setOnClickListener {
            readerBalance.balance()
            //tvDisplayTraceNum.text = Reader.termStan.toString()
        }
    }

    private fun demoReverse(data: Any): MutableList<*> {
        data as MutableList<*>
        return data.reversed() as MutableList<*>
    }
}
