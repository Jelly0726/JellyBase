package com.base.circledialog.view;

import android.content.Context;
import android.os.Build;
import android.text.InputFilter;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.base.circledialog.params.ButtonParams;
import com.base.circledialog.params.CircleParams;
import com.base.circledialog.params.DialogParams;
import com.base.circledialog.params.InputParams;
import com.base.circledialog.params.TitleParams;
import com.base.circledialog.res.drawable.CircleDrawable;
import com.base.circledialog.res.drawable.InputDrawable;
import com.base.circledialog.res.values.CircleColor;
import com.base.moneyedittext.MoneyValueFilter;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by hupei on 2017/3/31.
 */

class BodyInputView extends ScaleLinearLayout {
    private ScaleEditText mEditText;

    public BodyInputView(Context context, CircleParams params) {
        super(context);
        init(context, params);
    }

    private void init(Context context, CircleParams params) {
        DialogParams dialogParams = params.dialogParams;
        TitleParams titleParams = params.titleParams;
        InputParams inputParams = params.inputParams;
        ButtonParams negativeParams = params.negativeParams;
        ButtonParams positiveParams = params.positiveParams;

        //如果标题没有背景色，则使用默认色
        int backgroundColor = inputParams.backgroundColor != 0 ? inputParams.backgroundColor :
                CircleColor.bgDialog;

        //有标题没按钮则底部圆角
        if (titleParams != null && negativeParams == null && positiveParams == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(new CircleDrawable(backgroundColor, 0, 0, dialogParams.radius,
                        dialogParams.radius));
            } else {
                setBackgroundDrawable(new CircleDrawable(backgroundColor, 0, 0, dialogParams
                        .radius, dialogParams.radius));
            }
        }
        //没标题有按钮则顶部圆角
        else if (titleParams == null && (negativeParams != null || positiveParams != null)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(new CircleDrawable(backgroundColor, dialogParams.radius, dialogParams
                        .radius, 0, 0));
            } else {
                setBackgroundDrawable(new CircleDrawable(backgroundColor, dialogParams.radius,
                        dialogParams.radius, 0, 0));
            }
        }
        //没标题没按钮则全部圆角
        else if (titleParams == null && negativeParams == null && positiveParams == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(new CircleDrawable(backgroundColor, dialogParams.radius));
            } else {
                setBackgroundDrawable(new CircleDrawable(backgroundColor, dialogParams.radius));
            }
        }
        //有标题有按钮则不用考虑圆角
        else setBackgroundColor(backgroundColor);

        mEditText = new ScaleEditText(context);
        mEditText.setHint(inputParams.hintText);
        mEditText.setHintTextColor(inputParams.hintTextColor);
        mEditText.setTextSize(inputParams.textSize);
        mEditText.setTextColor(inputParams.textColor);
        mEditText.setHeight(inputParams.inputHeight);
        mEditText.setText(inputParams.text);
        mEditText.setSelection(inputParams.text.length());
        int[] paddings=inputParams.paddings;
        mEditText.setPadding(paddings[0],paddings[1],paddings[2],paddings[3]);
        if (inputParams.type==InputParams.INPUT_MONEY){
            mEditText.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(inputParams.digits)});
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
                       {
                           public void run()
                           {
                               InputMethodManager inputManager =
                                       (InputMethodManager)mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(mEditText, 0);
                           }
                       },
                500);

        int backgroundResourceId = inputParams.inputBackgroundResourceId;
        if (backgroundResourceId == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mEditText.setBackground(new InputDrawable(inputParams.strokeWidth, inputParams
                        .strokeColor, inputParams.inputBackgroundColor));
            } else {
                mEditText.setBackgroundDrawable(new InputDrawable(inputParams.strokeWidth,
                        inputParams.strokeColor, inputParams.inputBackgroundColor));
            }
        } else mEditText.setBackgroundResource(backgroundResourceId);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        int[] margins = inputParams.margins;
        if (margins != null) {
            layoutParams.setMargins(margins[0], margins[1], margins[2], margins[3]);
        }
        addView(mEditText, layoutParams);
    }

    public EditText getInput() {
        return mEditText;
    }
}
