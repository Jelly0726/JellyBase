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
        @IdRes vararg val value: Int = [View.NO_ID]
)
