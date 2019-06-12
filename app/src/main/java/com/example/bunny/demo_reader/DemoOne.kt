package com.example.bunny.demo_reader

import com.example.bunny.rabbit.model.WriteModel


class DemoOne : MainInterface {

    private var mainInterface: MainInterface = this
    private lateinit var mainDemo: MainDemo

    private val payload = byteArrayOf(0x01, 0x00, 0x04, 0x00)

    fun cancel() {
        mainDemo = MainDemo(mainInterface)


        val start: MutableList<Byte> = mutableListOf(0x00, 0x00)
        val version: MutableList<Byte> = mutableListOf(0x00, 0x00)
        val sessionId: MutableList<Byte> = mutableListOf(0x00, 0x00, 0x00, 0x00)
        val messageType: Byte = 0x00
        val snPacket: MutableList<Byte> = mutableListOf(0x00, 0x00)
        val snCurrent: MutableList<Byte> = mutableListOf(0x00, 0x00)
        val snTotal: MutableList<Byte> = mutableListOf(0x00, 0x00)
        val commandId: MutableList<Byte> = mutableListOf(0x00, 0x00)
        val statusOrResult: MutableList<Byte> = mutableListOf(0x00, 0x00, 0x00, 0x00)
        val payloadType: MutableList<Byte> = mutableListOf(0x00, 0x00)
        val payloadLen: MutableList<Byte> = mutableListOf(0x00, 0x00)
        val payload: MutableList<Byte> = mutableListOf()
        val checksum: MutableList<Byte> = mutableListOf(0x00, 0x00)
        val stop: MutableList<Byte> = mutableListOf(0x00, 0x00)
        
    }
}