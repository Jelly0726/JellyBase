package com.jelly.jellybase.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jelly.baselibrary.applicationUtil.AppUtils;
import com.jelly.baselibrary.nativeUtil.NativeUtils;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.jellybase.R;

import butterknife.BindView;
import butterknife.OnClick;

public class SingMD5Activity extends BaseActivity{
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.thisApk_tv)
    TextView thisApk_tv;
    @BindView(R.id.thisApk_btn)
    Button thisApk_btn;
    @BindView(R.id.thisInfo_btn)
    Button thisInfo_btn;
    @BindView(R.id.thisApk_copy)
    Button thisApk_copy;

    @BindView(R.id.otherPackage_ed)
    EditText otherPackage_ed;
    @BindView(R.id.otherApk_btn)
    Button otherApk_btn;
    @BindView(R.id.otherInfo_btn)
    Button otherInfo_btn;
    @BindView(R.id.otherApk_copy)
    Button otherApk_copy;
    @BindView(R.id.otherApk_tv)
    TextView otherApk_tv;
    @BindView(R.id.ndk_btn)
    Button ndk_btn;
    @BindView(R.id.ndk_copy)
    Button ndk_copy;
    @BindView(R.id.ndk_tv)
    TextView ndk_tv;

    private ClipboardManager mClipboardManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.singmd5_activity;
    }
    private void iniView(){
        mClipboardManager =(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
    }
    @OnClick({R.id.left_back,R.id.thisApk_btn,R.id.thisApk_copy,R.id.thisInfo_btn,R.id.otherApk_btn
            ,R.id.otherApk_copy,R.id.otherInfo_btn,R.id.ndk_copy,R.id.ndk_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_back:
                finish();
            case R.id.thisApk_btn:
                thisApk_tv.setText(AppUtils.getSignMd5Str(this));
                break;
            case R.id.thisInfo_btn:
                thisApk_tv.setText(AppUtils.getSign(this));
                break;
            case R.id.thisApk_copy:
                String ssss=thisApk_tv.getText().toString();
                ClipData mClipData =ClipData.newPlainText("Label", ssss);         //‘Label’这是任意文字标签
                mClipboardManager.setPrimaryClip(mClipData);
                ToastUtils.showShort(this, "复制成功，可以发给朋友们了。");
                break;
            case R.id.otherApk_btn:
                String pa=otherPackage_ed.getText().toString().trim();
                if (TextUtils.isEmpty(pa)){
                    ToastUtils.showShort(this, "请输入应用包名。");
                    return;
                }
                otherApk_tv.setText(AppUtils.getSignMd5Str(this,pa));
                break;
            case R.id.otherApk_copy:
                String ss=otherApk_tv.getText().toString();
                ClipData mClipDat =ClipData.newPlainText("Label", ss);         //‘Label’这是任意文字标签
                mClipboardManager.setPrimaryClip(mClipDat);
                ToastUtils.showShort(this, "复制成功，可以发给朋友们了。");
                break;
            case R.id.otherInfo_btn:
                String pas=otherPackage_ed.getText().toString().trim();
                if (TextUtils.isEmpty(pas)){
                    ToastUtils.showShort(this, "请输入应用包名。");
                    return;
                }
                otherApk_tv.setText(AppUtils.getSign(this,pas));
                break;
            case R.id.ndk_copy:
                String s=ndk_tv.getText().toString();
                ClipData mClipDa =ClipData.newPlainText("Label", s);         //‘Label’这是任意文字标签
                mClipboardManager.setPrimaryClip(mClipDa);
                ToastUtils.showShort(this, "复制成功，可以发给朋友们了。");
                break;
            case R.id.ndk_btn:
                ndk_tv.setText(NativeUtils.getNativeString());
                break;
        }
    }
}
