package com.jelly.baselibrary.androidPicker

import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask
import androidx.fragment.app.FragmentActivity
import com.jelly.baselibrary.addressmodel.Province
import com.jelly.baselibrary.androidPicker.AddressDialog
import com.jelly.baselibrary.moshi.JsonTool
import com.jelly.baselibrary.mprogressdialog.MProgressUtil
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * 获取地址数据并显示地址选择器
 *
 * @author 李玉江[QQ:1032694760]
 * @since 2015/12/15
 */
class AddressDialogTask(activity: Activity?) : CoroutineScope by MainScope() {
    private val activity: WeakReference<Activity?> = WeakReference(activity)
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
        launch{
            MProgressUtil.getInstance().show(activity.get(), "正在初始化数据...")
          val result= async(Dispatchers.IO) {
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
                    data.addAll(JsonTool.get().toList(json, Province::class.java))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
              data
            }
            result.await()?.let {result->
                MProgressUtil.getInstance().dismiss()
                if (result.size > 0) {
                    val addCartDialog = AddressDialog.getInstance()
                    addCartDialog.setData(result)
                    addCartDialog.onAddressPickListener = callback
                    addCartDialog.show(
                        (activity.get() as FragmentActivity?)!!.supportFragmentManager,
                        "addCartDialog"
                    )
                } else {
                    callback!!.onAddressInitFailed()
                }
            }
        }
    }

    interface Callback : AddressDialog.OnAddressPickListener {
        fun onAddressInitFailed()
    }

}