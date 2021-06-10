package com.jelly.mvvmdemo

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class DemoVo():BaseObservable(){
    var id:String=""
    @Bindable
    var text:String=""
    set(value) {
        field=value
        notifyPropertyChanged(BR.text)
    }
}
