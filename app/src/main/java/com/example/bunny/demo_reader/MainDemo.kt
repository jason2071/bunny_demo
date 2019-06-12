package com.example.bunny.demo_reader

import com.example.bunny.api.SerialPort
import com.example.bunny.manager.Contextor
import com.example.bunny.rabbit.model.WriteModel

class MainDemo(private val mainInterface: MainInterface) {
    private var mContext = Contextor.getInstance().context.applicationContext
    private var serialPort = SerialPort.getInstance()


    fun initReaderData() {

    }

    fun setWritePacket() {

    }

    fun setTraceNum(termStan: Int) {

    }

    fun setTxPacketList() {

    }

}