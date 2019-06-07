package com.example.bunny.reader.base

import com.example.bunny.reader.model.RxFormat
import com.example.bunny.reader.model.TxFormat

object Reader {

    // save sequence number
    var termStan = 0

    // save txFormat
    lateinit var txFormat: TxFormat

    // save rxFormat
    lateinit var rxFormat: RxFormat

    // save txFormatList
    var txFormatList = ArrayList<Byte>()

    // save rxFormatList
    var rxFormatList = ArrayList<Byte>()
}