package com.base.passwordView;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.countdowntimerbtn.CountDownTimerButton;
import com.base.passwordView.view.MDProgressBar;
import com.base.passwordView.view.PasswordKeyboard;
import com.base.passwordView.view.PasswordView;
import com.jelly.jellybase.R;

import java.util.Map;
import java.util.TreeMap;


/**
 * Created by chenjiawei on 16/8/30.
 * 防支付宝密码输入界面
 */
public class PasswordKeypad extends DialogFragment implements View.OnClickListener, PasswordKeyboard.OnPasswordInputListener,
        MDProgressBar.OnPasswordCorrectlyListener {

    private TextView title;
    private TextView errorMsgTv;
    private  TextView cancelTv;
    private  TextView back_dialog;
    private  TextView msg_verify;

    private Callback mCallback;

    private RelativeLayout passwordContainer;
    private RelativeLayout msg_content;

    private MDProgressBar progressBar;

    private PasswordView passwordView;
    private PasswordView msg_inputBox;

    private int passwordCount;//密码长度
    private int msgCount;//验证长度

    private boolean passwordState = true;
    private boolean isPassword = true;//是否支付密码
    private int keyType=0;//0 只用支付密码，1 只用短信验证码 2使用支付密码或短信验证码

    PasswordKeyboard numberKeyBoard;
    PasswordKeyboard msg_keyboard;
    private CountDownTimerButton msg_password;

    private StringBuffer mPasswordBuffer = new StringBuffer();

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallback = (Callback) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.password_keypad, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window window = getDialog().getWindow();
        //去掉边框
        window.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        window.setLayout(dm.widthPixels, window.getAttributes().height);
        window.setWindowAnimations(R.style.exist_menu_animstyle);
        window.setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = (TextView) view.findViewById(R.id.title);
        errorMsgTv = (TextView) view.findViewById(R.id.error_msg);
        TextView forgetPasswordTv = (TextView) view.findViewById(R.id.forget_password);
        msg_verify = (TextView) view.findViewById(R.id.msg_verify);
        cancelTv = (TextView) view.findViewById(R.id.cancel_dialog);
        back_dialog = (TextView) view.findViewById(R.id.back_dialog);
        back_dialog.setVisibility(View.GONE);

        msg_content = (RelativeLayout) view.findViewById(R.id.msg_content);
        passwordContainer = (RelativeLayout) view.findViewById(R.id.password_content);
        progressBar = (MDProgressBar) view.findViewById(R.id.password_progressBar);
        progressBar.setOnPasswordCorrectlyListener(this);
        passwordView = (PasswordView) view.findViewById(R.id.password_inputBox);
        //设置密码长度
        if (passwordCount > 0) {
            passwordView.setPasswordCount(passwordCount);
        }
        msg_inputBox = (PasswordView) view.findViewById(R.id.msg_inputBox);
        //设置验证码长度
        if (msgCount > 0) {
            msg_inputBox.setPasswordCount(msgCount);
        }else if (passwordCount > 0){
            msg_inputBox.setPasswordCount(passwordCount);
        }
        msg_password= (CountDownTimerButton) view.findViewById(R.id.msg_password);
        msg_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });
        numberKeyBoard = (PasswordKeyboard) view.findViewById(R.id.password_keyboard);
        numberKeyBoard.setOnPasswordInputListener(this);
        msg_keyboard = (PasswordKeyboard) view.findViewById(R.id.msg_keyboard);
        msg_keyboard.setOnPasswordInputListener(this);

        cancelTv.setOnClickListener(this);
        forgetPasswordTv.setOnClickListener(this);
        msg_verify.setOnClickListener(this);
        back_dialog.setOnClickListener(this);

        if (keyType==0||keyType==2) {
            isPassword=true;
            passwordState = true;
            title.setText("请输入交易密码");
            passwordContainer.setVisibility(View.VISIBLE);
            msg_content.setVisibility(View.GONE);
            back_dialog.setVisibility(View.GONE);
            cancelTv.setVisibility(View.VISIBLE);
            if (keyType==2) {
                msg_verify.setVisibility(View.VISIBLE);
            }else {
                msg_verify.setVisibility(View.GONE);
            }
        }else if (keyType==1){
            isPassword=false;
            passwordState = true;
            title.setText("请输入短信验证码");
            passwordContainer.setVisibility(View.GONE);
            msg_content.setVisibility(View.VISIBLE);
            back_dialog.setVisibility(View.GONE);
            cancelTv.setVisibility(View.VISIBLE);
            msg_verify.setVisibility(View.GONE);
        }
    }

    /**
     * 设置密码长度
     */
    public void setPasswordCount(int passwordCount) {
        this.passwordCount = passwordCount;
    }
    /**
     * 设置短信验证码长度
     */
    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_dialog:
                if (mCallback != null) {
                    mCallback.onCancel();
                }
                dismiss();
                break;
            case R.id.forget_password:
                if (mCallback != null) {
                    mCallback.onForgetPassword();
                }
                break;
            case R.id.msg_verify:
                isPassword=false;
                passwordState = true;
                title.setText("请输入短信验证码");
                passwordContainer.setVisibility(View.GONE);
                msg_content.setVisibility(View.VISIBLE);
                back_dialog.setVisibility(View.VISIBLE);
                cancelTv.setVisibility(View.GONE);
                sendMsg();
                break;
            case R.id.back_dialog:
                isPassword=true;
                passwordState = true;
                title.setText("请输入交易密码");
                passwordContainer.setVisibility(View.VISIBLE);
                msg_content.setVisibility(View.GONE);
                back_dialog.setVisibility(View.GONE);
                cancelTv.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setCallback(Callback callBack) {
        this.mCallback = callBack;
    }

    public void setPasswordState(boolean correct) {
        setPasswordState(correct, "");
    }

    public void setPasswordState(boolean correct, String msg) {
        passwordState = correct;
        if (correct) {
            progressBar.setSuccessfullyStatus();
        } else {
            progressBar.setVisibility(View.GONE);
            if (isPassword) {
                numberKeyBoard.resetKeyboard();
                passwordView.clearPassword();
                passwordContainer.setVisibility(View.VISIBLE);
                msg_content.setVisibility(View.GONE);
            }else {
                msg_keyboard.resetKeyboard();
                msg_inputBox.clearPassword();
                passwordContainer.setVisibility(View.GONE);
                msg_content.setVisibility(View.VISIBLE);
            }
            errorMsgTv.setText(msg);
        }
    }

    @Override
    public void onPasswordCorrectly() {
        if (mCallback != null) {
            mCallback.onPasswordCorrectly();
        }
    }

    private void startLoading(CharSequence password) {
        if (isPassword) {
            passwordContainer.setVisibility(View.INVISIBLE);
            msg_content.setVisibility(View.GONE);
        }else {
            passwordContainer.setVisibility(View.GONE);
            msg_content.setVisibility(View.INVISIBLE);
        }
        progressBar.setVisibility(View.VISIBLE);
        if (mCallback != null) {
            mCallback.onInputCompleted(isPassword,password);
        }
    }

    @Override
    public void onInput(String character) {
        if (PasswordKeyboard.DEL.equals(character)) {
            if (mPasswordBuffer.length() > 0) {
                mPasswordBuffer.delete(mPasswordBuffer.length() - 1, mPasswordBuffer.length());
            }
        } else if (PasswordKeyboard.DONE.equals(character)) {
            dismiss();
        } else {
            if (!passwordState) {
                if (!TextUtils.isEmpty(errorMsgTv.getText())) {
                    errorMsgTv.setText("");
                }
            }
            mPasswordBuffer.append(character);
        }
        if (isPassword) {
            passwordView.setPassword(mPasswordBuffer);
            if (mPasswordBuffer.length() == passwordView.getPasswordCount()) {
                startLoading(mPasswordBuffer);
            }
        }else {
            msg_inputBox.setPassword(mPasswordBuffer);
            if (mPasswordBuffer.length() == msg_inputBox.getPasswordCount()) {
                startLoading(mPasswordBuffer);
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mPasswordBuffer.length() > 0) {
            mPasswordBuffer.delete(0, mPasswordBuffer.length());
        }
        msg_password.onDestroy();
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }
    private void sendMsg(){
        Map map=new TreeMap();
//        HttpMethods.getInstance().sendGsSms(new Gson().toJson(map),new Observer<HttpResult>() {
//            @Override
//            public void onError(Throwable e) {
//                ToastUtils.showShort(getContext(),e.getMessage());
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//
//            @Override
//            public void onSubscribe(@NonNull Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(HttpResult model) {
//                ToastUtils.showShort(getContext(),model.getMsg());
//                if (model.getStatus()== HttpCode.SUCCEED){
//                    msg_password.setText("验证码已发送！重新发送");//设置倒计时开始时按钮上的显示文字
//                    msg_password.setStartCountDownText("验证码已发送！重新发送");//设置倒计时开始时按钮上的显示文字
//                    msg_password.startCountDownTimer(60000,1000);//设置倒计时时间，间隔
//                }
//            }
//        });
    }
}
