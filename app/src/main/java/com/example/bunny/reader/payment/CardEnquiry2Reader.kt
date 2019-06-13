package com.example.bunny.reader.payment

import com.example.bunny.reader.interfaces.IMainReader
import com.example.bunny.reader.interfaces.IReaderResponse
import com.example.bunny.reader.manager.ReaderManager
import com.example.bunny.reader.model.BaseReaderResponse

class CardEnquiry2Reader(private val iReaderResponse: IReaderResponse) : IMainReader {

    private val iMainReader: IMainReader = this
    private val readerManager: ReaderManager = ReaderManager(iMainReader)

    private val payload = byteArrayOf(0xF1.toByte(), 0x12, 0x00, 0x00)
    private val commandId = byteArrayOf(0x09, 0)

    private var errorCode = mutableListOf<Byte>()

    override fun onInitReaderData() {
    }

    override fun onResponse(res: BaseReaderResponse) {
    }
}