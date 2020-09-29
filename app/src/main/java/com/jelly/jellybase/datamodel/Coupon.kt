package com.jelly.jellybase.datamodel

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Administrator on 2017/10/17.
 */
@SuppressLint("ParcelCreator")// 用于处理 Lint 的错误提示
@Parcelize
data class Coupon(
        var id:String="",//ID
        var name:String=""//名称
): Parcelable{

}