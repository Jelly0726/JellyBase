package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.applicationUtil.ToastUtils;
import com.base.bankcard.BandCardEditText;
import com.base.httpmvp.presenter.AddBankPresenter;
import com.base.httpmvp.view.IAddBankView;
import com.base.mprogressdialog.MProgressUtil;
import com.base.multiClick.AntiShake;
import com.base.view.MyActivity;
import com.jelly.jellybase.R;
import com.maning.mndialoglibrary.MProgressDialog;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jelly.jellybase.MainActivity.login;


/**
 * Created by Administrator on 2017/9/27.
 */

public class AddBankCardActivity extends MyActivity implements IAddBankView {
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


    private AddBankPresenter addBankPresenter;
    private MProgressDialog progressDialog;
    private String bankName="没有查到所属银行";
    private String bankType="储蓄卡";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bankcard);
        ButterKnife.bind(this);
        iniView();
        iniProgress();
        addBankPresenter=new AddBankPresenter(this);
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
    private void iniProgress(){
        progressDialog= MProgressUtil.getInstance().getMProgressDialog(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog=null;
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
                addBankPresenter.addBank(lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
                break;
        }
    }

    @Override
    public void showProgress() {
        if (progressDialog!=null){
            progressDialog.show();
        }
    }

    @Override
    public void closeProgress() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public Object addBankParam() {
        String phone=phone_edit.getText().toString().trim();
        String cardholder=cardholder_edit.getText().toString().trim();
        String bankCard=bankCard_id.getCardNo().toString().trim();
        Map map=new TreeMap();
        map.put("saleid",login.getUserID()+"");
        map.put("phone",phone);
        map.put("bankname",bankName);
        map.put("bankno",bankCard);
        map.put("bankuser",cardholder);
        map.put("type",bankType);
        map.put("isdefault",false);
        return map;
    }

    @Override
    public void addBankSuccess(boolean isRefresh, Object mCallBackVo) {
        ToastUtils.showToast(this, (String) mCallBackVo);
        finish(2000);
    }

    @Override
    public void addBankFailed(boolean isRefresh, String message) {
        ToastUtils.showToast(this,message);
    }
}
