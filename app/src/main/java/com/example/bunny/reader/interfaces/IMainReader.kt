package com.example.bunny.reader.interfaces

import com.example.bunny.reader.model.BaseReaderResponse


interface IMainReader {
    fun onInitReaderData()
    fun onResponse(res: BaseReaderResponse)
}