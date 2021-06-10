package com.jelly.mvvmdemo

import android.os.Bundle
import android.view.View
import com.jelly.mvvmdemo.databinding.MainActivityBinding

class MainActivity : BaseActivity<MainActivityBinding>() {
   lateinit var demoVo:DemoVo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        demoVo = DemoVo()
        demoVo.text="健康的是否会客户"
        binding.demoVo = demoVo
    }

    fun onClick(view: View) {
        println("view=${view.id}")
        demoVo.text="的是否结束看来苦加入公会"
    }
}