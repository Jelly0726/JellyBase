package com.jelly.mvvmdemo.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel

/**
 * 主页的viewModel
 */
class MainViewModel:ViewModel() {
    var isShow=ObservableBoolean()
    @Bindable
    var text:String=""
}