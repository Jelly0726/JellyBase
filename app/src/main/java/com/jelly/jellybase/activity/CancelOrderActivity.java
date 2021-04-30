package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.CancelorderActivityBinding;
import com.jelly.mvp.contact.CancelOrderContact;
import com.jelly.mvp.presenter.CancelOrderPresenter;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.DialogParams;
import com.trello.rxlifecycle3.android.ActivityEvent;

import cn.qqtheme.framework.picker.SinglePicker;
import io.reactivex.ObservableTransformer;

public class CancelOrderActivity extends BaseActivityImpl<CancelOrderContact.View
        ,CancelOrderContact.Presenter, CancelorderActivityBinding>
        implements CancelOrderContact.View, View.OnClickListener {
    private String[] cause;
    private String cancelCause;
    private SinglePicker causePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
        iniCause();
    }
    @Override
    public CancelOrderContact.Presenter initPresenter() {
        return new CancelOrderPresenter();
    }
    @Override
    public CancelOrderContact.View initIBView() {
        return this;
    }

    @Override
    public <T> ObservableTransformer<T, T> bindLifecycle() {
        return lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY);
    }
    private void iniView(){
        getBinding().leftBack.setOnClickListener(this);
        getBinding().commitBtn.setOnClickListener(this);
        getBinding().cancelCauseTv.setOnClickListener(this);
        getBinding().causeLayout.setVisibility(View.GONE);
        getBinding().cancelCauseEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String ss=s.toString();
                if (!TextUtils.isEmpty(ss)){
                    getBinding().causeNumTv.setText(ss.length()+"/100");
                }
            }
        });
    }
    public void onClick(View v){
        if (AntiShake.check(v.getId()))return;
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.commit_btn:
                new CircleDialog.Builder()
                        .configDialog(new ConfigDialog() {
                            @Override
                            public void onConfig(DialogParams params) {
                                params.width=0.6f;
                            }
                        })
                        .setCanceledOnTouchOutside(false)
                        .setCancelable(false)
                        .setTitle("取消订单?")
                        .setText("确定要取消订单？")
                        .setNegative("取消", null)
                        .setPositive("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .show(getSupportFragmentManager());
                break;
            case R.id.cancelCause_tv:
                if (causePicker!=null)
                    causePicker.show();
                break;
        }
    }
    private void iniCause(){
        cause= getResources().getStringArray(R.array.cause);
        cancelCause=cause[0];
        getBinding().cancelCauseTv.setText(cancelCause);
        causePicker = new SinglePicker<>(this, cause);
        causePicker.setCanceledOnTouchOutside(false);
        causePicker.setSelectedIndex(0);
        causePicker.setCycleDisable(true);
        causePicker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                cancelCause=item;
                getBinding().cancelCauseTv.setText(cancelCause);
                if (cancelCause.trim().equals(cause[cause.length-1])){
                    getBinding().causeLayout.setVisibility(View.VISIBLE);
                }else {
                    getBinding().causeLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public Object cancelOrderParam() {
        return null;
    }

    @Override
    public void cancelOrderSuccess(Object mCallBackVo) {

    }

    @Override
    public void cancelOrderFailed(String message) {

    }
}
