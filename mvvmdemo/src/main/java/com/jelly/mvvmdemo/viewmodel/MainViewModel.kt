package com.jelly.mvvmdemo.viewmodel

import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.databinding.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jelly.mvvmdemo.DemoVo

/**
 * 主页的viewModel
 */
class MainViewModel:ViewModel(){
    var isShow=ObservableBoolean()
    var mLisr=ObservableArrayList<DemoVo>()
//    @Bindable
    var text=ObservableField<String>("健康的是否会客户")
    @BindingAdapter("bindImage")
    public fun bindImage(imageView: ImageView,  imgdir:MutableLiveData<String>){
        if (!imgdir.getValue().equals("")){
            imageView.setImageBitmap(BitmapFactory.decodeFile(imgdir.getValue()));
        }
    }
    fun loadData(){
        isShow.set(true)
        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            text.set("的是否结束看来苦加入公会")
            isShow.set(false)
            for (i in 0..3){
                mLisr.add(DemoVo())
            }
        },2000)
    }
    fun loadMoreData(){
        isShow.set(true)
        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            isShow.set(false)
            for (i in 0..3){
                mLisr.add(DemoVo())
            }
        },2000)
    }
}