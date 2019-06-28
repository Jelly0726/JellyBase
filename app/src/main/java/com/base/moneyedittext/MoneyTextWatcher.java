package com.base.moneyedittext;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 *
 *描述   ：金额输入字体监听类，限制小数点后输入位数
 *
 * 默认限制小数点2位
 * 默认第一位输入小数点时，转换为0.
 * 如果起始位置为0,且第二位跟的不是".",则无法后续输入
 *
 *  默认两位小数
 mEditText.addTextChangedListener(new MoneyTextWatcher(mEditText1));
 手动设置其他位数，例如3
 mEditText.addTextChangedListener(new MoneyTextWatcher(mEditText1).setDigits(3);
 */
public class MoneyTextWatcher implements TextWatcher {
    private EditText editText;
    private int digits = 2;

    public MoneyTextWatcher(EditText et) {
        editText = et;
    }

    /**
     *
     * @param d 手动设置其他位数，若为0 则不能输入"."
     * @return
     */
    public MoneyTextWatcher setDigits(int d) {
        digits = d;
        return this;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (digits > 0) {
            //删除“.”后面超过2位后的数据
            if (s.toString().contains(".")) {
                if (s.length() - 1 - s.toString().indexOf(".") > digits) {
                    s = s.toString().subSequence(0,
                            s.toString().indexOf(".") + digits+1);
                    editText.setText(s);
                    editText.setSelection(s.length()); //光标移到最后
                }
            }
            //如果"."在起始位置,则起始位置自动补0
            if (s.toString().trim().substring(0).equals(".")) {
                s = "0" + s;
                editText.setText(s);
                editText.setSelection(2);
            }

            //如果起始位置为0,且第二位跟的不是".",则无法后续输入
            if (s.toString().startsWith("0")
                    && s.toString().trim().length() > 1) {
                if (!s.toString().substring(1, 2).equals(".")) {
                    editText.setText(s.subSequence(0, 1));
                    editText.setSelection(1);
                    return;
                }
            }
            //如果已经包含"."，则不能再输入"."
            if (s.toString().substring(s.toString().indexOf(".")+1,s.length()).contains(".")){
                s= s.toString().substring(0,s.toString().indexOf(".")+1)
                        +(s.toString().substring(s.toString().indexOf(".")+1,s.length()).replace(".", ""));
                editText.setText(s);
                editText.setSelection(s.length()); //光标移到最后
                return;
            }
        }else {
            //如果小数限制为0则输入".",则无法后续输入
            //删除“.”后面超过2位后的数据
            if (s.toString().contains(".")) {
                    s = s.toString().subSequence(0,
                            s.toString().indexOf("."));
                    editText.setText(s);
                    editText.setSelection(s.length()); //光标移到最后
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}