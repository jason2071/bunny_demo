package com.example.bunny.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.bunny.manager.Contextor

object LogUtil {
    fun log(s: String) {
        Log.v("MainActivityA", s)
    }
}

object ToastUtil {
    fun toast(s: String) {
        Toast.makeText(Contextor.getInstance().context, s, Toast.LENGTH_SHORT).show()
    }
}