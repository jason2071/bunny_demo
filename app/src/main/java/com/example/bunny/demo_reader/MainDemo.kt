package com.example.bunny.demo_reader

class MainDemo(private val mainInterface: MainInterface) {

    fun setData(number: Int) {
        mainInterface.onRes("is $number")
    }
}