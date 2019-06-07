package com.example.bunny.reader.model


data class TxFormat(
        var TX_DLESTX: ArrayList<Byte>
        , var TX_VER: ArrayList<Byte>
        , var TX_SessionID: ArrayList<Byte>
        , var TX_MSGTYPE: Byte
        , var TX_SN_PACKET: ArrayList<Byte>
        , var TX_SN_CURRENT: ArrayList<Byte> // change
        , var TX_SN_TOTAL: ArrayList<Byte>
        , var TX_CMDID: ArrayList<Byte> // static
        , var TX_STATUS: ArrayList<Byte>
        , var TX_PayloadType: ArrayList<Byte>
        , var TX_PayloadLen: ArrayList<Byte> // change
        , var TX_Payload: ArrayList<Byte> // change
        , var TX_Checksum: ArrayList<Byte> // change
        , var TX_DLEETX: ArrayList<Byte>
)