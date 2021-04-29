package com.jelly.jellybase.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.multiClick.OnMultiClickListener;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.RechargeActivityBinding;
import com.jelly.jellybase.datamodel.PayTypePicker;
import com.jelly.jellybase.weixinpay.PayUtil;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.SinglePicker;


/**
 * Created by JELLY on 2017/9/26.
 */

public class RechargeActivity extends BaseActivity<RechargeActivityBinding> {
    /**
     * 微信支付回调
     */
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 开始监听，注册广播
         */
        if (broadcastReceiver != null) {
            IntentFilter mFilter = new IntentFilter();
            mFilter.addAction(PayUtil.WX_RECHARGE_SUCCESS);
            registerReceiver(broadcastReceiver, mFilter);
        }
        iniView();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 停止监听，注销广播
         */
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    private void iniView(){
        getViewBinding().leftBack.setOnClickListener(listener);
        getViewBinding().changePay.setOnClickListener(listener);
    }
    private OnMultiClickListener listener=new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()){
                case R.id.left_back:
                    finish();
                    break;
                case R.id.change_pay:
                    onSinglePicker();
                    break;
            }
        }
    };
    public void onSinglePicker() {
        List<PayTypePicker> data = new ArrayList<>();
        data.add(new PayTypePicker(1,"使用支付宝充值",R.drawable.icon_alipay));
        data.add(new PayTypePicker(2,"使用微信充值",R.drawable.weixin_icon));
        data.add(new PayTypePicker(3,"使用银联充值",R.drawable.yinlian));
        SinglePicker<PayTypePicker> picker = new SinglePicker<>(this, data);
        picker.setCanceledOnTouchOutside(false);
        picker.setSelectedIndex(1);
        picker.setCycleDisable(true);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<PayTypePicker>() {
            @Override
            public void onItemPicked(int index, PayTypePicker item) {
                //showToast("index=" + index + ", id=" + item.getId() + ", name=" + item.getName());
                getViewBinding().payLogo.setImageResource(item.getPayLogo());
                getViewBinding().payName.setText(item.getTitle());
            }
        });
        picker.show();
    }

}
