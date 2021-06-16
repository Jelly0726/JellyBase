package com.jelly.baselibrary.addcartdialog

import android.os.Parcel
import android.os.Parcelable

/**
 * 规格数据模型
 */
class SpecVo() :Parcelable{
    var id:String=""
    var spec:String=""
    var childs=ArrayList<SpecVo>()

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        spec = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(spec)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SpecVo> {
        override fun createFromParcel(parcel: Parcel): SpecVo {
            return SpecVo(parcel)
        }

        override fun newArray(size: Int): Array<SpecVo?> {
            return arrayOfNulls(size)
        }
    }
}