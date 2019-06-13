package com.example.bunny.rabbit.base

import com.example.bunny.rabbit.model.ReadModel
import com.example.bunny.rabbit.model.WriteModel


object RabbitObject {

    // for send data
    var writeModel = WriteModel()
    var writeDataList = mutableListOf<Byte>()

    // for receive data
    var readModel = ReadModel()
    var readDataList = mutableListOf<Byte>()

    // save sequence number
    var traceNumber = 0

    // data type
    const val ACK1: Byte = 0x01
    const val NAK: Byte = 0x02
    const val CNX: Byte = 0x03
    const val ACK4: Byte = 0x04
    const val ACK5: Byte = 0x05
    const val ACK6: Byte = 0x06
    const val ACK1_5: Byte = 0x07
}