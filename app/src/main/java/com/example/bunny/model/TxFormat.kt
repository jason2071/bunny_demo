package com.example.bunny.model


data class TxFormat(
        var TX_DLESTX: ArrayList<Int>
        , var TX_VER: ArrayList<Int>
        , var TX_SessionID: ArrayList<Int>
        , var TX_MSGTYPE: String
        , var TX_SN_PACKET: ArrayList<Int>
        , var TX_SN_CURRENT: ArrayList<Int>
        , var TX_SN_TOTAL: ArrayList<Int>
        , var TX_CMDID: ArrayList<Int>
        , var TX_STATUS: ArrayList<Int>
        , var TX_PayloadType: ArrayList<Int>
        , var TX_PayloadLen: ArrayList<Int>
        , var TX_Payload: ArrayList<Int>
        , var TX_Checksum: ArrayList<Int>
        , var TX_DLEETX: ArrayList<Int>
)