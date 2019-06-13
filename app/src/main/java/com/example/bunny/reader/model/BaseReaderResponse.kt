package com.example.bunny.reader.model


data class BaseReaderResponse(
        var status: Boolean = false
        , var writeDataList: MutableList<Byte> = mutableListOf()
        , var readDataList: MutableList<Byte> = mutableListOf()
        , var errorCode: MutableList<Byte> = mutableListOf()
        , var result: Any? = null
)

data class TimeReaderResponse(
        var readerTime7: MutableList<Byte> = mutableListOf()
)

data class DeviceInfoReaderResponse(
        var deviceID4: MutableList<Byte> = mutableListOf()
        , var merchID8: MutableList<Byte> = mutableListOf()
        , var firmVer4: MutableList<Byte> = mutableListOf()
        , var appVer4: MutableList<Byte> = mutableListOf()
        , var sAMVer4: MutableList<Byte> = mutableListOf()
        , var pollTO4: MutableList<Byte> = mutableListOf()
        , var authTO4: MutableList<Byte> = mutableListOf()
)

data class AuthReaderResponse(
        var randomNumber: MutableList<Byte>
        , var encryptData: MutableList<Byte>
        , var decryptData: MutableList<Byte>
)