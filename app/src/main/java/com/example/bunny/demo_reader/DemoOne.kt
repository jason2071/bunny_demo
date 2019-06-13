package com.example.bunny.demo_reader

import com.example.bunny.util.LogUtil


class DemoOne : MainInterface {

    private val mainInterface: MainInterface = this
    private val mainDemo = MainDemo(mainInterface)

    private var firstLoad = false

    override fun onLoad() {
        mainDemo.setData(1)
    }

    override fun onRes(message: String) {

        if (!firstLoad) {

            firstLoad = true

            LogUtil.log("onRes")
            mainDemo.setData(2)
        } else {
            LogUtil.log("fin")
        }

        LogUtil.log(message)
    }
}