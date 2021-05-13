package com.jelly.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FIELD)
public annotation class EventMethod(
        /** 此注释所应用的侦听器方法的名称  */
        val name: String,
        /** 方法参数列表。如果类型不是原始，则必须是完全限定的。 */
        vararg val parameters: String = [],
        /** 侦听器方法的原始或完全限定返回类型。也可以是“void”。 */
        val returnType: String = "void",
        /** 如果[。returnType]不是' void '当没有绑定存在时返回该值。 */
        val defaultReturn: String = "null"
)
