package com.example.bunny.reader.model

data class CardModel(
        var cscID5: MutableList<Byte> = mutableListOf()
        , var cardType1: Byte = 0
        , var cardStatus1: Byte = 0
        , var purseAmt6: MutableList<Byte> = mutableListOf()
        , var expd2: MutableList<Byte> = mutableListOf()
        , var lastUsageExpd4: MutableList<Byte> = mutableListOf()
        , var blacklist1: Byte = 0
        , var lTxnTime4: MutableList<Byte> = mutableListOf()
        , var lTxnID1: Byte = 0
        , var lTxnSpID2: MutableList<Byte> = mutableListOf()
        , var lTxnRwID4: MutableList<Byte> = mutableListOf()
        , var lTxnAmt: MutableList<Byte> = mutableListOf()
        , var lTxnPurse: MutableList<Byte> = mutableListOf()
        , var lTxnBonus2: MutableList<Byte> = mutableListOf()
        , var lTxnRemainTripNo2: MutableList<Byte> = mutableListOf()
        , var lTxnLocateCode2: MutableList<Byte> = mutableListOf()
        , var lTxnEntryDateTime4: MutableList<Byte> = mutableListOf()
        , var lTxnEntryLocateCode2: MutableList<Byte> = mutableListOf()
        , var lTxnEntryEquipNo2: MutableList<Byte> = mutableListOf()
        , var lTxnExitEquipNo2: MutableList<Byte> = mutableListOf()
        , var batchID4: MutableList<Byte> = mutableListOf()
)