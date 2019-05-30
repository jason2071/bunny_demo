package com.example.bunny.model

class ReaderManager {

    private lateinit var txFormat: TxFormat
    private var termStan = 0

    fun readerCancel() {
        val payload = arrayListOf(0xF1, 0x12, 0x04, 0x00)
        var retStatus = false

        readerSetTxPacket()
        readerSetTraceNum()
        txFormat.TX_CMDID = arrayListOf(0xAA, 0x00)

        txFormat.TX_PayloadType.add(payload[0])
        txFormat.TX_PayloadType.add(payload[1])

        txFormat.TX_PayloadLen.add(payload[2])
        txFormat.TX_PayloadLen.add(payload[3])
    }

    private fun readerSetTxPacket() {
        txFormat = TxFormat(
                arrayListOf(0x10, 0x02)
                , arrayListOf(0x01, 0)
                , arrayListOf(0, 0, 0, 0)
                , ""
                , arrayListOf(0x01, 0x00)
                , arrayListOf(0x01, 0x00)
                , arrayListOf(0x01, 0x00)
                , arrayListOf()
                , arrayListOf(0x00, 0x00, 0x00, 0x00)
                , arrayListOf(0x00, 0x00)
                , arrayListOf()
                , arrayListOf()
                , arrayListOf()
                , arrayListOf(0x10, 0x03)
        )
    }

    private fun readerSetTraceNum() {
        termStan++
        if (termStan == 0xFFFF) {
            termStan = 1
        }
        txFormat.TX_SN_PACKET = arrayListOf((termStan and 0xff), (termStan and 0xff00 shr 8))
    }

    private fun norm2LittleEndian() {
        
    }
}