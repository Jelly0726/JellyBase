package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.base.multiClick.AntiShake;
import com.base.toast.ToastUtils;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;
import com.willy.ratingbar.BaseRatingBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/11.
 */

public class EvaluateActivity extends BaseActivity {
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.top_right)
    LinearLayout top_right;
    @BindView(R.id.describeRatingBar)
    BaseRatingBar describeRatingBar;
    @BindView(R.id.logisticsRatingBar)
    BaseRatingBar logisticsRatingBar;
    @BindView(R.id.storeRatingBar)
    BaseRatingBar storeRatingBar;
    @BindView(R.id.content_ed)
    EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluate_activity);
        ButterKnife.bind(this);
        iniView();
    }


    private void iniView(){
        describeRatingBar.setRating(4);
        logisticsRatingBar.setRating(4);
        storeRatingBar.setRating(4);
//        describeRatingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
//            @Override
//            public void onRatingChange(BaseRatingBar baseRatingBar, float v) {
//
//            }
//        });
    }
    @OnClick({R.id.left_back,R.id.top_right})
    public void onClick(View v) {
        if (AntiShake.check(v.getId()))return;
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.top_right:
                String contents=content.getText().toString();
                if (TextUtils.isEmpty(contents)){
                    ToastUtils.showToast(this,"请输入评价内容");
                    return;
                }
                break;
        }
    }
}
