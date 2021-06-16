package com.jelly

import android.app.Application
import com.jelly.mprogressdialog.MProgressUtil

class BaseApplication:Application() {
    companion object {
        private lateinit var myApp: BaseApplication
        /**
         * 获取本app可用的Context
         *
         * @return 返回一个本类的context
         */
        @JvmStatic
        val instance: BaseApplication
            get() {
                if (myApp == null) {
                    myApp = BaseApplication()
                }
                return myApp
            }
    }
    override fun onCreate() {
        super.onCreate()
        myApp = this
        AppInit.init(this)
        MProgressUtil.getInstance().initialize(this)
    }
}