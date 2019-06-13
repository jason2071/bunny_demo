package com.example.bunny.reader.interfaces

import com.example.bunny.reader.model.BaseReaderResponse


interface IReaderResponse {
    fun onResponseSuccess(data: BaseReaderResponse)
}