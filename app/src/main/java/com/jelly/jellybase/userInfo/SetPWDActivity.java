package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.applicationUtil.MD5;
import com.base.applicationUtil.MyApplication;
import com.base.applicationUtil.ToastUtils;
import com.base.config.IntentAction;
import com.base.httpmvp.contact.SetPwdContact;
import com.base.httpmvp.presenter.SetPassWordActivityPresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.multiClick.AntiShake;
import com.base.sqldao.DBHelper;
import com.jelly.jellybase.R;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/9/28.
 */

public class SetPWDActivity extends BaseActivityImpl<SetPwdContact.Presenter>
        implements SetPwdContact.View {

    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.next_tv)
    TextView next_tv;
    @BindView(R.id.password_edit)
    EditText password_edit;
    @BindView(R.id.password1_edit)
    EditText password1_edit;
    private String phone;
    private String password;
    private String password1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setpwd_activity);
        // 进行id绑定
        ButterKnife.bind(this);
        phone=getIntent().getStringExtra("phone");
    }
    @OnClick({ R.id.next_tv, R.id.left_back})
    public void onClick(View v) {
        if (AntiShake.check(v.getId())) {    //判断是否多次点击
            return;
        }
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.next_tv:
                String password=password_edit.getText().toString().trim();
                String password1=password1_edit.getText().toString().trim();
                if (TextUtils.isEmpty(password)||TextUtils.isEmpty(password1)){
                    ToastUtils.showToast(this,"请输入密码!");
                    return;
                }
                if (!password.equals(password1)){
                    ToastUtils.showToast(this,"两次密码输入不一致,请重新输入!");
                    return;
                }
                presenter.setPassword(lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public SetPwdContact.Presenter initPresenter() {
        return new SetPassWordActivityPresenter(this);
    }

    @Override
    public Object getSetPassWordParam() {
        String password=password_edit.getText().toString().trim();
        password= MD5.MD5Encode(password);
        Map map=new TreeMap();
        map.put("salesphone",phone);
        map.put("password",password);
        return map;
    }

    @Override
    public void excuteSuccess( Object mCallBackVo) {
        DBHelper.getInstance(MyApplication.getMyApp()).clearLogin();
        MyApplication.getMyApp().finishAllActivity();
        String password=password_edit.getText().toString().trim();
        Intent intent = new Intent();
        //intent.setClass(this, LoginActivity.class);
        intent.putExtra("phone",phone);
        intent.putExtra("password",password);
        intent.putExtra("from",0);
        intent.setAction(IntentAction.ACTION_LOGIN);
        startActivity(intent);
        finish();
    }

    @Override
    public void excuteFailed( String message) {
        ToastUtils.showToast(this,message);
    }
}
