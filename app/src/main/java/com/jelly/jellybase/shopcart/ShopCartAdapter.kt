package com.luolifen.easybio.shopcart

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.base.SwipeRefresh.stick.StickAdapter
import com.base.Utils.StringUtil
import com.base.appManager.BaseApplication
import com.base.applicationUtil.AppUtils
import com.base.imageView.ImageViewPlus
import com.bumptech.glide.Glide
import com.jelly.jellybase.R
import com.jelly.jellybase.shopcart.CartInfo
import com.mylhyl.circledialog.CircleDialog
import com.mylhyl.circledialog.callback.ConfigButton
import com.mylhyl.circledialog.callback.ConfigDialog
import com.mylhyl.circledialog.callback.ConfigInput
import com.mylhyl.circledialog.callback.ConfigTitle
import com.mylhyl.circledialog.params.ButtonParams
import com.mylhyl.circledialog.params.DialogParams
import com.mylhyl.circledialog.params.InputParams
import com.mylhyl.circledialog.params.TitleParams

class ShopCartAdapter(context: Context) :
    RecyclerView.Adapter<ShopCartAdapter.GroupViewHolder>(),
    StickAdapter<ShopCartAdapter.GroupViewHolder> {
    private val context:Context
    private var mListItems: List<CartInfo>? = null
    var checkInterface: CheckInterface? = null
    var modifyCountInterface: ModifyCountInterface? = null
    private var circleDialog: CircleDialog.Builder? = null
    init {
        this@ShopCartAdapter.context=context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.select_box.setOnClickListener { v ->
            mListItems!![position].isSelect = (v as CheckBox).isChecked
            holder.select_box.isChecked = v.isChecked
            if (checkInterface != null)
                checkInterface!!.checkChild(position, v.isChecked)
        }
        holder.add_img.setOnClickListener {
            if (modifyCountInterface != null)
                modifyCountInterface!!.doIncrease(
                    position,
                    holder.num_edit,
                    holder.select_box.isChecked
                )
        }
        holder.sub_img.setOnClickListener {
            if (modifyCountInterface != null)
                modifyCountInterface!!.doDecrease(
                    position,
                    holder.num_edit,
                    holder.select_box.isChecked
                )
        }
        holder.num_edit.setOnClickListener {
            if (circleDialog == null) {
                synchronized(context) {
                    if (circleDialog == null) {
                        circleDialog = CircleDialog.Builder()
                            .configDialog(object : ConfigDialog {
                                override fun onConfig(params: DialogParams) {
                                    params.width = 0.6f
                                }
                            })
                            .setCanceledOnTouchOutside(false)
                            .setCancelable(false)
                            .setTitle("编辑数量")
                            .configTitle(object : ConfigTitle {
                                override fun onConfig(params: TitleParams) {
                                    params.textSize =
                                        AppUtils.spTopx(BaseApplication.getInstance(), 15f)
                                    params.textColor =
                                        ContextCompat.getColor(context, R.color.mainText)
                                }
                            })
                            .configInput(object : ConfigInput {
                                override fun onConfig(params: InputParams) {
                                    params.type = InputParams.INPUT_MONEY
                                    params.digits = 0
                                    params.text = holder.num_edit.text.toString()
                                    params.textSize =
                                        AppUtils.spTopx(BaseApplication.getInstance(), 15f)
                                    params.textColor =
                                        ContextCompat.getColor(context, R.color.mainText)
                                }
                            })
                            .setInputHeight(AppUtils.dipTopx(BaseApplication.getInstance(), 40f))
                            .configPositive(object : ConfigButton {
                                override fun onConfig(params: ButtonParams) {
                                    params.textSize =
                                        AppUtils.spTopx(BaseApplication.getInstance(), 15f)
                                    params.textColor =
                                        ContextCompat.getColor(context, R.color.mainText)
                                }
                            })
                            .setPositiveInput("确定") { text, v ->
                                if (!StringUtil.isEmpty(text)) {
                                    circleDialog = null
                                    //                                            mListItems.get(position).setGoodsNum(Integer.parseInt(text));
                                    //                                            holder.num_edit.setText(text);
                                    if (modifyCountInterface != null)
                                        modifyCountInterface!!.doUpdate(
                                            position,
                                            Integer.parseInt(text),
                                            holder.num_edit,
                                            holder.select_box.isChecked
                                        )
                                    true
                                }else
                                false
                            }
                        circleDialog!!.show((context as AppCompatActivity).supportFragmentManager)
                    }
                }
            }
        }
        if (mListItems!![position].goodsType == "beautyPackage") {
            holder.name_tv.text = StringUtil.setIconToText(
                0,
                mListItems!![position].name,
                R.mipmap.ic_combo,
                AppUtils.dipTopx(BaseApplication.getInstance(), 15f)
            )
        } else {
            holder.name_tv.text = mListItems!![position].name
        }
        holder.price_tv.text = "¥" + mListItems!![position].price
        holder.select_box.isChecked = mListItems!![position].isSelect
        holder.num_edit.setText(mListItems!![position].goodsNum.toString() + "")
        if (!TextUtils.isEmpty(mListItems!![position].majorImage)) {
            Glide.with(context)
                .load( mListItems!![position].majorImage)
                .placeholder(R.drawable.ic_placeholder_figure)
                .error(R.drawable.ic_placeholder_figure)
                .dontAnimate()
                .into(holder.photo_img)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_NON_STICKY
    }

    override fun getItemCount(): Int {
        return if (mListItems == null) 0 else mListItems!!.size
    }

    override fun getHeaderCount(): Int {
        return 0
    }

    fun notifyDataSetChanged(newItems: List<CartInfo>) {
        mListItems = newItems
        notifyDataSetChanged()
    }

    override fun isPinnedViewType(viewType: Int): Boolean {
        return viewType == VIEW_TYPE_STICKY
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        public val select_box: CheckBox
        public val photo_img: ImageViewPlus
        public val name_tv: TextView
        public val price_tv: TextView
        public val add_img: ImageView
        public val num_edit: EditText
        public val sub_img: ImageView

        init {
            select_box = itemView.findViewById(R.id.select_box)
            photo_img = itemView.findViewById(R.id.photo_img)
            name_tv = itemView.findViewById<View>(R.id.name_tv) as TextView
            price_tv = itemView.findViewById<View>(R.id.price_tv) as TextView
            add_img = itemView.findViewById(R.id.add_img)
            num_edit = itemView.findViewById(R.id.num_edit)
            sub_img = itemView.findViewById(R.id.sub_img)
        }
    }

    /**
     * 店铺的复选框
     */
    public interface CheckInterface {
        /**
         * 子选框状态改变触发的事件
         *
         * @param childPosition 子元素的位置
         * @param isChecked     子元素的选中与否
         */
        fun checkChild(childPosition: Int, isChecked: Boolean)
    }


    /**
     * 改变数量的接口
     */
    public interface ModifyCountInterface {
        /**
         * 增加操作
         *
         * @param childPosition 子元素的位置
         * @param showCountView 用于展示变化后数量的View
         * @param isChecked     子元素选中与否
         */
        fun doIncrease(childPosition: Int, showCountView: View, isChecked: Boolean)

        fun doDecrease(childPosition: Int, showCountView: View, isChecked: Boolean)

        fun doUpdate(childPosition: Int, count: Int, showCountView: View, isChecked: Boolean)

        /**
         * 删除子Item
         *
         * @param childPosition
         */
        fun childDelete(childPosition: Int)
    }

    companion object {
        internal val VIEW_TYPE_NON_STICKY = R.layout.shopcart_activity_item
        internal val VIEW_TYPE_STICKY = R.layout.shopcart_activity_item
    }

}


