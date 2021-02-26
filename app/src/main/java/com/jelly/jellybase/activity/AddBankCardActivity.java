package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.bankcard.BandCardEditText;
import com.base.httpmvp.contact.AddBankCartContact;
import com.base.httpmvp.presenter.AddBankPresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.multiClick.AntiShake;
import com.base.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableTransformer;


/**
 * Created by Administrator on 2017/9/27.
 */

public class AddBankCardActivity extends BaseActivityImpl<AddBankCartContact.View,AddBankCartContact.Presenter>
        implements AddBankCartContact.View {
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.cardholder_edit)
    EditText cardholder_edit;
    @BindView(R.id.phone_edit)
    EditText phone_edit;
    @BindView(R.id.bankCard_id)
    BandCardEditText bankCard_id;
    @BindView(R.id.bankCard_name)
    TextView bankCard_name;
    @BindView(R.id.default_cb)
    CheckBox default_cb;
    @BindView(R.id.commit_tv)
    TextView commit_tv;


    private String bankName="没有查到所属银行";
    private String bankType="储蓄卡";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.bankcard_add_activity;
    }
    private void iniView(){
        bankCard_id.setBankCardListener(new BandCardEditText.BankCardListener() {
            @Override
            public void success(String name,String type) {
                bankCard_name.setText(name+"·"+type);
                bankName=name;
                bankType=type;
            }

            @Override
            public void failure() {
                bankCard_name.setText("没有查到所属银行");
                bankName="没有查到所属银行";
                bankType="储蓄卡";
            }
        });
    }
    @Override
    public void onBackPressed() {
        closeProgress();
        super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public AddBankCartContact.Presenter initPresenter() {
        return new AddBankPresenter();
    }

    @Override
    public AddBankCartContact.View initIBView() {
        return this;
    }

    @Override
    public <T> ObservableTransformer<T, T> bindLifecycle() {
        return lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY);
    }
    @OnClick({R.id.left_back,R.id.commit_tv})
    public void onClick(View v) {
        if (AntiShake.check(v.getId())) {    //判断是否多次点击
            return;
        }
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.commit_tv:
                String phone=phone_edit.getText().toString().trim();
                String cardholder=cardholder_edit.getText().toString().trim();
                String bankCard=bankCard_id.getText().toString().trim();
                if (TextUtils.isEmpty(phone)||TextUtils.isEmpty(cardholder)||
                        TextUtils.isEmpty(bankCard)){
                    ToastUtils.showToast(this,"手机号、持卡人、卡号不能为空！");
                    return;
                }
                presenter.addBank();
                break;
        }
    }

    @Override
    public Object addBankParam() {
        String phone=phone_edit.getText().toString().trim();
        String cardholder=cardholder_edit.getText().toString().trim();
        String bankCard=bankCard_id.getCardNo().toString().trim();
        Map map=new TreeMap();
        map.put("phone",phone);
        map.put("bankname",bankName);
        map.put("bankno",bankCard);
        map.put("bankuser",cardholder);
        map.put("type",bankType);
        map.put("isdefault",false);
        return map;
    }

    @Override
    public void addBankSuccess( Object mCallBackVo) {
        ToastUtils.showToast(this, (String) mCallBackVo);
        finish(2000);
    }

    @Override
    public void addBankFailed( String message) {
        ToastUtils.showToast(this,message);
    }
}
