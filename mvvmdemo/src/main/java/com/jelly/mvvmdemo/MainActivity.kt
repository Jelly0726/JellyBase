package com.jelly.mvvmdemo

import android.os.Bundle
import android.view.View
import androidx.databinding.Observable
import com.jelly.mvvmdemo.databinding.MainActivityBinding
import com.jelly.mvvmdemo.viewmodel.MainViewModel

class MainActivity : BaseActivity<MainActivityBinding>() {
   lateinit var demoVo:MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        demoVo = MainViewModel()
        demoVo.text="健康的是否会客户"
        binding.demoVo = demoVo
        demoVo.isShow.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (demoVo.isShow.get()){//弹出进度条

                }else{//关闭进度条

                }
            }
        })
    }

    fun onClick(view: View) {
        println("view=${view.id}")
        demoVo.text="的是否结束看来苦加入公会"
    }
}