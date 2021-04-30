package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.EvaluateActivityBinding;

/**
 * Created by Administrator on 2017/10/11.
 */

public class EvaluateActivity extends BaseActivity<EvaluateActivityBinding> implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }

    private void iniView(){
        getBinding().leftBack.setOnClickListener(this);
        getBinding().topRight.setOnClickListener(this);
        getBinding().describeRatingBar.setRating(4);
        getBinding().logisticsRatingBar.setRating(4);
        getBinding().storeRatingBar.setRating(4);
//        describeRatingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
//            @Override
//            public void onRatingChange(BaseRatingBar baseRatingBar, float v) {
//
//            }
//        });
    }
    public void onClick(View v) {
        if (AntiShake.check(v.getId()))return;
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.top_right:
                String contents= getBinding().contentEd.getText().toString();
                if (TextUtils.isEmpty(contents)){
                    ToastUtils.showToast(this,"请输入评价内容");
                    return;
                }
                break;
        }
    }
}
