package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.Utils.ToastUtils;
import com.base.applicationUtil.MyApplication;
import com.base.httpmvp.contact.MessageDetailsContact;
import com.base.httpmvp.databean.Message;
import com.base.httpmvp.presenter.MessageDetailsPresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.mprogressdialog.MProgressUtil;
import com.jelly.jellybase.R;
import com.maning.mndialoglibrary.MProgressDialog;
import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.ig.DefaultImageDownloader;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 消息通知详情
 */
public class MessageDetailsActivity extends BaseActivityImpl<MessageDetailsContact.Presenter>
        implements MessageDetailsContact.View{
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.messageDetail_tv)
    TextView messageDetail_tv;
    private MProgressDialog progressDialog;
    private Message message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message= (Message) getIntent().getSerializableExtra("message");
        if (message==null){
            finish();
            return;
        }
        setContentView(R.layout.message_details_activity);
        ButterKnife.bind(this);
        iniView();
    }

    @Override
    public MessageDetailsContact.Presenter initPresenter() {
        return new MessageDetailsPresenter(this) ;
    }

    private void iniView(){
        progressDialog= MProgressUtil.getInstance().getMProgressDialog(this);

    }
    @OnClick({R.id.left_back})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
        }
    }
    @Override
    public void showProgress() {
        if (progressDialog!=null){
            progressDialog.show();
        }
    }

    @Override
    public void closeProgress() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public Object getMessageDetailsParam() {
        Map map=new TreeMap();
        map.put("protocol_type",1);
        return map;
    }

    @Override
    public void getMessageDetailsSuccess(Object mCallBackVo) {
        Message message= (Message) mCallBackVo;
        if (message==null)return;
        // 设置为Html
        RichText.initCacheDir(MyApplication.getMyApp());
        RichText.fromHtml(message.toString())
                .bind(this)
                .autoFix(true) // 是否自动修复，默认true
                .resetSize(true) // 默认false，是否忽略img标签中的宽高尺寸（只在img标签中存在宽高时才有效）
                // ，true：忽略标签中的尺寸并触发SIZE_READY回调，false：使用img标签中的宽高尺寸，不触发SIZE_READY回调
                .imageDownloader(new DefaultImageDownloader()) // 设置DefaultImageGetter的图片下载器
                .cache(CacheType.all)//默认为CacheType.ALL
                .into(messageDetail_tv);
    }

    @Override
    public void getMessageDetailsFailed(String message) {
        ToastUtils.showToast(this,message);
    }
}
