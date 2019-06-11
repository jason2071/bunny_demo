package com.example.bunny.rabbit.model

open class BaseResponse {
    var status = false
    var errorCode = ByteArray(4)
    var writeDataList = mutableListOf<Byte>()
    var readDataList = mutableListOf<Byte>()
}