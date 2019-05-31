package com.example.bunny.base

import com.example.bunny.model.TxFormat

object Reader {

    // save sequence number
    var sequenceNumber = 0

    // save txFormat
    lateinit var txFormat: TxFormat

    // save txFormatList
    var txFormatList = ArrayList<Int>()

    // save rxFormatList
    var rxFormatList = ArrayList<Int>()
}