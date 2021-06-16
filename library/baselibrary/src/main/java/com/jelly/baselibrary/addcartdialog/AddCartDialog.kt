package com.jelly.baselibrary.addcartdialog

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jelly.baselibrary.AppInit
import com.jelly.baselibrary.R
import com.jelly.baselibrary.applicationUtil.AppUtils
import com.jelly.baselibrary.imageView.ImageViewPlus
import com.jelly.baselibrary.model.Goods
import com.jelly.baselibrary.recyclerViewUtil.ItemDecoration
import com.mylhyl.circledialog.AbsBaseCircleDialog
import java.util.*

/**
 * 加入购物车、立即购买
 * Created by hupei on 2017/4/5.
 */
class AddCartDialog : AbsBaseCircleDialog(), View.OnClickListener {
    //确定
    private lateinit var confirm_tv: TextView
    //取消
    private lateinit var cancel_img: ImageView
    //商品图片
    private lateinit var goods_pic: ImageViewPlus
    //商品价格
    private lateinit var goods_price: TextView
    //商品库存
    private lateinit var goods_repertory: TextView
    //已选分类
    private lateinit var mark_tv: TextView
    //减数量
    private lateinit var reduceGoodsNum: TextView
    //购买数量
    private lateinit var goodsNum: TextView
    //加数量
    private lateinit var increaseGoodsNum: TextView
    private var product: Goods? = null
    private var count = 1
    var onConfirmListener: OnConfirmListener? = null
    private lateinit var mRecyclerView: RecyclerView
    private val checkid = -1 //选择
    private lateinit var mAdapter: AddcartAdapter
    companion object {
        val instance: AddCartDialog
            get() {
                val dialogFragment = AddCartDialog()
                dialogFragment.setCanceledOnTouchOutside(false)
                dialogFragment.setGravity(Gravity.CENTER)
                dialogFragment.setWidth(1f)
                return dialogFragment
            }
    }
    override fun createView(
        context: Context?,
        inflater: LayoutInflater?,
        container: ViewGroup?
    ): View {
        return inflater!!.inflate(R.layout.addcart_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val view = view
        confirm_tv = view!!.findViewById<TextView>(R.id.confirm_tv)
        confirm_tv!!.setOnClickListener(this)
        cancel_img = view!!.findViewById<ImageView>(R.id.cancel_img)
        cancel_img!!.setOnClickListener(this)
        goods_pic = view!!.findViewById<ImageViewPlus>(R.id.goods_pic)
        goods_price = view!!.findViewById<TextView>(R.id.goods_price)
        goods_repertory = view!!.findViewById<TextView>(R.id.goods_repertory)
        reduceGoodsNum = view!!.findViewById<TextView>(R.id.reduce_goodsNum)
        mark_tv = view!!.findViewById<TextView>(R.id.mark_tv)
        goodsNum = view!!.findViewById<TextView>(R.id.goods_Num)
        increaseGoodsNum = view!!.findViewById<TextView>(R.id.increase_goods_Num)
        mRecyclerView = view!!.findViewById<RecyclerView>(R.id.mRecyclerView)
        increaseGoodsNum!!.setOnClickListener(this)
        reduceGoodsNum!!.setOnClickListener(this)
        goodsNum!!.setOnClickListener(this)
        val mRect = Rect()
        mRect.top = AppUtils.dipTopx(AppInit.context, 16f)
        mRect.left = 0
        mRect.right = 0
        mRect.bottom = AppUtils.dipTopx(AppInit.context, 0f)
        val mLayoutManager = LinearLayoutManager(activity)
        val mItemDecoration =
            ItemDecoration(mRect, 1, ItemDecoration.NONE, resources.getColor(R.color.transparent))
        mAdapter = AddcartAdapter(activity!!)
        mRecyclerView!!.isNestedScrollingEnabled = false
        mRecyclerView!!.layoutManager = mLayoutManager
        mRecyclerView!!.addItemDecoration(mItemDecoration)
        mRecyclerView!!.adapter = mAdapter
        val mList = ArrayList<SpecVo>()
        //模拟数据
        for (i in 0..2) {
            val specVo = SpecVo()
            specVo.id = "" + i
            specVo.spec = "颜色$i"
            for (x in 0..8) {
                val spec = SpecVo()
                spec.id = "$i-$x"
                spec.spec = "蔚蓝$x"
                specVo.childs.add(spec)
            }
            mList.add(specVo)
        }
        mAdapter!!.notifyDataSetChanged(mList)
        iniData()
    }

    private fun iniData() {
        if (product != null) {
            if (!TextUtils.isEmpty(product!!.showImgs)) {
                Glide.with(activity!!)
                    .load(product!!.showImgs)
                    .dontAnimate()
                    .centerCrop()
                    .into(goods_pic!!)
            }
            goods_price!!.text = "¥：" + product!!.salesPrice
            goods_repertory!!.text = "库存" + product!!.stock + product!!.unit
        }
    }

    override fun onResume() {
        super.onResume()
    }
    fun setData(product: Goods?) {
        this.product = product
        if (activity != null) iniData()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancel_img -> {
                dismiss()
            }
            R.id.confirm_tv -> { //确定
                if (onConfirmListener != null) {
                    onConfirmListener!!.OnConfirm(count)
                }
                dismiss()
            }
            R.id.increase_goods_Num -> { //加
                doIncrease(goodsNum)
            }
            R.id.reduce_goodsNum -> { //减
                doDecrease(goodsNum)
            }
            R.id.goods_Num -> { //修改
    //            showDialog(goodsNum)
            }
        }
    }

    /**
     * 确定按钮监听
     */
    interface OnConfirmListener {
        fun OnConfirm(payment: Int)
    }

    /**
     *
     * @param showCountView
     */
    private fun showDialog(showCountView: View?) {
        val alertDialog_Builder = AlertDialog.Builder(
            context!!
        )
        val view = LayoutInflater.from(context).inflate(R.layout.addcart_dialog_change_num, null)
        val dialog = alertDialog_Builder.create()
        dialog.setView(view) //errored,这里是dialog，不是alertDialog_Buidler
        //count=child.getCount();
        val num = view.findViewById<EditText>(R.id.dialog_num)
        num.setText(count.toString() + "")
        //自动弹出键盘
        dialog.setOnShowListener {
            showKeyboard(
                context, showCountView
            )
        }
        val increase = view.findViewById<TextView>(R.id.dialog_increaseNum)
        val DeIncrease = view.findViewById<TextView>(R.id.dialog_reduceNum)
        val pButton = view.findViewById<TextView>(R.id.dialog_Pbutton)
        val nButton = view.findViewById<TextView>(R.id.dialog_Nbutton)
        nButton.setOnClickListener { dialog.dismiss() }
        pButton.setOnClickListener {
            val number = num.text.toString().trim { it <= ' ' }.toInt()
            if (number == 0) {
                dialog.dismiss()
            } else {
                num.setText(number.toString())
                //child.setCount(number);
                count = number
                goodsNum!!.text = number.toString()
                dialog.dismiss()
            }
        }
        increase.setOnClickListener {
            count++
            num.setText(count.toString())
        }
        DeIncrease.setOnClickListener {
            if (count > 1) {
                count--
                num.setText(count.toString())
            }
        }
        dialog.show()
    }

    fun doIncrease(showCountView: View?) {
//        GoodsInfo good = (GoodsInfo) ;
//        int count = good.getCount();
        count++
        //good.setCount(count);
        (showCountView as TextView?)!!.text = count.toString()
    }

    /**
     * @param showCountView
     */
    fun doDecrease(showCountView: View?) {
//        GoodsInfo good = (GoodsInfo) ;
//        int count = good.getCount();
        if (count == 1) {
            return
        }
        count--
        //good.setCount(count);
        (showCountView as TextView?)!!.text = "" + count
    }

    private fun hideKeyboard(mcontext: Context, view: ViewGroup) {
        view.requestFocus()
        val im = mcontext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        try {
            im.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showKeyboard(mcontext: Context?, view: View?) {
        val im = mcontext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.showSoftInput(view, 0)
    }
}