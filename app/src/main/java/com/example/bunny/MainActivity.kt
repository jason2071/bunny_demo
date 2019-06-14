package com.example.bunny

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.bunny.reader.interfaces.IReaderResponse
import com.example.bunny.reader.model.AuthReaderResponse
import com.example.bunny.reader.model.BaseReaderResponse
import com.example.bunny.reader.model.DeviceInfoReaderResponse
import com.example.bunny.reader.model.TimeReaderResponse
import com.example.bunny.reader.read_write.CancelReader
import com.example.bunny.util.log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IReaderResponse {

    private var iReaderResponse: IReaderResponse = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //initInstance()

        val data = DemoAppend(mutableListOf(0, 1, 2, 3), 9, mutableListOf(4, 5, 6, 7))
        val result = itemAppend(data)
        "result: $result".log()
        "subList 0-3 ${result.subList(0, 3)}".log()
        "subList 3-5 ${result.subList(3, 5)}".log()

    }

    data class DemoAppend(var list1: MutableList<Byte> = mutableListOf(), var msg: Byte = 0, var list2: MutableList<Byte> = mutableListOf())

    private fun itemAppend(data: DemoAppend): MutableList<Byte> {
        val items = mutableListOf<Byte>()
        items += data.list1
        items += data.msg
        items += data.list2
        return items
    }

    private fun nullCompare(buffer: MutableList<Byte>): Boolean {
        val value = buffer.find { it > 0 }
        return value == null
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
            is AuthReaderResponse -> {
            }
            is DeviceInfoReaderResponse -> {
            }
            is TimeReaderResponse -> {
            }
        }
    }
}
