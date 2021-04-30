package com.jelly.jellybase.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.applicationUtil.AppUtils;
import com.jelly.baselibrary.nativeUtil.NativeUtils;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.Singmd5ActivityBinding;

public class SingMD5Activity extends BaseActivity<Singmd5ActivityBinding> implements View.OnClickListener {

    private ClipboardManager mClipboardManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    private void iniView(){
        getBinding().leftBack.setOnClickListener(this);
        getBinding().thisApkBtn.setOnClickListener(this);
        getBinding().thisApkCopy.setOnClickListener(this);
        getBinding().thisInfoBtn.setOnClickListener(this);
        getBinding().otherApkBtn.setOnClickListener(this);
        getBinding().otherApkCopy.setOnClickListener(this);
        getBinding().otherInfoBtn.setOnClickListener(this);
        getBinding().ndkBtn.setOnClickListener(this);
        getBinding().ndkCopy.setOnClickListener(this);
        mClipboardManager =(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_back:
                finish();
            case R.id.thisApk_btn:
                getBinding().thisApkTv.setText(AppUtils.getSignMd5Str(this));
                break;
            case R.id.thisInfo_btn:
                getBinding().thisApkTv.setText(AppUtils.getSign(this));
                break;
            case R.id.thisApk_copy:
                String ssss= getBinding().thisApkTv.getText().toString();
                ClipData mClipData =ClipData.newPlainText("Label", ssss);         //‘Label’这是任意文字标签
                mClipboardManager.setPrimaryClip(mClipData);
                ToastUtils.showShort(this, "复制成功，可以发给朋友们了。");
                break;
            case R.id.otherApk_btn:
                String pa= getBinding().otherPackageEd.getText().toString().trim();
                if (TextUtils.isEmpty(pa)){
                    ToastUtils.showShort(this, "请输入应用包名。");
                    return;
                }
                getBinding().otherApkTv.setText(AppUtils.getSignMd5Str(this,pa));
                break;
            case R.id.otherApk_copy:
                String ss= getBinding().otherApkTv.getText().toString();
                ClipData mClipDat =ClipData.newPlainText("Label", ss);         //‘Label’这是任意文字标签
                mClipboardManager.setPrimaryClip(mClipDat);
                ToastUtils.showShort(this, "复制成功，可以发给朋友们了。");
                break;
            case R.id.otherInfo_btn:
                String pas= getBinding().otherPackageEd.getText().toString().trim();
                if (TextUtils.isEmpty(pas)){
                    ToastUtils.showShort(this, "请输入应用包名。");
                    return;
                }
                getBinding().otherApkTv.setText(AppUtils.getSign(this,pas));
                break;
            case R.id.ndk_copy:
                String s= getBinding().ndkTv.getText().toString();
                ClipData mClipDa =ClipData.newPlainText("Label", s);         //‘Label’这是任意文字标签
                mClipboardManager.setPrimaryClip(mClipDa);
                ToastUtils.showShort(this, "复制成功，可以发给朋友们了。");
                break;
            case R.id.ndk_btn:
                getBinding().ndkTv.setText(NativeUtils.getNativeString());
                break;
        }
    }
}
