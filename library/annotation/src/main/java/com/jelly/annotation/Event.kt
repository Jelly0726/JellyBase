package com.jelly.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Event(
        /**事件类型*/
        val targetType: KClass<*>,
        /**[目标类型][上的setter方法的名称。targetType]用于侦听器。*/
        val setter: String
)
