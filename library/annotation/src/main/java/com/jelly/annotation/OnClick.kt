package com.jelly.annotation

import android.view.View
import androidx.annotation.IdRes


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Event(
        targetType = View.OnClickListener::class
        , setter = "setOnClickListener"
)
annotation class OnClick(
        val isCheck:Boolean=false,//是否启用防重复点击校验
        @IdRes vararg val value: Int = [View.NO_ID]//设置点击事件的id集合
)
