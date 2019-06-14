package com.example.bunny.util

import android.content.Context
import android.util.Log
import com.example.bunny.BuildConfig


/**
 * Created on 2018/9/28.
 * By nesto
 */
private const val TAG = "MainActivityA"

fun Any.log(tag: String = TAG) {
    if (!BuildConfig.DEBUG) return
    if (this is String) {
        Log.v(tag, this)
    } else {
        Log.v(tag, this.toString())
    }
}

fun Any.logd(tag: String = TAG) {
    if (!BuildConfig.DEBUG) return
    if (this is String) {
        Log.d(tag, this)
    } else {
        Log.d(tag, this.toString())
    }
}

fun Any.loge(tag: String = TAG) {
    if (!BuildConfig.DEBUG) return
    if (this is String) {
        Log.e(tag, this)
    } else {
        Log.e(tag, this.toString())
    }
}

fun Context.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

typealias Action = () -> Unit