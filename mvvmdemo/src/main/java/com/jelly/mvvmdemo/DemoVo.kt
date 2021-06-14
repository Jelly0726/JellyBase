package com.jelly.mvvmdemo

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class DemoVo():BaseObservable(){
    @Bindable
    var label:String=""
    @Bindable
    var image:String=""
}
