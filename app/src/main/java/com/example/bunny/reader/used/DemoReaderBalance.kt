package com.example.bunny.reader.used

import com.example.bunny.reader.base.BaseDemoReader
import com.example.bunny.util.LogUtil

class DemoReaderBalance : BaseDemoReader() {

    fun balance() {
        val payload: ArrayList<Byte> = arrayListOf(0x01, 0x02, 0x03, 0x04)
        LogUtil.log("original: $payload")

        val reverse = norm2LittleEndian(payload)
        LogUtil.log("reverse: $reverse")

        val toNormal = littleEndian2Norm(reverse)
        LogUtil.log("toNormal: $toNormal")

    }
}