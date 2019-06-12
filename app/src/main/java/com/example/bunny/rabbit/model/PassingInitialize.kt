package com.example.bunny.rabbit.model

/**
 *  passing data to ReaderInitialize class
 */

data class PassingInitialize(
        var merchID8: MutableList<Byte>
        , var locateID4: MutableList<Byte>
        , var termID4: MutableList<Byte>
        , var serviceID4: MutableList<Byte>
)