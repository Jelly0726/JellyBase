package com.jelly.mvvmdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

/**
 * @description: recyclerView的Adapter的简单封装, 仅针对单类型viewType
 * @param V: item的xml文件对应的Binding类
 **/
abstract class BaseAdapterB<V : ViewBinding>(context: Context) :
        BaseAdapter<VBindHolder<V>>(context) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VBindHolder<V> {
        //==================使用反射创建对应泛型实例
        val clasType = javaClass.genericSuperclass as ParameterizedType
        clasType?.let { it ->
            for (cl in it.actualTypeArguments) {
                try {
                    cl as Class<V>
                    //多个泛型时判断类的name是否以Binding，不是跳过本次
                    if (!cl.name.endsWith("Binding")) continue
                    val inflate: Method = cl.getDeclaredMethod(
                            "inflate",
                            LayoutInflater::class.java,
                            ViewGroup::class.java,
                            Boolean::class.javaPrimitiveType
                    )
                    val viewBinding = inflate.invoke(null, inflater,parent,false) as V
                    return VBindHolder(viewBinding)
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
            }
        }
        //==================使用反射创建对应泛型实例
        return super.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: VBindHolder<V>, position: Int) {
        onBindViewHolder(holder, position, holder.binding)
    }

    abstract fun onBindViewHolder(holder: VBindHolder<V>, position: Int, binding: V)
}

class VBindHolder<V : ViewBinding>(val binding: V) : RecyclerView.ViewHolder(binding.root) {

}