package com.example.bunny.reader.model

data class RxFormat(
        var RX_DLESTX: ArrayList<Byte>
        , var RX_VER: ArrayList<Byte>
        , var RX_SessionID: ArrayList<Byte>
        , var RX_MSGTYPE: Byte
        , var RX_SN_PACKET: ArrayList<Byte>
        , var RX_SN_CURRENT: ArrayList<Byte> // change
        , var RX_SN_TOTAL: ArrayList<Byte>
        , var RX_CMDID: ArrayList<Byte> // static
        , var RX_RESULT: ArrayList<Byte>
        , var RX_PayloadType: ArrayList<Byte>
        , var RX_PayloadLen: ArrayList<Byte> // change
        , var RX_Payload: ArrayList<Byte> // change
        , var RX_Checksum: ArrayList<Byte> // change
        , var RX_DLEETX: ArrayList<Byte>
)