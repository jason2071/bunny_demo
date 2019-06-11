package com.example.bunny

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.bunny.mockup_data.Failure
import com.example.bunny.mockup_data.Success
import com.example.bunny.mockup_data.UserResult
import com.example.bunny.rabbit.used.ReaderCancel
import com.example.bunny.rabbit.used.ReaderGetTime
import com.example.bunny.rabbit.used.ReaderInitialize
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val keyValue = byteArrayOf(0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initInstance()


    }

    private fun initInstance() {
        val cancel = ReaderCancel()
        val initialize = ReaderInitialize()
        val getTime = ReaderGetTime()

        btnInitialize.setOnClickListener {
            valid(Success(listOf("one","two")))
        }

        btnInfo.setOnClickListener {
            valid(Failure("Welcome!"))
        }

        btnCancel.setOnClickListener {}

        btnBalance.setOnClickListener {}
    }

    private fun valid(result: UserResult): String {
        return when(result) {
            is Success -> result.users.size.toString()
            is Failure -> result.message
        }
    }
}
