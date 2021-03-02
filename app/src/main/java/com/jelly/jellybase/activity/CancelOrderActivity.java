package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.httpmvp.mvpContact.CancelOrderContact;
import com.base.httpmvp.mvpPresenter.CancelOrderPresenter;
import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.base.multiClick.AntiShake;
import com.jelly.jellybase.R;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.DialogParams;
import com.trello.rxlifecycle3.android.ActivityEvent;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.SinglePicker;
import io.reactivex.ObservableTransformer;

public class CancelOrderActivity extends BaseActivityImpl<CancelOrderContact.View,CancelOrderContact.Presenter>
        implements CancelOrderContact.View{
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.commit_btn)
    Button commit_btn;
    @BindView(R.id.orderNo_tv)
    TextView orderNo_tv;
    @BindView(R.id.time_tv)
    TextView time_tv;
    @BindView(R.id.order_product_img)
    ImageView order_product_img;
    @BindView(R.id.order_product_name)
    TextView order_product_name;
    @BindView(R.id.product_price)
    TextView product_price;
    @BindView(R.id.num_tv)
    TextView num_tv;
    @BindView(R.id.order_price)
    TextView order_price;
    @BindView(R.id.cancelCause_tv)
    TextView cancelCause_tv;
    @BindView(R.id.cause_layout)
    LinearLayout cause_layout;
    @BindView(R.id.cancelCause_edit)
    EditText cancelCause_edit;
    @BindView(R.id.causeNum_tv)
    TextView causeNum_tv;
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
    public int getLayoutId(){
        return R.layout.cancelorder_activity;
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
        cause_layout.setVisibility(View.GONE);
        cancelCause_edit.addTextChangedListener(new TextWatcher() {
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
                    causeNum_tv.setText(ss.length()+"/100");
                }
            }
        });
    }
    @OnClick({R.id.left_back,R.id.commit_btn,R.id.cancelCause_tv})
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
        cancelCause_tv.setText(cancelCause);
        causePicker = new SinglePicker<>(this, cause);
        causePicker.setCanceledOnTouchOutside(false);
        causePicker.setSelectedIndex(0);
        causePicker.setCycleDisable(true);
        causePicker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                cancelCause=item;
                cancelCause_tv.setText(cancelCause);
                if (cancelCause.trim().equals(cause[cause.length-1])){
                    cause_layout.setVisibility(View.VISIBLE);
                }else {
                    cause_layout.setVisibility(View.GONE);
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
