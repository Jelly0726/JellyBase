package com.jelly.mvvmdemo

import android.os.Bundle
import android.view.View
import androidx.databinding.Observable
import com.jelly.mprogressdialog.MProgressUtil
import com.jelly.mvvmdemo.databinding.MainActivityBinding
import com.jelly.mvvmdemo.viewmodel.MainViewModel
import com.maning.mndialoglibrary.MProgressDialog

class MainActivity : BaseActivity<MainActivityBinding>() {
   lateinit var demoVo:MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MProgressUtil.getInstance().initialize(this)
        demoVo = MainViewModel()
        binding.demoVo = demoVo
        demoVo.isShow.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (demoVo.isShow.get()){//弹出进度条
                    MProgressUtil.getInstance().show(this@MainActivity)
                }else{//关闭进度条
                    MProgressUtil.getInstance().dismiss()
                }
            }
        })
    }

    fun onClick(view: View) {
        println("view=${view.id}")
        demoVo.loadData()
    }
}