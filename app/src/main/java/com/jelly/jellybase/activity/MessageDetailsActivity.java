package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.base.BaseApplication;
import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.jelly.baselibrary.model.Message;
import com.jelly.baselibrary.mprogressdialog.MProgressUtil;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.MessageDetailsActivityBinding;
import com.jelly.mvp.contact.MessageDetailsContact;
import com.jelly.mvp.presenter.MessageDetailsPresenter;
import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.ig.DefaultImageDownloader;

import java.util.Map;
import java.util.TreeMap;

/**
 * 消息通知详情
 */
public class MessageDetailsActivity extends BaseActivityImpl<MessageDetailsContact.View
        , MessageDetailsContact.Presenter, MessageDetailsActivityBinding>
        implements MessageDetailsContact.View, View.OnClickListener {
    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message = (Message) getIntent().getSerializableExtra("message");
        if (message == null) {
            finish();
            return;
        }
        iniView();
    }

    @Override
    public MessageDetailsContact.Presenter initPresenter() {
        return new MessageDetailsPresenter();
    }

    @Override
    public MessageDetailsContact.View initIBView() {
        return this;
    }

    @Override
    public LifecycleOwner bindLifecycle() {
        return this;
    }

    private void iniView() {
        MProgressUtil.getInstance().initialize(this);
        getBinding().leftBack.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_back:
                finish();
                break;
        }
    }

    @Override
    public void showProgress() {
        MProgressUtil.getInstance().show(this);
    }

    @Override
    public void closeProgress() {
        MProgressUtil.getInstance().dismiss();
    }

    @Override
    public Object getMessageDetailsParam() {
        Map map = new TreeMap();
        map.put("protocol_type", 1);
        return map;
    }

    @Override
    public void getMessageDetailsSuccess(Object mCallBackVo) {
        Message message = (Message) mCallBackVo;
        if (message == null) return;
        // 设置为Html
        RichText.initCacheDir(BaseApplication.getInstance());
        RichText.fromHtml(message.toString())
                .bind(this)
                .autoFix(true) // 是否自动修复，默认true
                .resetSize(true) // 默认false，是否忽略img标签中的宽高尺寸（只在img标签中存在宽高时才有效）
                // ，true：忽略标签中的尺寸并触发SIZE_READY回调，false：使用img标签中的宽高尺寸，不触发SIZE_READY回调
                .imageDownloader(new DefaultImageDownloader()) // 设置DefaultImageGetter的图片下载器
                .cache(CacheType.all)//默认为CacheType.ALL
                .into(getBinding().messageDetailTv);
    }

    @Override
    public void getMessageDetailsFailed(String message) {
        ToastUtils.showToast(this, message);
    }
}
