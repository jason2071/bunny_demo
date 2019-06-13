package com.example.bunny

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.bunny.demo_reader.DemoOne
import com.example.bunny.reader.read_write.CancelReader
import com.example.bunny.reader.interfaces.IReaderResponse
import com.example.bunny.reader.model.AuthReaderResponse
import com.example.bunny.reader.model.BaseReaderResponse
import com.example.bunny.reader.model.DeviceInfoReaderResponse
import com.example.bunny.reader.model.TimeReaderResponse
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IReaderResponse {

    private var iReaderResponse: IReaderResponse = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //initInstance()

        val demoOne = DemoOne()
        demoOne.onLoad()

    }

    private fun initInstance() {

        val cancelReader = CancelReader(iReaderResponse)

        btnInitialize.setOnClickListener {
        }

        btnCancel.setOnClickListener {
            cancelReader.onInitReaderData()
        }

        btnInfo.setOnClickListener {}

        btnBalance.setOnClickListener {}
    }

    override fun onResponseSuccess(data: BaseReaderResponse) {
        data.status
        data.writeDataList
        data.readDataList
        data.errorCode

        when (data.result) {
            is AuthReaderResponse -> {}
            is DeviceInfoReaderResponse -> {}
            is TimeReaderResponse -> {}
        }
    }
}
