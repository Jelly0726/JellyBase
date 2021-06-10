package com.jelly

import android.content.Context

/**
 * 基础库初始化一些属性
 */
object AppInit {
    lateinit var context:Context
    var isVampix = false //是否App黑白化
    fun init(mContext:Context){
        context=mContext.applicationContext
    }
    fun getPackageName():String{
        if (context==null){
            Throwable("请先初始化")
            return ""
        }
        return context.packageName
    }
}