package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.applicationUtil.ToastUtils;
import com.base.countdowntimerbtn.CountDownTimerButton;
import com.base.httpmvp.contact.ForgetPwdContact;
import com.base.httpmvp.presenter.ForgetPasswordPresenter;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.multiClick.AntiShake;
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

public class ForgetActivity extends BaseActivityImpl<ForgetPwdContact.Presenter>
        implements ForgetPwdContact.View {
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.next_tv)
    TextView next_tv;
    @BindView(R.id.btn_get_ver)
    CountDownTimerButton get_ver_btn;
    @BindView(R.id.phone_edit)
    EditText phone_edit;
    @BindView(R.id.verificationCode_edit)
    EditText verificationCode_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_forget_activity);
        // 进行id绑定
        ButterKnife.bind(this);
        iniView();
        initCountDownBtn();
    }
    private void iniView(){
    }
    private void initCountDownBtn() {
        get_ver_btn= (CountDownTimerButton) findViewById(R.id.btn_get_ver);
        get_ver_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=phone_edit.getText().toString().trim();
                if (TextUtils.isEmpty(phone))
                {
                    ToastUtils.showToast(ForgetActivity.this,"请输入手机号");
                    return;
                }
                presenter.getVerifiCode(lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
                get_ver_btn.setStartCountDownText("再次获取");//设置倒计时开始时按钮上的显示文字
                get_ver_btn.startCountDownTimer(60000,1000);//设置倒计时时间，间隔
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        get_ver_btn.onDestroy();
    }

    @Override
    public ForgetPwdContact.Presenter initPresenter() {
        return new ForgetPasswordPresenter(this);
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
                String phone=phone_edit.getText().toString().trim();
                String verificationCode=verificationCode_edit.getText().toString().trim();
                if (TextUtils.isEmpty(phone)||TextUtils.isEmpty(verificationCode))
                {
                    ToastUtils.showToast(ForgetActivity.this,"请输入手机号和验证码");
                    return;
                }
                presenter.forgetPwd(lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
                break;
        }
    }

    @Override
    public Object getVerifiCodeParam() {
        String phone=phone_edit.getText().toString().trim();
        Map map=new TreeMap<>();
        map.put("phone",phone);
        map.put("flag",2);//验证码标识：1注册，2忘记密码，3修改手机号
        return map;
    }

    @Override
    public void verifiCodeSuccess( Object mCallBackVo) {
        HttpResult httpResultAll= (HttpResult)mCallBackVo;
        ToastUtils.showToast(this,httpResultAll.getMsg());
    }

    @Override
    public void verifiCodeFailed( String message) {
        ToastUtils.showToast(this,message);
    }

    @Override
    public Object forgetPasswordParam() {
        String phone=phone_edit.getText().toString().trim();
        String verificationCode=verificationCode_edit.getText().toString().trim();
        Map map=new TreeMap<>();
        map.put("phone",phone);
        map.put("vericode",verificationCode);
        return map;
    }

    @Override
    public void forgetPasswordSuccess( Object mCallBackVo) {
        String phone=phone_edit.getText().toString().trim();
        Intent intent=new Intent(this,RefeshSetPWDActivity.class);
        intent.putExtra("phone",phone);
        startActivity(intent);
    }

    @Override
    public void forgetPasswordFailed(String message) {
        ToastUtils.showToast(this,message);
    }
}
