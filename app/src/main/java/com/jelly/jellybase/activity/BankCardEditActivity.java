package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.toast.ToastUtils;
import com.base.bankcard.BankCardInfo;
import com.base.httpmvp.contact.BankCartContact;
import com.base.httpmvp.presenter.BankCartPresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.multiClick.AntiShake;
import com.jelly.jellybase.R;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/9/27.
 */

public class BankCardEditActivity extends BaseActivityImpl<BankCartContact.Presenter>
        implements BankCartContact.View {
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.bankCard_id)
    EditText bankCard_id;
    @BindView(R.id.bankCard_name)
    TextView bankCard_name;
    @BindView(R.id.commit_tv)
    TextView commit_tv;

    private BankCardInfo bankCardInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bankcard_edit_activity);
        bankCardInfo= (BankCardInfo) getIntent().getSerializableExtra("bankCardInfo");
        if (bankCardInfo==null){
            finish();
            return;
        }
        ButterKnife.bind(this);
        iniView();
    }
    private void iniView(){
        bankCard_name.setText(bankCardInfo.getBankName()+"·"+bankCardInfo.getType());
        bankCard_id.setText(bankCardInfo.getBankNo());
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
    public BankCartContact.Presenter initPresenter() {
        return new BankCartPresenter(this);
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
                presenter.deletebank(lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
                break;
        }
    }

    @Override
    public Object deletebankParam() {
        Map map=new TreeMap();
        map.put("bankid",bankCardInfo.getBankid());
        return map;
    }

    @Override
    public void deletebankSuccess( Object mCallBackVo) {
        ToastUtils.showToast(this, (String) mCallBackVo);
        finish(2000);
    }

    @Override
    public void deletebankFailed(String message) {
        ToastUtils.showToast(this,message);
    }
}
