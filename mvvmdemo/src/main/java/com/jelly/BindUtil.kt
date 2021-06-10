package com.jelly

import android.app.Activity
import android.app.Dialog
import android.view.View
import com.jelly.annotation.Event
import com.jelly.multiClick.AntiShake
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import java.util.regex.Pattern

/**
 * 对注解OnClick解析
 * 适用于 activity和fragment中绑定点击事件
 * 注：无法在library库使用会提示错误: 元素值必须为常量表达式
 * */
class BindUtil {

    companion object {
        fun bind(target: Activity) {
            val sourceView: View = target.window.decorView
            bind(target, sourceView)
        }

        fun bind(target: Dialog) {
            val sourceView: View = target.window!!.decorView
            bind(target, sourceView)
        }

        fun bind(target: View) {
            bind(target, target)
        }

        fun bind(target: Any, source: Activity) {
            val sourceView = source.window.decorView
            bind(target, sourceView)
        }

        fun bind(target: Any, source: Dialog) {
            val sourceView = source.window!!.decorView
            bind(target, sourceView)
        }

        fun bind(target: Any, sourceView: View) {
            // 1. 开头是一样的，获取到class对象
            val clazz: Class<*> = target.javaClass;
            // 2. 获取到所有的方法
            val declaredMethods: Array<Method> = clazz.declaredMethods;
            // 3. 遍历所有的方法
            for (method in declaredMethods) {
                method.isAccessible = true;
                // 4. 获取方法上的注解, 因为一个方法上面可能会有多个注解，所以要获取所有的注解
                val annotations = method.annotations
                // 5. 遍历方法上面的注解
                for (annotation in annotations) {
                    annotation as Annotation
                    // 6. 获取这个注解上面的注解类(也就是OnClick注解的class)
                    val aClass = annotation.annotationClass.java
                    // 7. 根据OnClick注解的class，获取EventBus注解
                    val eventBus = aClass.getAnnotation(Event::class.java) ?: continue
                    // 8. 判断如果有EventBus注解，才代表的是事件注解，进行处理
                    // 9. 获取EventBus注解的值
                    val eventType = eventBus.targetType;
                    val eventMethodName = eventBus.setter;
                    try {
                        // 10. 通过反射拿到方法注解中的值(这里就是所有view的id数组)
                        val isCk = aClass.getDeclaredMethod("isCheck");
                        val isCheck = isCk.invoke(annotation) ?: continue
                        val ids = aClass.getDeclaredMethod("value");
                        val viewIds = ids.invoke(annotation) ?: continue
                        viewIds as IntArray
                        // 11. 遍历id数组
                        for (viewId in viewIds) {
                            if (viewId == View.NO_ID) continue
                            // 12. 获取view
                            val findViewMethod =
                                sourceView.javaClass.getMethod("findViewById", Int::class.java);
                            val view = findViewMethod.invoke(sourceView, viewId) ?: continue
                            view as View
                            // 13. 如果有这个view，才进行处理
                            // 14. 动态代理，代理事件类型，交给我们的方法来处理
                            val proxy = Proxy.newProxyInstance(target.javaClass.classLoader,
                                arrayOf(eventType.java),
                                InvocationHandler { _, _, args ->
                                    // 执行当前activity中的方法，参数不能少，需要跟原事件方法参数一样
                                    val view = args[0]
                                    isCheck as Boolean
                                    if (isCheck) {//做防重复校验
                                        if (AntiShake.check(view)) {
                                            //重复点击
                                            return@InvocationHandler this
                                        }
                                    }
                                    //获取方法的参数类型数组。用于确定参数数量
                                    val typePars = method.parameterTypes
                                    if (typePars.size == 1)
                                        method.invoke(target, view)
                                    else if (typePars.isEmpty())
                                        method.invoke(target)
                                    else method.invoke(target, args)
                                })
                            // 获取activity中的事件方法
                            val activityEventMethod =
                                view.javaClass.getMethod(eventMethodName, eventType.java)
                            // 15. 当这个方法执行的时候，自动执行代理方法
                            activityEventMethod.invoke(view, proxy)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * @Description: 将map的Key大写转换成小写
     * @Param: list
     * @return:
     */
    fun upperCaseToLowerCase(list: List<Map<String, Any?>>): List<Map<String, Any?>>? {
        val objects: MutableList<Map<String, Any?>> = ArrayList()
        try {
            for (map in list) {
                val stringObjectHashMap: MutableMap<String, Any?> = HashMap()
                for (oldKey in map.keys) {
                    //key是旧的key值
                    val newKey = oldKey.toLowerCase()
                    //调用lineToHump方法，将下划线转成驼峰式
                    val s = lineToHump(newKey)
                    stringObjectHashMap[s] = map[oldKey]
                }
                objects.add(stringObjectHashMap)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return objects
    }

    private val linePattern = Pattern.compile("_(\\w)")

    /**
     * @Description: 下划线转驼峰
     * @Param:
     * @return:
     */
    fun lineToHump(str: String): String {
        var str = str
        str = str.toLowerCase()
        val matcher = linePattern.matcher(str)
        val sb = StringBuffer()
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase())
        }
        matcher.appendTail(sb)
        return sb.toString()
    }
}