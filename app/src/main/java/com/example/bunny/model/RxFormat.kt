package com.example.bunny.model

data class RxFormat(
        var RX_DLESTX: ArrayList<Int>
        , var RX_VER: ArrayList<Int>
        , var RX_SessionID: ArrayList<Int>
        , var RX_MSGTYPE: ArrayList<Int>
        , var RX_SN_PACKET: ArrayList<Int>
        , var RX_SN_CURRENT: ArrayList<Int> // change
        , var RX_SN_TOTAL: ArrayList<Int>
        , var RX_CMDID: ArrayList<Int> // static
        , var RX_STATUS: ArrayList<Int>
        , var RX_PayloadType: ArrayList<Int>
        , var RX_PayloadLen: ArrayList<Int> // change
        , var RX_Payload: ArrayList<Int> // change
        , var RX_Checksum: ArrayList<Int> // change
        , var RX_DLEETX: ArrayList<Int>
)