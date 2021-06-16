package com.jelly.baselibrary.addcartdialog

import android.content.Context
import android.util.SparseArray
import android.widget.CheckBox
import com.jelly.baselibrary.BaseAdapterB
import com.jelly.baselibrary.R
import com.jelly.baselibrary.VBindHolder
import com.jelly.baselibrary.databinding.AddcartClassifyCheckBinding
import com.jelly.baselibrary.databinding.AddcartDialogItemBinding

class AddcartAdapter(context: Context):BaseAdapterB<AddcartDialogItemBinding>(context) {
    private val mList=ArrayList<SpecVo>()
    val selectMap=SparseArray<SpecVo>()//保存选中的规格信息
    val selectPos=SparseArray<Int>()//保存选中的控件下标
    override fun onBindViewHolder(
        holder: VBindHolder<AddcartDialogItemBinding>,
        position: Int,
        binding: AddcartDialogItemBinding
    ) {
        val item=mList[position]
        binding.titleTv.text=item.spec
        if (binding.classifyLayout.childCount > 0) {
            binding.classifyLayout.removeAllViewsInLayout()
        }
        for (i in item.childs.indices) {
            val checkBoxBinding= AddcartClassifyCheckBinding.inflate(inflater,binding.classifyLayout,false)
            checkBoxBinding.checkBox.text = item.childs[i].spec
            checkBoxBinding.checkBox.tag = i
            if (i == 0 && position==0) {
                checkBoxBinding.checkBox.isChecked = true
                selectPos.put(position,i)
                selectMap.put(position,item.childs[i])
//                mark_tv.setText("已选”" + mList[i] + "“")
            }
            checkBoxBinding.checkBox.setOnClickListener {view->
                view as CheckBox
                val i = view.tag as Int
                if (view.isChecked) {
                    if (selectPos.get(position,-1) != -1
                        && selectPos.get(position,-1) != i) {
                        val compoundButton = binding.classifyLayout.getChildAt(selectPos.get(position,-1)) as CheckBox
                        compoundButton.isChecked = false
                    }
                    selectPos.put(position,i)
                    selectMap.put(position,item.childs[i])
//                    mark_tv.setText("已选”" + mList[i] + "“")
                } else {
                    if (selectPos.get(position,-1) == -1
                        || selectPos.get(position,-1) == i) {
                        selectPos.put(position,i)
                        selectMap.put(position,item.childs[i])
//                        mark_tv.setText("已选”" + mList[i] + "“")
                        view.isChecked = true
                    }
                }
            }
            binding.classifyLayout.addView(checkBoxBinding.root)
        }
    }

    override fun notifyDataSetChanged(dataList: List<*>) {
        selectMap.clear()
        selectPos.clear()
        mList.clear()
        mList.addAll(dataList as ArrayList<SpecVo>)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}