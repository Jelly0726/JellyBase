package com.base.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.model.Goods;
import com.base.view.FlowLayout;
import com.bumptech.glide.Glide;
import com.jelly.jellybase.R;
import com.jelly.jellybase.shopcar.Utils.UtilTool;
import com.mylhyl.circledialog.AbsBaseCircleDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 加入购物车、立即购买
 * Created by hupei on 2017/4/5.
 */
public class AddCartDialog extends AbsBaseCircleDialog implements View.OnClickListener {
    //确定
    private TextView confirm_tv;
    //取消
    private ImageView cancel_img;
    //商品图片
    private ImageView goods_pic;
    //商品价格
    private TextView goods_price;
    //商品库存
    private TextView goods_repertory;
    //已选分类
    private TextView mark_tv;
    //减数量
    private TextView reduceGoodsNum;
    //购买数量
    private TextView goodsNum;
    //加数量
    private TextView increaseGoodsNum;
    private Goods product;
    private int count=1;
    private OnConfirmListener onConfirmListener;
    private FlowLayout classify_layout;
    private List<String> mList;
    private int checkid=-1;//选择
    public static AddCartDialog getInstance() {
        AddCartDialog dialogFragment = new AddCartDialog();
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(1f);
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.addcart_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        confirm_tv= (TextView) view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(this);
        cancel_img= (ImageView) view.findViewById(R.id.cancel_img);
        cancel_img.setOnClickListener(this);
        goods_pic= (ImageView) view.findViewById(R.id.goods_pic);
        goods_price= (TextView) view.findViewById(R.id.goods_price);
        goods_repertory= (TextView) view.findViewById(R.id.goods_repertory);
        reduceGoodsNum= (TextView) view.findViewById(R.id.reduce_goodsNum);
        mark_tv= (TextView) view.findViewById(R.id.mark_tv);
        goodsNum= (TextView) view.findViewById(R.id.goods_Num);
        increaseGoodsNum= (TextView) view.findViewById(R.id.increase_goods_Num);
        classify_layout= view.findViewById(R.id.classify_layout);

        increaseGoodsNum.setOnClickListener(this);
        reduceGoodsNum.setOnClickListener(this);
        goodsNum.setOnClickListener(this);
        iniData();
    }
    private void iniData(){
        if (product!=null){
            if (!TextUtils.isEmpty(product.getShowImgs())){
                Glide.with(getActivity())
                        .load(product.getShowImgs())
                        .dontAnimate()
                        .centerCrop()
                        .into(goods_pic);
            }
            goods_price.setText("¥："+product.getSalesPrice());
            goods_repertory.setText("库存"+product.getStock()+product.getUnit());
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    public void setData(Goods product){
        this.product=product;
        if (getActivity()!=null)
            iniData();

    }
    /**
     * 初始化
     * */
    private void iniEvaluate(){
        mList=new ArrayList<>();
        mList.add("金典款");
        mList.add("升级款");
        if (mList==null)
            return;
        if(classify_layout.getChildCount()>0){
            classify_layout.removeAllViewsInLayout();
        }
        for (int i = 0; i <mList.size(); i++) {
            CheckBox checkBox = (CheckBox) getLayoutInflater().inflate(
                    R.layout.addcart_classify_check, classify_layout, false);
            checkBox.setText(mList.get(i));
            checkBox.setTag(i);
            if (i==0){
                checkBox.setChecked(true);
                checkid=i;
                mark_tv.setText("已选”"+mList.get(i)+"“");
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        int i=(int) buttonView.getTag();
                        if (checkid!=-1&&checkid!=i){
                            CheckBox compoundButton= (CheckBox) classify_layout.getChildAt(checkid);
                            checkid=i;
                            compoundButton.setChecked(false);
                        }else {
                            checkid=i;
                        }
                        mark_tv.setText("已选”"+mList.get(i)+"“");
                    }else {
                        int i=(int) buttonView.getTag();
                        if (checkid ==-1||checkid==i){
                            checkid=i;
                            mark_tv.setText("已选”"+mList.get(i)+"“");
                            ((CheckBox)buttonView).setChecked(true);
                        }
                    }
                }
            });
            classify_layout.addView(checkBox);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_img:
                dismiss();
                break;
            case R.id.confirm_tv://确定
                if(onConfirmListener!=null){
                    onConfirmListener.OnConfirm(count);
                }
                dismiss();
                break;
            case R.id.increase_goods_Num://加
                doIncrease(goodsNum);
                break;
            case R.id.reduce_goodsNum://减
                doDecrease(goodsNum);
                break;
            case R.id.goods_Num://修改
                showDialog(goodsNum);
                break;
        }
    }
    public OnConfirmListener getOnConfirmListener() {
        return onConfirmListener;
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }
    /**
     * 确定按钮监听
     */
    public interface OnConfirmListener{
        void OnConfirm(int payment);
    }
    /**
     *
     * @param showCountView
     */
    private void showDialog(final View showCountView) {
        final AlertDialog.Builder alertDialog_Builder=new AlertDialog.Builder(getContext());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.shopcar_dialog_change_num,null);
        final AlertDialog dialog=alertDialog_Builder.create();
        dialog.setView(view);//errored,这里是dialog，不是alertDialog_Buidler
        //count=child.getCount();
        final EditText num= (EditText) view.findViewById(R.id.dialog_num);
        num.setText(count+"");
        //自动弹出键盘
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                UtilTool.showKeyboard(getContext(),showCountView);
            }
        });
        final TextView increase= (TextView) view.findViewById(R.id.dialog_increaseNum);
        final TextView DeIncrease=(TextView)view.findViewById(R.id.dialog_reduceNum);
        final TextView pButton= (TextView) view.findViewById(R.id.dialog_Pbutton);
        final TextView nButton= (TextView) view.findViewById(R.id.dialog_Nbutton);
        nButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        pButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number=Integer.parseInt(num.getText().toString().trim());
                if(number==0){
                    dialog.dismiss();
                }else{
                    num.setText(String.valueOf(number));
                    //child.setCount(number);
                    count=number;
                    goodsNum.setText(String.valueOf(number));
                    dialog.dismiss();
                }
            }
        });
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                num.setText(String.valueOf(count));
            }
        });
        DeIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count>1){
                    count--;
                    num.setText(String.valueOf(count));
                }
            }
        });
        dialog.show();
    }
    public void doIncrease(View showCountView) {
//        GoodsInfo good = (GoodsInfo) ;
//        int count = good.getCount();
        count++;
        //good.setCount(count);
        ((TextView) showCountView).setText(String.valueOf(count));
    }
    /**
     * @param showCountView
     */
    public void doDecrease( View showCountView) {
//        GoodsInfo good = (GoodsInfo) ;
//        int count = good.getCount();
        if (count == 1) {
            return;
        }
        count--;
        //good.setCount(count);
        ((TextView) showCountView).setText("" + count);
    }
}