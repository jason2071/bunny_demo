package com.example.bunny

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.bunny.demo_reader.DemoOne
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    private lateinit var demoOne: DemoOne

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        demoOne = DemoOne()
        initInstance()
    }

    private fun initInstance() {
        btnInitialize.setOnClickListener {
            demoOne.cancel()
        }

        btnInfo.setOnClickListener {
        }

        btnCancel.setOnClickListener {}

        btnBalance.setOnClickListener {}
    }

}
