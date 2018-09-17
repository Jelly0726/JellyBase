package com.base.passwordView;

/**
 * Created by chenjiawei on 16/8/26.
 */
public interface Callback {

    void onForgetPassword();


    /**
     *
     * @param type  true 支付密码  false短信验证码
     * @param password
     */
    void onInputCompleted(boolean type,CharSequence password);

    void onPasswordCorrectly();

    void onCancel();
}
