package com.example.bunny.rabbit.model

data class ReaderResponse(
        var status: Boolean = false
        , var writeDataList: MutableList<Byte> = arrayListOf()
        , var readDataList: MutableList<Byte> = arrayListOf()
        , var errorCode: MutableList<Byte> = arrayListOf()
        , var info: InfoResponse? = null
        , var auth: AuthResponse? = null
        , var time: TimeResponse? = null
        , var mode: ModeResponse? = null

)

data class InfoResponse(
        var deviceID4: MutableList<Byte>
        , var merchID8: MutableList<Byte>
        , var firmVer4: MutableList<Byte>
        , var appVer4: MutableList<Byte>
        , var sAMVer4: MutableList<Byte>
        , var pollTO4: MutableList<Byte>
        , var authTO4: MutableList<Byte>
)

data class AuthResponse(
        var randomNumber: MutableList<Byte>
        , var encryptData: MutableList<Byte>
        , var decryptData: MutableList<Byte>
)

data class TimeResponse(
        var readerTime: MutableList<Byte>
)

data class ModeResponse(
        var mode1: Byte
)