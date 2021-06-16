package com.jelly.baselibrary.androidPicker

import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask
import cn.qqtheme.framework.entity.Province
import cn.qqtheme.framework.picker.AddressPicker
import cn.qqtheme.framework.util.ConvertUtils
import com.alibaba.fastjson.JSON
import com.jelly.baselibrary.mprogressdialog.MProgressUtil
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import java.util.*

/**
 * 获取地址数据并显示地址选择器
 *
 * @author 李玉江[QQ:1032694760]
 * @since 2015/12/15
 */
class AddressPickTask(activity: Activity) : CoroutineScope by MainScope() {
    private val activity: WeakReference<Activity> = WeakReference(activity)
    private var callback: Callback? = null
    private var selectedProvince = ""
    private var selectedCity = ""
    private var selectedCounty = ""
    private var hideProvince = false
    private var hideCounty = false
    fun setHideProvince(hideProvince: Boolean) {
        this.hideProvince = hideProvince
    }

    fun setHideCounty(hideCounty: Boolean) {
        this.hideCounty = hideCounty
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    fun execute(vararg params: String) {
        if (activity.get() == null) {
            callback!!.onAddressInitFailed()
            return
        }
        launch(Dispatchers.Main){
            MProgressUtil.getInstance().show(activity.get(), "正在初始化数据...")
            val data=async(Dispatchers.IO) {
                if (params != null) {
                    when (params.size) {
                        1 -> selectedProvince = params[0]
                        2 -> {
                            selectedProvince = params[0]
                            selectedCity = params[1]
                        }
                        3 -> {
                            selectedProvince = params[0]
                            selectedCity = params[1]
                            selectedCounty = params[2]
                        }
                        else -> {
                        }
                    }
                }
                val data = ArrayList<Province>()
                try {
                    val json = ConvertUtils.toString(
                        activity.get()!!.assets.open("city.json")
                    )
                    data.addAll(JSON.parseArray(json, Province::class.java))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                data
            }
            data.await()?.let {data->
                MProgressUtil.getInstance().dismiss()
                if (data.size > 0) {
                    val picker = AddressPicker(activity.get(), data)
                    picker.setHideProvince(hideProvince)
                    picker.setHideCounty(hideCounty)
                    if (hideCounty) {
                        picker.setColumnWeight(0.8f, 1.0f)
                    } else if (hideProvince) {
                        picker.setColumnWeight(1.0f, 0.8f)
                    } else {
                        picker.setColumnWeight(0.8f, 1.0f, 1.0f)
                    }
                    picker.setSelectedItem(selectedProvince, selectedCity, selectedCounty)
                    picker.setOnAddressPickListener(callback)
                    picker.show()
                } else {
                    callback!!.onAddressInitFailed()
                }
            }
        }
    }

    interface Callback : AddressPicker.OnAddressPickListener {
        fun onAddressInitFailed()
    }

}